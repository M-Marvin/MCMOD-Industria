package de.industria.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.industria.Industria;
import de.industria.blocks.BlockJigsaw;
import de.industria.blocks.BlockJigsaw.JigsawType;
import de.industria.gui.ContainerJigsaw;
import de.industria.structureprocessor.JigsawTemplateProcessor;
import de.industria.structureprocessor.JigsawTemplateProcessor.JigsawReplacement;
import de.industria.typeregistys.ModItems;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.handler.ItemStackHelper;
import de.industria.util.handler.JigsawFileManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.Template.BlockInfo;
import net.minecraft.world.server.ServerWorld;

public class TileEntityJigsaw extends TileEntity implements INamedContainerProvider, ITickableTileEntity {
	
	public ResourceLocation poolFile;
	public ResourceLocation name;
	public ResourceLocation targetName;
	public BlockState replaceState;
	public boolean powered;
	public boolean lockOrientation;
	
	public TileEntityJigsaw() {
		super(ModTileEntityType.JIGSAW);
		this.poolFile = new ResourceLocation(Industria.MODID, "empty");
		this.name = new ResourceLocation(Industria.MODID, "empty");
		this.targetName = new ResourceLocation(Industria.MODID, "empty");
		this.replaceState = Blocks.AIR.defaultBlockState();
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.putString("poolFile", this.poolFile.toString());
		compound.putString("name", this.name.toString());
		compound.putString("targetName", this.targetName.toString());
		compound.putString("replaceState", ItemStackHelper.getBlockStateString(this.replaceState));
		compound.putBoolean("lockOrientation", this.lockOrientation);
		compound.putBoolean("powered", this.powered);
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		this.poolFile = ResourceLocation.tryParse(compound.getString("poolFile"));
		this.name = ResourceLocation.tryParse(compound.getString("name"));
		this.targetName = ResourceLocation.tryParse(compound.getString("targetName"));
		this.replaceState = Blocks.AIR.defaultBlockState();
		try {
			BlockStateParser parser = new BlockStateParser(new StringReader(compound.contains("replaceState") ? compound.getString("replaceState") : "minecraft:air"), true);
			parser.parse(false);
			this.replaceState = parser.getState();
		} catch (CommandSyntaxException e) {
			Industria.LOGGER.error("Cant parse BlockState!");
			e.printStackTrace();
		}
		this.lockOrientation = compound.getBoolean("lockOrientation");
		this.powered = compound.getBoolean("powered");
		super.load(state, compound);
	}
	
	public void onNeighborChange() {
		
		boolean power = this.getLevel().hasNeighborSignal(worldPosition);
		
		if (power != this.powered) {
			
			Random rand = this.level.random;
			this.powered = power;
			if (this.powered) this.generateStructure(false, rand.nextInt(20), new Random(rand.nextLong()));
			
		}
		
	}
	
	public Direction getFacing() {
		
		BlockState state = this.getBlockState();
		
		if (state.getValue(BlockJigsaw.TYPE) == JigsawType.HORIZONTAL) {
			return state.getValue(BlockJigsaw.FACING);
		} else if (state.getValue(BlockJigsaw.TYPE) == JigsawType.VERTICAL_UP) {
			return Direction.UP;
		} else {
			return Direction.DOWN;
		}
		
	}
	
