package de.industria.tileentity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import de.industria.gui.ContainerNComputer;
import de.industria.items.ItemHardDrive;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.LuaInterpreter;
import de.industria.util.LuaInterpreter.ILuaThreadViolating;
import de.industria.util.blockfeatures.INetworkDevice;
import de.industria.util.blockfeatures.IElectricConnectiveBlock.Voltage;
import de.industria.util.blockfeatures.INetworkDevice.NetworkDeviceIP;
import de.industria.util.blockfeatures.INetworkDevice.NetworkMessage;
import de.industria.util.handler.DriveManager;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
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
	protected List<NetworkMessage> sendMessages;
	protected HashMap<NetworkDeviceIP, NetworkMessage> recivedMessages;
	protected NetworkDeviceIP lastReciveTarget;
	
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
				if (TileEntityNComputer.this.sendMessages.size() < 16) {
					LuaString ip = targetIP.checkstring();
					String[] ipS = ip.toString().split("\\.");
					LuaTable args = argTable.checktable();
					NetworkDeviceIP nip = new NetworkDeviceIP(Byte.valueOf(ipS[0]), Byte.valueOf(ipS[1]), Byte.valueOf(ipS[2]), Byte.valueOf(ipS[3]));
					NetworkMessage msg = new NetworkMessage();
					msg.setSendTime(TileEntityNComputer.this.world.getGameTime());
					msg.setTargetIP(nip);
					for (int i = 1; i <= args.length(); i++) {
						LuaValue arg = args.get(i);
						if (arg.isboolean()) {
							msg.writeBoolean(arg.checkboolean());
						} else if (arg.isint()) {
							msg.writeInt(arg.checkint());
						} else if (arg.isstring()) {
							msg.writeString(arg.tostring().toString());
						}
					}
					if (TileEntityNComputer.this.sendMessages.size() > 120) TileEntityNComputer.this.sendMessages.clear();
					TileEntityNComputer.this.sendMessages.add(msg);
				}
				return LuaValue.NONE;
			}
		}
		// "pullMessage"
		final class pullMessage extends VarArgFunction {
			@Override
			public Varargs invoke(Varargs args) {
				if (TileEntityNComputer.this.recivedMessages.size() > 0) {
					
					NetworkDeviceIP oldestIP = null;
					long priority = 0L;
					for (Entry<NetworkDeviceIP, NetworkMessage> entry : TileEntityNComputer.this.recivedMessages.entrySet()) {
						long msgPri = TileEntityNComputer.this.world.getGameTime() - entry.getValue().getSendTime();
						if (msgPri > priority) {
							priority = msgPri;
							oldestIP = entry.getKey();
						}
					}
					
					if (oldestIP != null) {
						
						NetworkMessage message = TileEntityNComputer.this.recivedMessages.remove(oldestIP);
						
						List<LuaValue> table = new ArrayList<LuaValue>();
						
						table.add(LuaValue.valueOf(message.getSenderIP().getIP()[0] + "." + message.getSenderIP().getIP()[1] + "." + message.getSenderIP().getIP()[2] + "." + message.getSenderIP().getIP()[3]));
						table.add(LuaValue.valueOf(message.getTargetIP().getIP()[0] + "." + message.getTargetIP().getIP()[1] + "." + message.getTargetIP().getIP()[2] + "." + message.getTargetIP().getIP()[3]));
						
						for (Object arg : message.getArgs()) {
							if (arg instanceof Integer) {
								table.add(LuaValue.valueOf((int) arg));
							} else if (arg instanceof String) {
								table.add(LuaValue.valueOf((String) arg));
							} else if (arg instanceof Boolean) {
								table.add(LuaValue.valueOf((Boolean) arg));
							}
						}
						
						Varargs ret = LuaValue.varargsOf(table.toArray(new LuaValue[] {}));
						return ret;
					}
				}
				return LuaValue.varargsOf(new LuaValue[] {LuaValue.NIL});
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
		// "sleep"
		final class sleep extends OneArgFunction {
			@Override
			public LuaValue call(LuaValue milis) {
				int m = milis.checkint();
				try {
					TileEntityNComputer.this.luaInterpreter.validateTime();
					Thread.sleep(m);
				} catch (InterruptedException e) {}
				return LuaValue.NONE;
			}
		}
		// "loadFile" (String path)
		final class loadFile extends OneArgFunction {
			@Override
			public LuaValue call(LuaValue path) {
				String discPath = path.checkstring().toString();
				if (DriveManager.containsDrive(discPath, TileEntityNComputer.this.getBootDrive(), TileEntityNComputer.this.getDriveSlot())) {
					String script = DriveManager.loadDataFromDrive(discPath, (ServerWorld) world);
					String name = discPath.split("/")[discPath.split("/").length - 1];
					return TileEntityNComputer.this.luaInterpreter.loadScript(script, name);
				}
				return LuaValue.NONE;
			}
		}
		// "readFile" (String path)
		final class readFile extends OneArgFunction {
			@Override
			public LuaValue call(LuaValue path) {
				String discPath = path.checkstring().toString();
				if (DriveManager.containsDrive(discPath, TileEntityNComputer.this.getBootDrive(), TileEntityNComputer.this.getDriveSlot())) {
					String file = DriveManager.loadDataFromDrive(discPath, (ServerWorld) world);
					return LuaValue.valueOf(new String(file));
				}
				return LuaValue.NIL;
			}
		}	
		// "writeFile" (String path, String content)
		final class writeFile extends TwoArgFunction {
			@Override
			public LuaValue call(LuaValue path, LuaValue content) {
				String discPath = path.checkstring().toString();
				if (DriveManager.containsDrive(discPath, TileEntityNComputer.this.getBootDrive(), TileEntityNComputer.this.getDriveSlot())) {
					String file = content.checkstring().toString();
					DriveManager.saveDataInDrive(discPath, file, (ServerWorld) world);
				}
				return LuaValue.NONE;
			}
		}
		protected LuaValue env;
		
		@Override
		public LuaValue call(LuaValue modname, LuaValue env) {
			LuaValue library = tableOf();
			this.env = env;
			library.set("sendMessage", new sendMessage());
			library.set("pullMessage", new pullMessage());
			library.set("shutdown", new shutdown());
			library.set("restart", new restart());
			library.set("sleep", new sleep());
			library.set("loadFile", new loadFile());
			library.set("writeFile", new writeFile());
			library.set("readFile", new readFile());
			env.set("computer", library);
			return env;
		}
		
	}
	
	public TileEntityNComputer() {
		super(ModTileEntityType.COMPUTER, 2);
		this.outputStream = new ByteArrayOutputStream();
		this.recivedMessages = new HashMap<NetworkDeviceIP, INetworkDevice.NetworkMessage>();;
		this.sendMessages = new ArrayList<INetworkDevice.NetworkMessage>();
		this.luaInterpreter = new LuaInterpreter(this, this.outputStream, 50, new computer());
		this.deviceIP = NetworkDeviceIP.DEFAULT;
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
					
					int response = this.luaInterpreter.updateExecutationState();
					
					byte[] consoleOutput = this.outputStream.toByteArray();
					this.outputStream.reset();
					String[] lines = new String(consoleOutput).split("\n");
					String line = lines[lines.length -1];
					if (line.length() > 0) this.consoleLine = line.substring(0, line.length() - 1);
					
					if (response == -1) {
						this.isRunning = false;
						this.consoleLine = this.luaInterpreter.getCrashMessage();
					} else if (response == 0) {
						this.isRunning = false;
					}
										
				}
				
				NetworkMessage message = null;
				long priority = 0L;
				for (NetworkMessage msg : this.sendMessages) {
					long msgPri = this.world.getGameTime() - msg.getSendTime();
					if (msgPri > priority) {
						priority = msgPri;
						message = msg;
					}
				}
				
				if (message != null) {
					this.sendMessage(message);
					this.sendMessages.remove(message);
				}
				
			} else {
				this.powerStartupTime = 0;
				this.sendMessages.clear();
				this.recivedMessages.clear();
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
		return new TranslationTextComponent("block.industria.computer");
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
		this.recivedMessages.put(message.getSenderIP(), message);
	}
	
	public void sendMessage(NetworkMessage msg) {
		BlockState state = this.getBlockState();
		INetworkDevice device = state.getBlock() instanceof INetworkDevice ? (INetworkDevice) state.getBlock() : null;
		if (device != null) {
			device.sendMessage(msg, world, pos, state);
		}
	}
	
}