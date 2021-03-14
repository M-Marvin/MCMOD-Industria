package de.redtec.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.redtec.RedTec;
import de.redtec.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.redtec.tileentity.TileEntitySimpleBlockTicking;
import de.redtec.typeregistys.ModSoundEvents;
import de.redtec.util.ElectricityNetworkHandler;
import de.redtec.util.ElectricityNetworkHandler.ElectricityNetwork;
import de.redtec.util.IAdvancedBlockInfo;
import de.redtec.util.IElectricConnective;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BlockMTransformatorContact extends BlockContainerBase implements IElectricConnective, IAdvancedBlockInfo {
	
	public static final EnumProperty<Voltage> VOLTAGE = EnumProperty.create("voltage", Voltage.class, Voltage.LowVoltage, Voltage.NormalVoltage, Voltage.HightVoltage, Voltage.ExtremVoltage);
	public static final BooleanProperty INPUT = BooleanProperty.create("input");
	
	public BlockMTransformatorContact() {
		super("transformator_contact", Material.IRON, 2F, SoundType.METAL);
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(VOLTAGE, INPUT);
	}
	
	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		return state.get(VOLTAGE);
	}
	
	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
		
		if (state.get(INPUT)) {
			
			float needEnergy = getNeedEnergy(world, pos);
			
			int transferPower = getPower(world, pos);
			float needCurrent = Math.min(transferPower, needEnergy) / state.get(VOLTAGE).getVoltage();
			
			return Math.max(0.001F, needCurrent);
			
		} else {
			
			return -(getEnergy(world, pos) / (float) state.get(VOLTAGE).getVoltage());
			
		}
		
	}
	
	@Override
	public boolean canConnect(Direction side, World world, BlockPos pos, BlockState state) {
		return true;
	}
	
	@Override
	public DeviceType getDeviceType() {
		return DeviceType.SWITCH;
	}
	
	public float getEnergy(World world, BlockPos pos) {

		List<BlockPos> blocks = getBlocks(world, pos);
		ElectricityNetworkHandler handler = ElectricityNetworkHandler.getHandlerForWorld(world);
		float recivedEnergy = 0;
		int energyOutputs = 0;
		
		for (BlockPos pos2 : blocks) {
			
			BlockState state = world.getBlockState(pos2);
			
			if (state.getBlock() == RedTec.transformator_contact) {
				
				if (state.get(INPUT)) {

					ElectricityNetwork network = handler.getNetwork(pos2);
					
					if (network.getVoltage() == state.get(VOLTAGE)) {
						
						float energy = network.getVoltage().getVoltage() * network.getCurrent();
						recivedEnergy += energy;
						
					}
					
				} else {
					
					energyOutputs++;
					
				}
				
			}
			
		}
		
		return recivedEnergy / energyOutputs;
		
	}
	
	public float getNeedEnergy(World world, BlockPos pos) {

		List<BlockPos> blocks = getBlocks(world, pos);
		ElectricityNetworkHandler handler = ElectricityNetworkHandler.getHandlerForWorld(world);
		float needEnergy = 0;
		
		for (BlockPos pos2 : blocks) {
			
			BlockState state = world.getBlockState(pos2);
			
			if (state.getBlock() == RedTec.transformator_contact) {
				
				if (!state.get(INPUT)) {

					ElectricityNetwork network = handler.getNetwork(pos2);
					
					float energy = network.getVoltage().getVoltage() * network.getNeedCurrent();
					needEnergy += energy;
					
				}
			}
			
		}
		
		return needEnergy;
		
	}
	
	public int getPower(World world, BlockPos pos) {
		
		List<BlockPos> blocks = getBlocks(world, pos);
		return blocks.size() * 1000;
		
	}
	
	public List<BlockPos> getBlocks(World world, BlockPos pos) {
		
		List<BlockPos> blocks = new ArrayList<BlockPos>();
		scannBlocks(blocks, pos, world, 0);
		return blocks;
		
	}
	
	private void scannBlocks(List<BlockPos> blockList, BlockPos scannPos, World world, int scannDepth) {
		
		if (!blockList.contains(scannPos)) {
			
			BlockState state = world.getBlockState(scannPos);
			
			if (state.getBlock() == RedTec.transformator_coil || state.getBlock() == RedTec.transformator_contact) {
				
				blockList.add(scannPos);
				
				if (scannDepth <= 100) {
					
					for (Direction d : Direction.values()) {
						
						BlockPos scannPos2 = scannPos.offset(d);
						
						scannBlocks(blockList, scannPos2, world, scannDepth++);
						
					}
					
				}
				
			}
			
		}
		
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return Block.makeCuboidShape(1, 0, 1, 15, 16, 15);
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (player.isSneaking()) {
			worldIn.setBlockState(pos, state.with(VOLTAGE, state.get(VOLTAGE).next()));
		} else {
			worldIn.setBlockState(pos, state.with(INPUT, !state.get(INPUT)));
		}
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info, flag) -> {
			info.add(new TranslationTextComponent("redtec.block.info.power", 1000));
			info.add(new TranslationTextComponent("redtec.block.info.transformator"));
		};
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null;
	}
	
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		this.updateNetwork(worldIn, pos);
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntitySimpleBlockTicking();
	}
	
	@Override
	public boolean beforNetworkChanges(World world, BlockPos pos, BlockState state, ElectricityNetwork network, int lap) {
		
		if (network.canMachinesRun() == Voltage.NoLimit && lap < 3) {
			
			List<BlockPos> blocks = getBlocks(world, pos);
			for (BlockPos pos1 : blocks) {
				BlockState state1 = world.getBlockState(pos1);
				if (state1.getBlock() == RedTec.transformator_contact) {
					ElectricityNetworkHandler.getHandlerForWorld(world).updateNetwork(world, pos1);
				}
			}
			
		}
		
		return false;
		
	}
	
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (ElectricityNetworkHandler.getHandlerForWorld(worldIn).getNetwork(pos).getCurrent() > 0.5F) {
			worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), ModSoundEvents.TRANSFORMATOR_LOOP, SoundCategory.BLOCKS, 1F, 1, false);
		}
		super.animateTick(stateIn, worldIn, pos, rand);
	}
	
}