	public void generateStructure(boolean keepJigsaws, int levels, Random rand) {
		
		if (!this.level.isClientSide()) {
			
			boolean hasAlreadyGenerated = this.level.getBlockState(worldPosition.relative(this.getFacing())).getBlock() == ModItems.jigsaw;
			
			ServerWorld world = (ServerWorld) this.level;
			ListNBT list = JigsawFileManager.getPoolList(world, this.poolFile);
			
			if (list != null && levels > 0 && !hasAlreadyGenerated && !this.targetName.getPath().equals("empty")) {
				
				HashMap<Integer, Integer> structures = new HashMap<Integer, Integer>();
				int index = 0;
				int structIndex = 0;
				for (int i = 0; i < list.size(); i++) {
					CompoundNBT entry = list.getCompound(i);
					int chance = entry.getInt("chance");
					for (int i1 = 0; i1 < chance; i1++) {
						structures.put(index++, structIndex);
					}
					structIndex++;
				}
				
				int randomStructureId = structures.size() > 1 ? structures.get(rand.nextInt(index - 1)) : structures.get(0);
				CompoundNBT structureNBT = list.getCompound(randomStructureId);
				
				ResourceLocation resourceStructure = ResourceLocation.tryParse(structureNBT.getString("file"));
				JigsawReplacement replaceMode = JigsawReplacement.fromName(structureNBT.getString("replaceBlocks"));
				ListNBT blockList = structureNBT.contains("blocks") ? structureNBT.getList("blocks", 8) : new ListNBT();
				
				List<BlockState> blocks = new ArrayList<BlockState>();
				
				for (int i = 0; i < blockList.size(); i++) {
					StringNBT stringNBT = (StringNBT) blockList.get(i);
					BlockStateParser parser = new BlockStateParser(new StringReader(stringNBT.getAsString()), true);
					try {
						parser.parse(false);
						blocks.add(parser.getState());
					} catch (CommandSyntaxException e) {
						Industria.LOGGER.warn("Cant parse replace-list block " + stringNBT.getAsString() + " in jigsaw metafile " + resourceStructure + "!");
					}
				}
				
				Template template = JigsawFileManager.getTemplate(world, resourceStructure);
				
				if (template != null) {
					
					JigsawType alowedType = this.getBlockState().getValue(BlockJigsaw.TYPE).getOppesite();
					
					List<BlockInfo> jigsawBlocks = template.filterBlocks(BlockPos.ZERO, new PlacementSettings(), ModItems.jigsaw);
					
					List<BlockInfo> filteredJigsaws = new ArrayList<BlockInfo>();
					for (BlockInfo block : jigsawBlocks) {
						ResourceLocation jigsawName = ResourceLocation.tryParse(block.nbt.getString("name"));
						if (jigsawName.toString().equals(this.targetName.toString()) && block.state.getValue(BlockJigsaw.TYPE) == alowedType) filteredJigsaws.add(block);
					}
					
					if (filteredJigsaws.size() != 0) {
						
						BlockInfo randomJigsaw = filteredJigsaws.size() > 1 ? filteredJigsaws.get(rand.nextInt(filteredJigsaws.size())) : filteredJigsaws.get(0);
						
						Rotation rotation = Rotation.NONE;
						
						if (alowedType != JigsawType.HORIZONTAL && !this.lockOrientation) {
							
							rotation = Rotation.getRandom(rand);
							
						} else {
							
							Direction compare1 = this.getBlockState().getValue(BlockJigsaw.FACING);
							Direction compare2 = alowedType == JigsawType.HORIZONTAL ? randomJigsaw.state.getValue(BlockJigsaw.FACING).getOpposite() : randomJigsaw.state.getValue(BlockJigsaw.FACING);
							
							if (compare1 == compare2) {
								rotation = Rotation.NONE;
							} else if (compare1 == compare2.getOpposite()) {
								rotation = Rotation.CLOCKWISE_180;
							} else if (compare1.getCounterClockWise() == compare2) {
								rotation = Rotation.CLOCKWISE_90;
							} else if (compare1.getClockWise() == compare2) {
								rotation = Rotation.COUNTERCLOCKWISE_90;
							}
							
						}
						
						PlacementSettings placement = (new PlacementSettings())
								.setMirror(Mirror.NONE)
								.setRotation(rotation)
								.setIgnoreEntities(false)
								.setChunkPos((ChunkPos)null)
								.addProcessor(new JigsawTemplateProcessor(replaceMode, blocks));
						
						BlockPos offset = randomJigsaw.pos;
						offset = Template.transform(offset, Mirror.NONE, rotation, BlockPos.ZERO);
						offset = offset.relative(this.getFacing().getOpposite());
						
						BlockPos generationPos = this.worldPosition.subtract(offset);
						
						template.placeInWorldChunk(world, generationPos, placement, rand);
						
						for (BlockInfo jigsaw : jigsawBlocks) {
							
							BlockPos transformedPos = Template.transform(jigsaw.pos, Mirror.NONE, rotation, BlockPos.ZERO);
							transformedPos = generationPos.offset(transformedPos);
							
							TileEntity tileEntity = world.getBlockEntity(transformedPos);
							
							if (tileEntity instanceof TileEntityJigsaw) {
								
								((TileEntityJigsaw) tileEntity).generateStructure(keepJigsaws, levels - 1, rand);
								
							}
							
						}
						
					}
					
				}
				
			}
			
			if (!keepJigsaws) {
				
				world.setBlock(worldPosition, replaceState, 2);
				
			}
			
		}
		
	}
		
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerJigsaw(id, playerInv, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return NarratorChatListener.NO_TITLE;
	}

	// Called from worldgeneration (JigsawFeature)
	public void setWaitForGenerate(int generationLevels, Random rand) {
		this.waitForGenerateLevels = generationLevels;
		this.randForGeneration = rand;
	}
	protected int waitForGenerateLevels = -1;
	protected Random randForGeneration = null;
	
	@Override
	public void tick() {
		if (!this.level.isClientSide() && this.waitForGenerateLevels > 0) {
			this.generateStructure(false, this.waitForGenerateLevels, this.randForGeneration);
			this.waitForGenerateLevels = -1;
			this.randForGeneration = null;
		}
	}
	
}
