package de.redtec.tileentity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import de.redtec.gui.ContainerNComputer;
import de.redtec.items.ItemHardDrive;
import de.redtec.typeregistys.ModTileEntityType;
import de.redtec.util.DriveManager;
import de.redtec.util.ElectricityNetworkHandler;
import de.redtec.util.ElectricityNetworkHandler.ElectricityNetwork;
import de.redtec.util.IElectricConnective.Voltage;
import de.redtec.util.INetworkDevice;
import de.redtec.util.INetworkDevice.NetworkDeviceIP;
import de.redtec.util.INetworkDevice.NetworkMessage;
import de.redtec.util.LuaInterpreter;
import de.redtec.util.LuaInterpreter.ILuaThreadViolating;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class TileEntityNComputer extends TileEntityInventoryBase implements INamedContainerProvider, ITickableTileEntity, ILuaThreadViolating {
	
	public static final String BOOT_DRIVE_FILE = "/boot.lua";
	
	// Temp variables
	protected short powerStartupTime;
	protected String cachedBootCode;
	protected short driveValidationTime;
	protected LuaInterpreter luaInterpreter;
	protected ByteArrayOutputStream outputStream;
	protected List<NetworkMessage> recivedMessages;
	
	public boolean isRunning;
	public boolean hasPower;
	public String consoleLine;
	public NetworkDeviceIP deviceIP;
	

	// "computer" LUA API
	protected class computer extends TwoArgFunction {
		
		// "sendMessage" (byte[] targetIP, ... args)
		final class sendMessage extends TwoArgFunction  {
			@Override
			public LuaValue call(LuaValue targetIP, LuaValue argTable) {
				LuaTable ip = targetIP.checktable();
				LuaTable args = argTable.checktable();
				NetworkDeviceIP nip = new NetworkDeviceIP((byte) ip.get(0).checkint(), (byte) ip.get(1).checkint(), (byte) ip.get(2).checkint(), (byte) ip.get(3).checkint());
				NetworkMessage msg = new NetworkMessage();
				msg.setTargetIP(nip);
				for (int i = 0; i < args.length(); i++) {
					LuaValue arg = args.get(i);
					if (arg.isboolean()) {
						msg.getDataBuffer().writeBoolean(arg.checkboolean());
					} else if (arg.isint()) {
						msg.getDataBuffer().writeInt(arg.checkint());
					} else if (arg.isstring()) {
						msg.getDataBuffer().writeString(arg.tostring().toString());
					}
				}
				TileEntityNComputer.this.sendMessage(msg);
				return LuaValue.NONE;
			}
		}
		// "pullMessage"
		final class pullMessage extends ZeroArgFunction {
			@Override
			public LuaValue call() {
				if (TileEntityNComputer.this.recivedMessages.size() > 0) {
					NetworkMessage message = TileEntityNComputer.this.recivedMessages.get(0);
					TileEntityNComputer.this.recivedMessages.remove(0);
					LuaTable table = new LuaTable();
					LuaTable ipTable = new LuaTable();
					for (byte b : message.getSenderIP().getIP()) {
						ipTable.add(b);
					}
					table.add(ipTable);
					LuaTable ip2Table = new LuaTable();
					for (byte b : message.getTargetIP().getIP()) {
						ip2Table.add(b);
					}
					table.add(ip2Table);
					for (Object arg : message.getArgs()) {
						if (arg instanceof Integer) {
							table.add(LuaValue.valueOf((int) arg));
						} else if (arg instanceof String) {
							table.add(LuaValue.valueOf((String) arg));
						} else if (arg instanceof Boolean) {
							table.add(LuaValue.valueOf((Boolean) arg));
						}
					}
					return table;
				}
				return LuaValue.NIL;
			}
		}
		// "restart"
		final class restart extends ZeroArgFunction {
			@Override
			public LuaValue call() {
				TileEntityNComputer.this.restartComputer();
				return LuaValue.NONE;
			}
		}
		// "shutdown"
		final class shutdown extends ZeroArgFunction {
			@Override
			public LuaValue call() {
				TileEntityNComputer.this.stopComputer();
				return LuaValue.NONE;
			}
		}
		
		@Override
		public LuaValue call(LuaValue modname, LuaValue env) {
			LuaValue library = tableOf();
			library.set("skip", new sendMessage());
			library.set("shutdown", new shutdown());
			library.set("restart", new restart());
			env.set("computer", library);
			return env;
		}
		
	}
	
	public TileEntityNComputer() {
		super(ModTileEntityType.COMPUTER, 2);
		this.outputStream = new ByteArrayOutputStream();
		this.recivedMessages = new ArrayList<INetworkDevice.NetworkMessage>();
		this.luaInterpreter = new LuaInterpreter(this, this.outputStream, new computer());
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-2, -1, -2), pos.add(2, 2, 2));
	}
	
	@Override
	public void tick() {
		
		if (!this.world.isRemote()) {
			
			ElectricityNetworkHandler.getHandlerForWorld(world).updateNetwork(world, pos);
			ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(world).getNetwork(pos);
			this.hasPower = network.canMachinesRun() == Voltage.LowVoltage;
			this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
			
			this.driveValidationTime++;
			if (this.driveValidationTime > 20) {
				this.driveValidationTime = 0;
				loadCodeFromDisk();
			}
			
			if (this.isRunning) {
				
				if (this.powerStartupTime < 4) this.powerStartupTime++;
				if (this.getBootDrive() == null || (!this.hasPower && this.powerStartupTime >= 4)) {
					this.stopComputer();
					if (!this.hasPower) this.consoleLine = "Power outage!";
					return;
				}
				
				if (!this.luaInterpreter.isCodeRunning() && this.cachedBootCode != null) {
					this.luaInterpreter.executeCode(this.cachedBootCode);
				}
				
				if (this.cachedBootCode != null) {
					
					int response = this.luaInterpreter.checkExecutationState();
					
					if (response == -1) {
						this.isRunning = false;
						this.consoleLine = this.luaInterpreter.getCrashMessage();
					} else if (response == 0) {
						this.isRunning = false;
						this.consoleLine = "Sequenze complete!";
					} else if (response == 1) {
						byte[] consoleOutput = this.outputStream.toByteArray();
						this.outputStream.reset();
						String[] lines = new String(consoleOutput).split("\n");
						String line = lines[lines.length -1];
						if (line.length() > 0) this.consoleLine = line.substring(0, line.length() - 1);
					}
					
				}
				
			} else {
				this.powerStartupTime = 0;
			}
			
		}
		
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isViolating() {
		return this.world.isBlockLoaded(getPos()) && this.world.getServer().isServerRunning();
	}
	
	@Override
	public void remove() {
		super.remove();
		this.luaInterpreter.stopExecuting();
	}
	
	public void startComputer() {
		if (this.hasPower && !this.isRunning) {
			this.isRunning = true;
			this.consoleLine = "";
			this.luaInterpreter.executeCode(this.cachedBootCode);
		}
	}
	
	public void stopComputer() {
		this.luaInterpreter.stopExecuting();
		this.isRunning = false;
		this.consoleLine = "Programm terminated";
	}
	
	public void restartComputer() {
		this.luaInterpreter.stopExecuting();
		this.startComputer();
	}
	
	public boolean isComputerRunning() {
		return isRunning;
	}
	
	public String getCachedBootCode() {
		return cachedBootCode == null ? "" : this.cachedBootCode;
	}
	
	public String getConsoleLine() {
		return consoleLine;
	}
	
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerNComputer(id, playerInv, this);
	}

	public boolean hasPower() {
		return this.hasPower;
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.redtec.computer");
	}
	
	public String getBootDrive() {
		if (this.world.isRemote()) throw new IllegalStateException("Cant get DriveFolder on client!");
		ItemStack stack = this.getStackInSlot(0);
		return ItemHardDrive.getDriveName(stack, (ServerWorld) this.world);
	}
	
	public String getDriveSlot() {
		if (this.world.isRemote()) throw new IllegalStateException("Cant get DriveFolder on client!");
		ItemStack stack = this.getStackInSlot(1);
		return ItemHardDrive.getDriveName(stack, (ServerWorld) this.world);
	}
	
	public void onClientUpdate(boolean runClicked, boolean saveClicked, String[] code) {
		
		if (this.world.isRemote()) throw new IllegalStateException("Cant access Drives on client!");
		
		if (saveClicked) {
			String drive = getBootDrive();
			if (drive != null) {
				StringBuilder sb = new StringBuilder();
				for (String s : code) {
					sb.append(s).append("\n");
				}
				DriveManager.saveDataInDrive(drive + BOOT_DRIVE_FILE, sb.toString(), (ServerWorld) this.world);
				
			}
		}
		
		if (runClicked) {
			if (isComputerRunning()) {
				this.stopComputer();
			} else {
				this.startComputer();
			}
		}
		
	}
	
	public void loadCodeFromDisk() {
		
		if (this.world.isRemote()) throw new IllegalStateException("Cant access Drives on client!");
		
		this.cachedBootCode = "";
		String bootDriveName = getBootDrive();
		if (bootDriveName != null) {
			this.cachedBootCode = DriveManager.loadDataFromDrive(bootDriveName + BOOT_DRIVE_FILE, (ServerWorld) this.world);
		}
		
	}
	
	@Override
	public void read(BlockState state, CompoundNBT compound) {
		this.deviceIP = NetworkDeviceIP.read(compound.getCompound("DeviceIP"));
		this.hasPower = compound.getBoolean("hasPower");
		this.isRunning = compound.getBoolean("IsRunning");
		this.consoleLine = compound.getString("Console");
		super.read(state, compound);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.put("DeviceIP", this.deviceIP.writeNBT());
		compound.putBoolean("hasPower", this.hasPower);
		compound.putBoolean("IsRunning", this.isRunning);
		if (this.consoleLine != null) compound.putString("Console", this.consoleLine);
		return super.write(compound);
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		if (this.cachedBootCode != null) nbt.putString("Code", this.cachedBootCode);
		nbt.putBoolean("IsRunning", this.isRunning);
		nbt.putBoolean("hasPower", this.hasPower);
		if (this.consoleLine != null) nbt.putString("Console", this.consoleLine);
		return new SUpdateTileEntityPacket(this.pos, 0, nbt);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT nbt = pkt.getNbtCompound();
		if (nbt.contains("Code")) this.cachedBootCode = nbt.getString("Code");
		this.isRunning = nbt.getBoolean("IsRunning");
		this.hasPower = nbt.getBoolean("hasPower");
		this.consoleLine = nbt.getString("Console");
	}
	
	public void onMessageRecived(NetworkMessage message) {
		if (this.recivedMessages.size() < 16) {
			this.recivedMessages.add(message);
		}
	}
	
	public void sendMessage(NetworkMessage msg) {
		BlockState state = this.getBlockState();
		INetworkDevice device = state.getBlock() instanceof INetworkDevice ? (INetworkDevice) state.getBlock() : null;
		if (device != null) {
			device.sendMessage(msg, world, pos, state);
		}
	}
	
}
