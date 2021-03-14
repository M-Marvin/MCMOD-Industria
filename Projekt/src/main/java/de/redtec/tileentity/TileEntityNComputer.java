package de.redtec.tileentity;

import java.io.ByteArrayOutputStream;

import de.redtec.gui.ContainerNComputer;
import de.redtec.items.ItemHardDrive;
import de.redtec.typeregistys.ModTileEntityType;
import de.redtec.util.DriveManager;
import de.redtec.util.LuaInterpreter;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class TileEntityNComputer extends TileEntityInventoryBase implements INamedContainerProvider, ITickableTileEntity {
	
	public static final String BOOT_DRIVE_FILE = "/boot.lua";
	
	protected String cachedBootCode;
	protected short driveValidationTime;
	
	protected LuaInterpreter luaInterpreter;
	protected ByteArrayOutputStream outputStream;
	
	protected boolean isRunning;
	protected boolean hasPower;
	protected String consoleLine;
	
	public TileEntityNComputer() {
		super(ModTileEntityType.COMPUTER, 2);
		this.outputStream = new ByteArrayOutputStream();
		this.luaInterpreter = new LuaInterpreter(this::canCodeRun, this.outputStream);
	}

	@Override
	public void tick() {
		
		if (!this.world.isRemote()) {
			
			this.hasPower = true;
			this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
			
			this.driveValidationTime++;
			if (this.driveValidationTime > 20) {
				this.driveValidationTime = 0;
				loadCodeFromDisk();
			}
			
			if (this.isRunning) {

				if (this.getBootDrive() == null || !this.hasPower) {
					this.stopComputer();
					if (!this.hasPower) this.consoleLine = "Power outage!";
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
				
			}
			
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public boolean canCodeRun() {
		return this.world.isBlockLoaded(getPos()) && this.world.getServer().isServerRunning();
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
		this.hasPower = compound.getBoolean("hasPower");
		this.isRunning = compound.getBoolean("IsRunning");
		this.consoleLine = compound.getString("Console");
		super.read(state, compound);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putBoolean("hasPower", this.hasPower);
		compound.putBoolean("IsRunning", this.isRunning);
		compound.putString("Console", this.consoleLine);
		return super.write(compound);
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		if (this.cachedBootCode != null) nbt.putString("Code", this.cachedBootCode);
		nbt.putBoolean("IsRunning", this.isRunning);
		nbt.putString("Console", this.consoleLine);
		return new SUpdateTileEntityPacket(this.pos, 0, nbt);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT nbt = pkt.getNbtCompound();
		if (nbt.contains("Code")) this.cachedBootCode = nbt.getString("Code");
		this.isRunning = nbt.getBoolean("IsRunning");
		this.consoleLine = nbt.getString("Console");
	}
	
}
