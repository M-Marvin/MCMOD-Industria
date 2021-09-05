package de.industria.util.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import de.industria.blocks.BlockMultipart;
import de.industria.blocks.BlockMultipartBuilded;
import de.industria.util.handler.UtilHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MultipartBuild {
	
	protected BlockMultipartBuilded<?> resultingState;
	protected HashMap<String, Block[]> keys;
	protected List<String[]> pattern;
	
	public MultipartBuild(BlockMultipartBuilded<?> resultingState) {
		this.resultingState = resultingState;
		this.pattern = new ArrayList<String[]>();
		this.keys = new HashMap<String, Block[]>();
		this.addKey(" ", Blocks.AIR, Blocks.CAVE_AIR);
	}
	
	public BlockMultipartBuilded<?> getResultingState() {
		return resultingState;
	}
	
	public List<String[]> getPattern() {
		return pattern;
	}
	
	public HashMap<String, Block[]> getKeys() {
		return keys;
	}
	
	public int getSizeX() {
		return this.pattern.get(0)[0].length();
	}
	
	public int getSizeZ() {
		return this.pattern.get(0).length;
	}
	
	public int getSizeY() {
		return this.pattern.size();
	}
	
	protected void addPatternLayer(String... rowAlongX) {
		this.pattern.add(rowAlongX);
	}
	
	protected void addKey(String key, Block... possibleBlocks) {
		this.keys.put(key, possibleBlocks);
	}
	
	public MultipartBuildLocation getDefaultBuildData() {
		HashMap<BlockPos, BlockState> blockStates = new HashMap<BlockPos, BlockState>();
		try {
			for (int y = 0; y < getSizeY(); y++) {
				for (int z = 0; z < getSizeZ(); z++) {
					for (int x = 0; x < getSizeX(); x++) {
						char key = this.pattern.get(y)[z].charAt((getSizeX() - 1) - x);
						Block[] possibleBlocks = this.keys.getOrDefault("" + key, new Block[] {});
						blockStates.put(new BlockPos(x, y, z), possibleBlocks[0].defaultBlockState());
					}
				}
			}
		} catch (IndexOutOfBoundsException e) {
			System.err.println("Maleformed multiblock recipe (" + this.resultingState + ")!");
			e.printStackTrace();
			return MultipartBuildLocation.EMPTY;
		}
		MultipartBuildLocation location = new MultipartBuildLocation();
		location.blockStates = blockStates;
		return location;
	}
	
	public boolean tryBuildMultipart(World world, BlockPos pos) {
		
		if (	this.resultingState.getMultipartSizeX() != this.getSizeX() || 
				this.resultingState.getMultipartSizeY() != this.getSizeY() || 
				this.resultingState.getMultipartSizeZ() != this.getSizeZ()) {
			new IllegalStateException("MultipartBlock " + this.resultingState + " size is not identical with MultipartBuild size, this can not be build!").printStackTrace();
			return false;
		}
		
		MultipartBuildLocation buildLocation = tryGetBuildOrientation(world, pos);
		if (buildLocation != null) {
			
			for (int x = 0; x < getSizeX(); x++) {
				for (int z = 0; z < getSizeZ(); z++) {
					for (int y = 0; y < getSizeY(); y++) {
						
						BlockPos buildPos = UtilHelper.rotateBlockPos(new BlockPos(x, y, z), buildLocation.orientation).offset(buildLocation.offset).offset(pos);
						
						BlockState replaceState = world.getBlockState(buildPos);
						if (replaceState.hasTileEntity()) {
							TileEntity tileEntity = world.getBlockEntity(buildPos);
							if (tileEntity instanceof IInventory) {
								InventoryHelper.dropContents(world, buildPos, (IInventory) tileEntity);
							}
						}
						
						world.removeBlock(buildPos, false);
						
						Direction facing = buildLocation.orientation;
						BlockPos internPos = new BlockPos(x, y, z);
						BlockState newState = this.resultingState.defaultBlockState().setValue(BlockMultipart.POS_X, internPos.getX()).setValue(BlockMultipart.POS_Y, internPos.getY()).setValue(BlockMultipart.POS_Z, internPos.getZ()).setValue(BlockMultipart.FACING, facing);
						
						world.setBlock(buildPos, newState, 2);
						
						if (this.resultingState.canStoreBuildData(world, buildPos, newState)) {
							this.resultingState.storeBuildData(world, buildPos, newState, buildLocation);
						}
						
					}
				}
			}

			return true;
			
		}
		
		return false;
		
	}
		
	/**
	 * Gets the rotation and offset of the new multipart when the structure matches.
	 * @param world
	 * @param pos
	 * @return
	 */
	@Nullable
	public MultipartBuildLocation tryGetBuildOrientation(World world, BlockPos pos) {
		for (Direction orientation : Direction.values()) {
			if (!orientation.getAxis().isVertical()) {
				BlockPos size = new BlockPos(getSizeX(), getSizeY(), getSizeZ());
				for (int x = -(size.getX() - 1); x <= 0; x++) {
					for (int y = -(size.getY() - 1); y <= 0; y++) {
						for (int z = -(size.getZ() - 1); z <= 0; z++) {
							BlockPos checkOffset = UtilHelper.rotateBlockPos(new BlockPos(x, y, z), orientation);
							MultipartBuildLocation location = new MultipartBuildLocation(orientation, checkOffset);
							if (matchesStructureAt(world, pos, location)) return location;
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Checks if the structure at the given location matches, and fills the list of the location object with the replaced BlockStates.
	 * @param world
	 * @param pos
	 * @param location
	 * @return
	 */
	public boolean matchesStructureAt(World world, BlockPos pos, MultipartBuildLocation location) {
		HashMap<BlockPos, BlockState> blockStates = new HashMap<BlockPos, BlockState>();
		try {
			for (int y = 0; y < getSizeY(); y++) {
				for (int z = 0; z < getSizeZ(); z++) {
					for (int x = 0; x < getSizeX(); x++) {
						BlockPos offset = UtilHelper.rotateBlockPos(new BlockPos(x, y, z), location.orientation).offset(location.offset).offset(pos);
						BlockState state = world.getBlockState(offset);
						blockStates.put(new BlockPos(x, y, z), state);
						
						char key = this.pattern.get(y)[z].charAt((getSizeX() - 1) - x);
						Block[] possibleBlocks = this.keys.getOrDefault("" + key, new Block[] {});
						if (possibleBlocks.length == 0) System.err.println("Key " + key + " in multipart recipe for block " + this.resultingState + " empty, this can not be build!");
						if (!matchesStates(possibleBlocks, state)) return false;
					}
				}
			}
		} catch (IndexOutOfBoundsException e) {
			System.err.println("Maleformed multiblock recipe (" + this.resultingState + ")!");
			e.printStackTrace();
			return false;
		}
		location.blockStates = blockStates;
		return true;
	}
	
	protected boolean matchesStates(Block[] possibleBlocks, BlockState state) {
		for (Block validState : possibleBlocks) {
			if (validState == state.getBlock()) return true;
		}
		return false;
	}
	
	public static class MultipartBuildLocation {
		public MultipartBuildLocation(Direction orientation, BlockPos offset) {
			this.orientation = orientation;
			this.offset = offset;
		}
		
		// Offset to the 0 0 0 position of the structure relative to the wrench-clicked-position
		public BlockPos offset = BlockPos.ZERO;
		// Orientation of the structure
		public Direction orientation = Direction.NORTH;
		// Map of the replaced BlockStates, positions relative to 0 0 0 of the structure and with facing north.
		public HashMap<BlockPos, BlockState> blockStates = new HashMap<BlockPos, BlockState>();
		
		public static final MultipartBuildLocation EMPTY = new MultipartBuildLocation();
		private MultipartBuildLocation() {}
		
		public boolean isEmpty() {
			return this.equals(EMPTY) || this.blockStates.isEmpty();
		}
		
		public CompoundNBT writeNBT(CompoundNBT nbt) {
			if (this.orientation == null || this.blockStates == null || this.offset == null) return nbt;
			nbt.putString("Orientation", this.orientation.getSerializedName());
			nbt.put("PlaceOffset", NBTUtil.writeBlockPos(this.offset));
			ListNBT blockStatesNBT = new ListNBT();
			for (Entry<BlockPos, BlockState> entry : this.blockStates.entrySet()) {
				CompoundNBT entryNBT = new CompoundNBT();
				entryNBT.put("Position", NBTUtil.writeBlockPos(entry.getKey()));
				entryNBT.put("State", NBTUtil.writeBlockState(entry.getValue()));
				blockStatesNBT.add(entryNBT);
			}
			nbt.put("BuildBlocks", blockStatesNBT);
			return nbt;
		}
		
		public static MultipartBuildLocation loadNBT(CompoundNBT nbt) {
			MultipartBuildLocation data = new MultipartBuildLocation(
					Direction.byName(nbt.getString("Orientation")),
					NBTUtil.readBlockPos(nbt.getCompound("PlaceOffset"))
			);
			ListNBT blockStateNBT = nbt.getList("BuildBlocks", 10);
			for (int i = 0; i < blockStateNBT.size(); i++) {
				CompoundNBT entry = blockStateNBT.getCompound(i);
				data.blockStates.put(NBTUtil.readBlockPos(entry.getCompound("Position")), NBTUtil.readBlockState(entry.getCompound("State")));
			}
			return data;
		}
		
	}
	
}

