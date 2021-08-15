package de.industria.items;

import java.util.HashMap;
import java.util.Map.Entry;

import de.industria.gui.ContainerRProcessor;
import de.industria.gui.ContainerTileEntity;
import de.industria.tileentity.TileEntityRSignalProcessorContact;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ItemProcessor extends ItemBase {
	
	private int maxLineCount;
	private boolean canHandleInteger;
	
	public ItemProcessor(String name, int maxLineCount, boolean canHandleInteger, Rarity rarity) {
		super(name, ItemGroup.TAB_REDSTONE, rarity);
		this.maxLineCount = maxLineCount;
		this.canHandleInteger = canHandleInteger;
	}
	
	public boolean canHandleInteger() {
		return this.canHandleInteger;
	}
	
	@Override
	public boolean isFoil(ItemStack stack) {
		return this.canHandleInteger();
	}
	
	public int getMaxLineCount() {
		return this.maxLineCount;
	}
		
	public ContainerTileEntity<TileEntityRSignalProcessorContact> createContainer(int id, PlayerInventory playerInv, TileEntityRSignalProcessorContact tileEntity) {
		return new ContainerRProcessor(id, playerInv, tileEntity);
	}
	
	public ITextComponent getScreenTitle(ItemStack stack) {
		return new TranslationTextComponent(this.getDescriptionId());
	}
	
	public void storeCodeLinesInProcessor(ItemStack stack, String[] codeLines) {
		
		CompoundNBT compound = stack.hasTag() ? stack.getTag() : new CompoundNBT();
		CompoundNBT codeTag = new CompoundNBT();
		for (int i = 0; i < codeLines.length; i++) {
			codeTag.putString("line" + i, codeLines[i]);
		}
		compound.put("Code", codeTag);
		stack.setTag(compound);
		
	}
	
	public String[] getCodeLinesFromProcessor(ItemStack stack) {
		
		String[] codeLines = new String[this.getMaxLineCount()];
		CompoundNBT compound = stack.hasTag() ? stack.getTag() : new CompoundNBT();
		CompoundNBT codeTag = compound.contains("Code") ? compound.getCompound("Code") : new CompoundNBT();
		for (int i = 0; i < this.getMaxLineCount(); i++) {
			codeLines[i] = codeTag.contains("line" + i) ? codeTag.getString("line" + i) : "";
		}
		
		return codeLines;
		
	}
	
	@SuppressWarnings("unchecked")
	public boolean process(ItemStack stack, HashMap<String, OperatorResult> variables) {
		
		boolean success = true;
		for (String line : this.getCodeLinesFromProcessor(stack)) {
			boolean result = executeLine(line, variables);
			if (!result) success = false;
		}
		
		if (!this.canHandleInteger) {
			
			for (Entry<String, OperatorResult> variable : ((HashMap<String, OperatorResult>) variables.clone()).entrySet()) {
				if (variable.getValue().isInt()) {
					variables.remove(variable.getKey());
				}
			}
			
		}
		
		return success;
		
	}
	
	public static class OperatorResult {
		
		public static final OperatorResult FAIL = new OperatorResult(null, null);
		public static final OperatorResult EMPTY = new OperatorResult(null, null);
		
		Object value;
		OperatorType type;
		
		public OperatorResult(Object value, OperatorType type) {
			this.value = value;
			this.type = type;
		}
		
		public Object getValue() {
			return value;
		}
		
		public boolean getBValue() {
			if (value instanceof Boolean) {
				return (Boolean) value;
			} else {
				return false;
			}
		}
		
		public int getIValue() {
			if (value instanceof Integer) {
				return (Integer) value;
			} else {
				return 0;
			}
		}
		
		public OperatorType getType() {
			return type;
		}
		
		public boolean isBool() {
			return this.type == OperatorType.BOOL || this == EMPTY;	
		}
		
		public boolean isInt() {
			return this.type == OperatorType.INT || this == EMPTY;	
		}
		
		@Override
		public String toString() {
			if (this == EMPTY) {
				return "Empty";
			} else if (this == FAIL) {
				return "Fail";
			} else if (this.isBool()) {
				return "Boolean{" + this.getBValue() + "}";
			} else if (this.isInt()) {
				return "Integer{" + this.getIValue() + "}";
			}
			return super.toString();
		}
		
	}
	
	
	
	
	
	private static boolean isVariable(String name) {
		return	!name.contains("=") &&
				!name.contains("+") &&
				!name.contains("-") &&
				!name.contains("!") &&
				!name.contains("|") &&
				!name.contains("&") &&
				!name.contains("?") &&
				!name.contains(":");
	}

	public static OperatorResult getPrimitiveValue(String name) {
		
		if (name.equals("true")) {
			return new OperatorResult(true, OperatorType.BOOL);
		} else if (name.equals("false")) {
			return new OperatorResult(false, OperatorType.BOOL);
		} else {
			try {
				int i = Integer.parseInt(name);
				return new OperatorResult(i, OperatorType.INT);
			} catch (NumberFormatException e) {
				return OperatorResult.FAIL;
			}
		}
		
	}
	
	public boolean executeLine(String line, HashMap<String, OperatorResult> variables) {
		
		String[] parts = line.split(" ");
		
		String outputVariable = "";
		
		if (parts.length > 1) {
			
			for (int i = 0; i < parts.length; i++) {
				String part = parts[i];
				
				if (i == 0) {
					if (!isVariable(part)) return false;
					outputVariable = part;
				} if (i == 2) {
					if (!part.equals("=")) return false;
				} else {
					
					String[] valueParts = subParts(parts, 2, parts.length - 1);
					OperatorResult result = calculate(valueParts, variables);
					
					if (result != OperatorResult.FAIL) {
						
						variables.put(outputVariable, result);
						
						return true;
						
					} else {
						
						return false;
						
					}
					
				}
				
			}
			
		}
		
		return true;
		
	}
	
	public static OperatorResult calculate(String[] parts, HashMap<String, OperatorResult> variables) {
		
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];

			Operator operator = Operator.getOperator(part);
			
			if (operator != null) {
				
				return operator.operate(parts, i, variables);
				
			}
			
		}
		
		if (parts.length == 1) {
			
			OperatorResult primitiv = getPrimitiveValue(parts[0]);
			if (primitiv != OperatorResult.FAIL) {
				
				return primitiv;
				
			}
			
			if (isVariable(parts[0])) {
				
				OperatorResult result = variables.get(parts[0]);
				if (result == null) result = OperatorResult.EMPTY;
				return result;
				
			}
			
		}
		
		return OperatorResult.FAIL;
		
	}
	
	public static String[] subParts(String[] parts, int from, int to) {
		
		if (from <= to) {
			String[] subParts = new String[(to - from) + 1];
			for (int i = from; i <= to; i++) {
				subParts[i - from] = parts[i];
			}
			return subParts;
		}
		return new String[] {};
		
	}
	
	public enum Operator {
		
		NOT((parts, opPos, variables) -> {
			OperatorResult result = calculate(subParts(parts, opPos + 1, parts.length - 1), variables);
			if (result.isBool()) {
				return new OperatorResult(!result.getBValue(), OperatorType.BOOL);
			} else {
				return OperatorResult.FAIL;
			}
		}),
		OR((parts, opPos, variables) -> {
			OperatorResult result1 = calculate(subParts(parts, 0, opPos - 1), variables);
			OperatorResult result2 = calculate(subParts(parts, opPos + 1, parts.length - 1), variables);
			if (result1.isBool() && result2.isBool()) {
				return new OperatorResult(result1.getBValue() || result2.getBValue(), OperatorType.BOOL);
			} else {
				return OperatorResult.FAIL;
			}
		}),
		AND((parts, opPos, variables) -> {
			OperatorResult result1 = calculate(subParts(parts, 0, opPos - 1), variables);
			OperatorResult result2 = calculate(subParts(parts, opPos + 1, parts.length - 1), variables);
			if (result1.isBool() && result2.isBool()) {
				return new OperatorResult(result1.getBValue() && result2.getBValue(), OperatorType.BOOL);
			} else {
				return OperatorResult.FAIL;
			}
		}),
		EQUALS((parts, opPos, variables) -> {
			OperatorResult result1 = calculate(subParts(parts, 0, opPos - 1), variables);
			OperatorResult result2 = calculate(subParts(parts, opPos + 1, parts.length - 1), variables);
			if (result1.isBool() && result2.isBool()) {
				return new OperatorResult(result1.getBValue() == result2.getBValue(), OperatorType.BOOL);
			} else {
				return OperatorResult.FAIL;
			}
		}),
		EQUALS_INT((parts, opPos, variables) -> {
			OperatorResult result1 = calculate(subParts(parts, 0, opPos - 1), variables);
			OperatorResult result2 = calculate(subParts(parts, opPos + 1, parts.length - 1), variables);
			if (result1.isInt() && result2.isInt()) {
				return new OperatorResult(result1.getIValue() == result2.getIValue(), OperatorType.INT);
			} else {
				return OperatorResult.FAIL;
			}
		}),
		HIGHER((parts, opPos, variables) -> {
			OperatorResult result1 = calculate(subParts(parts, 0, opPos - 1), variables);
			OperatorResult result2 = calculate(subParts(parts, opPos + 1, parts.length - 1), variables);
			if (result1.isInt() && result2.isInt()) {
				return new OperatorResult(result1.getIValue() > result2.getIValue(), OperatorType.BOOL);
			} else {
				return OperatorResult.FAIL;
			}
		}),
		LOWER((parts, opPos, variables) -> {
			OperatorResult result1 = calculate(subParts(parts, 0, opPos - 1), variables);
			OperatorResult result2 = calculate(subParts(parts, opPos + 1, parts.length - 1), variables);
			if (result1.isInt() && result2.isInt()) {
				return new OperatorResult(result1.getIValue() < result2.getIValue(), OperatorType.BOOL);
			} else {
				return OperatorResult.FAIL;
			}
		}),
		ADD((parts, opPos, variables) -> {
			OperatorResult result1 = calculate(subParts(parts, 0, opPos - 1), variables);
			OperatorResult result2 = calculate(subParts(parts, opPos + 1, parts.length - 1), variables);
			if (result1.isInt() && result2.isInt()) {
				return new OperatorResult(result1.getIValue() + result2.getIValue(), OperatorType.INT);
			} else {
				return OperatorResult.FAIL;
			}
		}),
		SUBTRACT((parts, opPos, variables) -> {
			OperatorResult result1 = calculate(subParts(parts, 0, opPos - 1), variables);
			OperatorResult result2 = calculate(subParts(parts, opPos + 1, parts.length - 1), variables);
			if (result1.isInt() && result2.isInt()) {
				return new OperatorResult(result1.getIValue() - result2.getIValue(), OperatorType.INT);
			} else {
				return OperatorResult.FAIL;
			}
		}),
		DIVISION((parts, opPos, variables) -> {
			OperatorResult result1 = calculate(subParts(parts, 0, opPos - 1), variables);
			OperatorResult result2 = calculate(subParts(parts, opPos + 1, parts.length - 1), variables);
			if (result1.isInt() && result2.isInt()) {
				return new OperatorResult(result1.getIValue() / result2.getIValue(), OperatorType.INT);
			} else {
				return OperatorResult.FAIL;
			}
		}),
		MULTIPLIKATION((parts, opPos, variables) -> {
			OperatorResult result1 = calculate(subParts(parts, 0, opPos - 1), variables);
			OperatorResult result2 = calculate(subParts(parts, opPos + 1, parts.length - 1), variables);
			if (result1.isInt() && result2.isInt()) {
				return new OperatorResult(result1.getIValue() * result2.getIValue(), OperatorType.INT);
			} else {
				return OperatorResult.FAIL;
			}
		}),
		SELECTOR((parts, opPos, variables) -> {
			OperatorResult selector = calculate(subParts(parts, 0, opPos - 1), variables);
			if (selector.isBool()) {
				
				for (int i = opPos + 1; i < parts.length; i++) {
					if (parts[i].equals(":")) {
						String[] selectedParts = selector.getBValue() ? subParts(parts, opPos + 1, i - 1) : subParts(parts, i + 1, parts.length - 1);
						OperatorResult result = calculate(selectedParts, variables);
						return result;
					}
				}
				
				return OperatorResult.FAIL;
				
			} else {
				return OperatorResult.FAIL;
			}
		}),
		BRACKETS((parts, opPos, variables) -> {
			for (int i = opPos + 1; i < parts.length; i++) {
				if (parts[i].equals(")")) {
					String[] subSequenz = subParts(parts, opPos + 1, i - 1);
					OperatorResult result = calculate(subSequenz, variables);
					
					String[] newSequenz = new String[parts.length - i];
					for (int i2 = i; i2 < parts.length; i2++) {
						newSequenz[i2 - i] = parts[i2];
					}
					newSequenz[0] = result.getValue() != null ? result.getValue().toString() : "false";
					
					OperatorResult completeResult = calculate(newSequenz, variables);
					return completeResult;
					
				}
			}
			return OperatorResult.FAIL;
		});
		
		private Operator(OperatorExecutor executor) {
			this.executor = executor;
		}
		
		private OperatorExecutor executor;
		
		public static Operator getOperator(String name) {
			switch(name) {
			case "=": return Operator.EQUALS;
			case "==": return Operator.EQUALS_INT;
			case "|": return Operator.OR;
			case "&": return Operator.AND;
			case "!": return Operator.NOT;
			case "+": return Operator.ADD;
			case "-": return Operator.SUBTRACT;
			case ">": return Operator.HIGHER;
			case "<": return Operator.LOWER;
			case "?": return Operator.SELECTOR;
			case "(": return Operator.BRACKETS;
			default: return null;
			}
		}
		
		public OperatorResult operate(String[] parts, int operatorPos, HashMap<String, OperatorResult> variables) {
			
			return this.executor.execute(parts, operatorPos, variables);
			
		}
		
	}
	
	private interface OperatorExecutor {
		
		public OperatorResult execute(String[] parts, int operatorPos, HashMap<String, OperatorResult> variables);
		
	}
	
	public enum OperatorType {
		
		BOOL,INT;
		
	}
	
}
