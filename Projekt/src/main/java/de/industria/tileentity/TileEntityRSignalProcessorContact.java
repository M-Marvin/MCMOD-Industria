package de.industria.tileentity;

import java.util.HashMap;
import java.util.Map.Entry;

import de.industria.blocks.BlockRSignalProcessorContact;
import de.industria.items.ItemProcessor;
import de.industria.items.ItemProcessor.OperatorResult;
import de.industria.items.ItemProcessor.OperatorType;
import de.industria.typeregistys.ModItems;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.ITENetworkDevice;
import de.industria.util.blockfeatures.ITENetworkDevice.NetworkDeviceIP;
import de.industria.util.blockfeatures.ITENetworkDevice.NetworkMessage;
import de.industria.util.types.RedstoneControlSignal;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TileEntityRSignalProcessorContact extends TileEntity implements ITickableTileEntity, INamedContainerProvider {
	
	public NetworkDeviceIP deviceIP;
	public ItemStack processorStack;
	public HashMap<String, ItemProcessor.OperatorResult> variables = new HashMap<String, ItemProcessor.OperatorResult>();
	
	private boolean lastSuccessState;
	
	public TileEntityRSignalProcessorContact() {
		super(ModTileEntityType.SIGNAL_PROCESSOR);
		this.deviceIP = NetworkDeviceIP.DEFAULT;
		this.processorStack = ItemStack.EMPTY;
	}
	
	public ItemStack getProcessorStack() {
		return processorStack;
	}
	
	public boolean hasProcessor() {
		return !this.processorStack.isEmpty();
	}
	
	public boolean setProcessorStack(ItemStack processorStack) {
		if (processorStack.getItem() instanceof ItemProcessor) {
			this.processorStack = processorStack;
			return true;
		}
		return false;
	}
	
	public void removeProcessor() {
		this.processorStack = ItemStack.EMPTY;
	}
	
	public HashMap<String, ItemProcessor.OperatorResult> getVariables() {
		return variables;
	}
	
	public String[] getCode() {
		
		if (this.hasProcessor()) {
			return ((ItemProcessor) this.getProcessorStack().getItem()).getCodeLinesFromProcessor(this.getProcessorStack());
		}
		return null;
		
	}
	
	public void setInput(ItemStack chanel, boolean powered) {
		
		String name = chanel.getHoverName().getContents();
		if (name.length() > 0) {
			
			this.variables.put(name, new OperatorResult(powered, OperatorType.BOOL));
			
		}
		
	}
	
	@Override
	public void tick() {
		
		if (!this.level.isClientSide) {
			
			if (this.hasProcessor()) {
				String[] codeLines = this.getCode();
				if (codeLines != null) {
					
					ItemProcessor processorItem = (ItemProcessor) this.getProcessorStack().getItem();
					this.lastSuccessState = processorItem.prozess(this.getProcessorStack(), this.variables);
									
				}
			}
			
			for (Entry<String, OperatorResult> variable : this.variables.entrySet()) {
				String name = variable.getKey();
				OperatorResult value = variable.getValue();
				
				if (value.getType() == OperatorType.BOOL) {
					
					ItemStack chanel = new ItemStack(Items.REDSTONE_TORCH);
					chanel.setHoverName(new StringTextComponent(name));
					RedstoneControlSignal signal = new RedstoneControlSignal(chanel, value.getBValue());
					
					BlockState state = this.level.getBlockState(this.worldPosition);
					if (state.getBlock() == ModItems.signal_processor_contact) {
						
						((BlockRSignalProcessorContact) state.getBlock()).sendSignal(this.level, this.worldPosition, signal);
						
					}
					
				}
				
			}
			
			this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 0);
			
		}
		
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT compound = this.serializeNBT();
		return new SUpdateTileEntityPacket(this.worldPosition, 0, compound);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.deserializeNBT(pkt.getTag());
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		if (hasProcessor()) {
			CompoundNBT processorNbt = this.processorStack.save(new CompoundNBT());
			compound.put("Processor", processorNbt);
		}
		compound.putBoolean("LastSuccessState", this.lastSuccessState);
		CompoundNBT bufferNBT = new CompoundNBT();
		for (Entry<String, OperatorResult> variable : this.variables.entrySet()) {
			if (variable.getValue() != OperatorResult.EMPTY && variable.getValue() != OperatorResult.FAIL) {
				CompoundNBT variableTag = new CompoundNBT();
				variableTag.putString("Type", variable.getValue().getType() == OperatorType.BOOL ? "Bool" : "Int");
				if (variable.getValue().isBool()) {
					variableTag.putBoolean("Value", variable.getValue().getBValue());
				} else {
					variableTag.putInt("Value", variable.getValue().getIValue());
				}
				bufferNBT.put(variable.getKey(), variableTag);
			}
		}
		compound.put("Memory", bufferNBT);
		compound.put("DeviceIP", this.deviceIP.writeNBT());
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		this.processorStack = ItemStack.EMPTY;
		if (compound.contains("Processor")) {
			ItemStack processorStack = ItemStack.of(compound.getCompound("Processor"));
			this.setProcessorStack(processorStack);
		}
		this.lastSuccessState = compound.getBoolean("LastSuccessState");
		CompoundNBT bufferNBT = compound.getCompound("Memory");
		this.variables.clear();
		for (String variableName : bufferNBT.getAllKeys()) {
			CompoundNBT variableTag = bufferNBT.getCompound(variableName);
			OperatorType type = variableTag.getString("Type").equals("Bool") ? OperatorType.BOOL : OperatorType.INT;
			if (type == OperatorType.BOOL) {
				boolean value = variableTag.getBoolean("Value");
				this.variables.put(variableName, new OperatorResult(value, type));
			} else {
				int value = variableTag.getInt("Value");
				this.variables.put(variableName, new OperatorResult(value, type));
			}
		}
		this.deviceIP = NetworkDeviceIP.read(compound.getCompound("DeviceIP"));
		super.load(state, compound);
	}
	
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity palyer) {
		
		if (hasProcessor()) {
			return ((ItemProcessor) this.getProcessorStack().getItem()).createContainer(id, playerInv, this);
		} else {
			return null;
		}
		
	}
	
	@Override
	public ITextComponent getDisplayName() {
		
		if (hasProcessor()) {
			return ((ItemProcessor) this.getProcessorStack().getItem()).getScreenTitle(this.getProcessorStack());
		} else {
			return new TranslationTextComponent(ModItems.signal_processor_contact.getDescriptionId());
		}
		
	}

	public void onMessageRecived(NetworkMessage message) {
		String cmdName = message.readString();
		if (cmdName.equals("resetMemory") && message.getArgCount() == 0) {
			this.variables.clear();
			sendResponse(message.getSenderIP(), "respReset", null, null);
		} else if (cmdName.equals("setBVariable") && message.getArgCount() == 2) {
			String variable = message.readString();
			boolean value = message.readBoolean();
			this.variables.put(variable, new OperatorResult(value, OperatorType.BOOL));
			sendResponse(message.getSenderIP(), "respSetB", null, null);
		} else if (cmdName.equals("setIVariable") && message.getArgCount() == 2) {
			String variable = message.readString();
			int value = message.readInt();
			this.variables.put(variable, new OperatorResult(value, OperatorType.INT));
			sendResponse(message.getSenderIP(), "respSetI", null, null);
		} else if (cmdName.equals("getBVariable") && message.getArgCount() == 1) {
			String variableName = message.readString();
			OperatorResult value = this.variables.getOrDefault(variableName, new OperatorResult(false, OperatorType.BOOL));
			if (value.getType() == OperatorType.BOOL) {
				sendResponse(message.getSenderIP(), "respGetB", variableName, value);
			}
		} else if (cmdName.equals("getIVariable") && message.getArgCount() == 1) {
			String variableName = message.readString();
			OperatorResult value = this.variables.getOrDefault(variableName, new OperatorResult(false, OperatorType.INT));
			if (value.getType() == OperatorType.INT) {
				sendResponse(message.getSenderIP(), "respGetI", variableName, value);
			}
		}
	}
	
	protected void sendResponse(NetworkDeviceIP targetIP, String cmd, String variableName, OperatorResult variable) {
		BlockState state = getBlockState();
		ITENetworkDevice device = state.getBlock() instanceof ITENetworkDevice ? (ITENetworkDevice) state.getBlock() : null;
		if (device != null) {
			NetworkMessage message = new NetworkMessage();
			message.setTargetIP(targetIP);
			message.writeString(cmd);
			if (variable != null) {
				if (variable.getType() == OperatorType.BOOL) {
					message.writeString(variableName);
					message.writeBoolean(variable.getBValue());
				} else {
					message.writeString(variableName);
					message.writeInt(variable.getIValue());
				}
			}
			device.sendMessage(message, level, worldPosition, state);
		}
	}
	
}
