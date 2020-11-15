package de.redtec.tileentity;

import java.util.HashMap;
import java.util.Map.Entry;

import de.redtec.RedTec;
import de.redtec.blocks.BlockSignalProcessorContact;
import de.redtec.items.ItemProcessor;
import de.redtec.items.ItemProcessor.OperatorResult;
import de.redtec.items.ItemProcessor.OperatorType;
import de.redtec.registys.ModTileEntityType;
import de.redtec.util.RedstoneControlSignal;
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

public class TileEntitySignalProcessorContact extends TileEntity implements ITickableTileEntity, INamedContainerProvider {
	
	private ItemStack processorStack;
	private HashMap<String, ItemProcessor.OperatorResult> variables = new HashMap<String, ItemProcessor.OperatorResult>();
	
	private boolean lastSuccessState;
	
	public TileEntitySignalProcessorContact() {
		super(ModTileEntityType.SIGNAL_PROCESSOR);
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
		
		String name = chanel.getDisplayName().getUnformattedComponentText();
		if (name.length() > 0) {
			
			this.variables.put(name, new OperatorResult(powered, OperatorType.BOOL));
			
		}
		
	}
	
	@Override
	public void tick() {
		
		if (!this.world.isRemote) {
			
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
					chanel.setDisplayName(new StringTextComponent(name));
					RedstoneControlSignal signal = new RedstoneControlSignal(chanel, value.getBValue());
					
					BlockState state = this.world.getBlockState(this.pos);
					if (state.getBlock() == RedTec.signal_processor_contact) {
						
						//System.out.println(signal.getChanelItem().getDisplayName().getUnformattedComponentText() + " " + signal.isPowered());
						
						((BlockSignalProcessorContact) state.getBlock()).sendSignal(this.world, this.pos, signal);
						
					}
					
				}
				
			}
			
			this.world.notifyBlockUpdate(this.pos, getBlockState(), getBlockState(), 0);
			
		}
		
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT compound = new CompoundNBT();
		this.write(compound);
		return new SUpdateTileEntityPacket(this.pos, 0, compound);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.func_230337_a_(this.getBlockState(), pkt.getNbtCompound());
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		if (hasProcessor()) {
			CompoundNBT processorNbt = this.processorStack.write(new CompoundNBT());
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
		return super.write(compound);
	}
	
	@Override
	public void func_230337_a_(BlockState state, CompoundNBT compound) {
		if (compound.contains("Processor")) {
			ItemStack processorStack = ItemStack.read(compound.getCompound("Processor"));
			this.setProcessorStack(processorStack);
		}
		this.lastSuccessState = compound.getBoolean("LastSuccessState");
		CompoundNBT bufferNBT = compound.getCompound("Memory");
		this.variables.clear();
		for (String variableName : bufferNBT.keySet()) {
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
		super.func_230337_a_(state, compound);
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
			return new TranslationTextComponent(RedTec.signal_processor_contact.getTranslationKey());
		}
		
	}
		
}
