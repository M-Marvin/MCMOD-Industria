package de.redtec.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.redtec.RedTec;
import de.redtec.blocks.BlockJigsaw;
import de.redtec.blocks.BlockJigsaw.JigsawType;
import de.redtec.gui.ContainerJigsaw;
import de.redtec.registys.ModTileEntityType;
import de.redtec.util.JigsawFileManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.Template.BlockInfo;
import net.minecraft.world.server.ServerWorld;

public class TileEntityJigsaw extends TileEntity implements INamedContainerProvider, ITickableTileEntity {
	
	public ResourceLocation poolFile;
	public ResourceLocation name;
	public ResourceLocation targetName;
	public ResourceLocation replaceState;
	public boolean powered;
	public boolean lockOrientation;
	
	public TileEntityJigsaw() {
		super(ModTileEntityType.JIGSAW);
		this.poolFile = new ResourceLocation(RedTec.MODID, "empty");
		this.name = new ResourceLocation(RedTec.MODID, "empty");
		this.targetName = new ResourceLocation(RedTec.MODID, "empty");
		this.replaceState = Blocks.AIR.getRegistryName();
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putString("poolFile", this.poolFile.toString());
		compound.putString("name", this.name.toString());
		compound.putString("targetName", this.targetName.toString());
		compound.putString("replaceState", this.replaceState.toString());
		compound.putBoolean("lockOrientation", this.lockOrientation);
		compound.putBoolean("powered", this.powered);
		return super.write(compound);
	}
	
	@Override
	public void func_230337_a_(BlockState state, CompoundNBT compound) {
		this.poolFile = ResourceLocation.tryCreate(compound.getString("poolFile"));
		this.name = ResourceLocation.tryCreate(compound.getString("name"));
		this.targetName = ResourceLocation.tryCreate(compound.getString("targetName"));
		this.replaceState = ResourceLocation.tryCreate(compound.getString("replaceState"));
		this.lockOrientation = compound.getBoolean("lockOrientation");
		this.powered = compound.getBoolean("powered");
		super.func_230337_a_(state, compound);
	}
	
	public void onNeighborChange() {
		
		boolean power = this.getWorld().isBlockPowered(pos);
		
		if (power != this.powered) {
			
			Random rand = this.world.rand;
			this.powered = power;
			if (this.powered) this.generateStructure(false, rand.nextInt(20), new Random(rand.nextLong()));
			
		}
		
	}
	
	public Direction getFacing() {
		
		BlockState state = this.getBlockState();
		
		if (state.get(BlockJigsaw.TYPE) == JigsawType.HORIZONTAL) {
			return state.get(BlockJigsaw.FACING);
		} else if (state.get(BlockJigsaw.TYPE) == JigsawType.VERTICAL_UP) {
			return Direction.UP;
		} else {
			return Direction.DOWN;
		}
		
	}
	
	public void generateStructure(boolean keepJigsaws, int levels, Random rand) {
		
		if (!this.world.isRemote()) {
			
			boolean hasAlreadyGenerated = this.world.getBlockState(pos.offset(this.getFacing())).getBlock() == RedTec.jigsaw;
			
			ServerWorld world = (ServerWorld) this.world;
			ListNBT list = JigsawFileManager.getPoolList(world, this.poolFile);
			
			if (list != null && levels > 0 && !hasAlreadyGenerated && !this.targetName.getPath().equals("empty")) {
				
				HashMap<Integer, String> files = new HashMap<Integer, String>();
				int index = 0;
				for (int i = 0; i < list.size(); i++) {
					CompoundNBT entry = list.getCompound(i);
					int chance = entry.getInt("chance");
					String file = entry.getString("file");
					for (int i1 = 0; i1 < chance; i1++) {
						files.put(index++, file);
					}
				}
				
				String randomFile = files.size() > 1 ? files.get(rand.nextInt(index - 1)) : files.get(0);
				
				ResourceLocation resourceStructure = ResourceLocation.tryCreate(randomFile);
				Template template = JigsawFileManager.getTemplate(world, resourceStructure);
				
				if (template != null) {
					
					JigsawType alowedType = this.getBlockState().get(BlockJigsaw.TYPE).getOppesite();
					List<BlockInfo> jigawBlocks = template.func_215381_a(BlockPos.ZERO, new PlacementSettings(), RedTec.jigsaw);
					
					List<BlockInfo> filteredJigsaws = new ArrayList<BlockInfo>();
					for (BlockInfo block : jigawBlocks) {
						ResourceLocation jigsawName = ResourceLocation.tryCreate(block.nbt.getString("name"));
						if (jigsawName.toString().equals(this.targetName.toString()) && block.state.get(BlockJigsaw.TYPE) == alowedType) filteredJigsaws.add(block);
					}
					
					if (filteredJigsaws.size() != 0) {
						
						BlockInfo randomJigsaw = filteredJigsaws.size() > 1 ? filteredJigsaws.get(rand.nextInt(filteredJigsaws.size())) : filteredJigsaws.get(0);
						
						Rotation rotation = Rotation.NONE;
						
						if (alowedType != JigsawType.HORIZONTAL && !this.lockOrientation) {
							
							rotation = Rotation.randomRotation(rand);
							
						} else {
							
							Direction compare1 = this.getBlockState().get(BlockJigsaw.FACING);
							Direction compare2 = alowedType == JigsawType.HORIZONTAL ? randomJigsaw.state.get(BlockJigsaw.FACING).getOpposite() : randomJigsaw.state.get(BlockJigsaw.FACING);
							
							if (compare1 == compare2) {
								rotation = Rotation.NONE;
							} else if (compare1 == compare2.getOpposite()) {
								rotation = Rotation.CLOCKWISE_180;
							} else if (compare1.rotateYCCW() == compare2) {
								rotation = Rotation.CLOCKWISE_90;
							} else if (compare1.rotateY() == compare2) {
								rotation = Rotation.COUNTERCLOCKWISE_90;
							}
							
						}
						
						PlacementSettings placement = (new PlacementSettings())
								.setMirror(Mirror.NONE)
								.setRotation(rotation)
								.setIgnoreEntities(false)
								.setChunk((ChunkPos)null);
						
						BlockPos offset = randomJigsaw.pos;
						offset = Template.getTransformedPos(offset, Mirror.NONE, rotation, BlockPos.ZERO);
						offset = offset.offset(this.getFacing().getOpposite());
						
						BlockPos generationPos = this.pos.subtract(offset);
						
						template.func_237144_a_(world, generationPos, placement, rand);
						
						for (BlockInfo jigsaw : jigawBlocks) {

							BlockPos transformedPos = Template.getTransformedPos(jigsaw.pos, Mirror.NONE, rotation, BlockPos.ZERO);
							transformedPos = generationPos.add(transformedPos);
							
							TileEntity tileEntity = world.getTileEntity(transformedPos);
							
							if (tileEntity instanceof TileEntityJigsaw) {
								
								((TileEntityJigsaw) tileEntity).generateStructure(keepJigsaws, levels - 1, rand);
								
							}
							
						}
						
					}
					
				}
				
			}
			
			if (!keepJigsaws) {
				
				@SuppressWarnings("deprecation")
				BlockState replaceState = Registry.BLOCK.getOrDefault(this.replaceState).getDefaultState();
				world.setBlockState(pos, replaceState, 2);
				
			}
			
		}
		
	}
		
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerJigsaw(id, playerInv, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return NarratorChatListener.EMPTY;
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
		if (!this.world.isRemote() && this.waitForGenerateLevels > 0) {
			this.generateStructure(false, this.waitForGenerateLevels, this.randForGeneration);
			this.waitForGenerateLevels = -1;
			this.randForGeneration = null;
		}
	}
	
}
