package de.m_marvin.industria.content.blockentities.machines;

import java.util.stream.IntStream;

import de.m_marvin.industria.content.blocks.machines.ElectroMagneticCoilBlock;
import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.conduits.types.items.IConduitItem;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.types.conduits.IElectricConduit;
import de.m_marvin.industria.core.magnetism.MagnetismUtility;
import de.m_marvin.industria.core.registries.Conduits;
import de.m_marvin.industria.core.registries.NodeTypes;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.blocks.DynamicMultiBlockEntity;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ElectroMagneticCoilBlockEntity extends DynamicMultiBlockEntity<ElectroMagneticCoilBlockEntity> {

	protected String[] nodeLanes = {"L", "N"};
	protected ItemStack wires = ItemStack.EMPTY;
	
	public ElectroMagneticCoilBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(ModBlockEntityTypes.ELECTRO_MAGNETIC_COIL.get(), pPos, pBlockState);
	}
	
	protected ElectroMagneticCoilBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
		super(pType, pPos, pBlockState);
	}

	@Override
	public Class<ElectroMagneticCoilBlockEntity> getMultiBlockTypeClass() {
		return ElectroMagneticCoilBlockEntity.class;
	}
	
	public ItemStack getWires() {
		return wires;
	}
	
	public double getInducedFieldStrength() {
		return MagnetismUtility.getMagneticFieldAt(this.level, this.worldPosition).getInducedFieldVector().length();
	}

	public double getVoltage() {
		return ElectricUtility.getVoltageBetween(this.level, new NodePos(this.worldPosition, 0), new NodePos(this.worldPosition, 0), 0, 1, this.nodeLanes[0], this.nodeLanes[1]);
	}
	
	public int getCoreBlockCount() {
		Vec3i size = Vec3i.fromVec(getMaxPos()).sub(Vec3i.fromVec(getMinPos())).abs().add(new Vec3i(1, 1, 1));
		return size.x * size.y * size.z;
	}
	
	public String[] getNodeLanes() {
		return nodeLanes;
	}
	
	public void setNodeLanes(String[] nodeLanes) {
		this.nodeLanes = nodeLanes;
		this.setChanged();
	}
	
	private int getElectricalConnectionCount() {
		Vec3i hp = Vec3i.fromVec(getMinPos());
		Vec3i lp = Vec3i.fromVec(getMaxPos());
		
		switch (this.getBlockState().getValue(BlockStateProperties.AXIS)) {
		case X: lp.setX(0); hp.setX(0); break;
		case Y: lp.setY(0); hp.setY(0); break;
		case Z: lp.setZ(0); hp.setZ(0); break;
		}
		
		return (Math.abs(hp.x - lp.x) + 1) * (Math.abs(hp.y - lp.y) + 1) * (Math.abs(hp.z - lp.z) + 1) * 2;
	}

	public NodePos[] getConnections() {
		return IntStream
				.range(0, getElectricalConnectionCount())
				.mapToObj(id -> new NodePos(this.worldPosition, id))
				.toArray(i -> new NodePos[i]);
	}

	public NodePos[] getConnectionsInput() {
		return IntStream
				.range(0, getElectricalConnectionCount() / 2)
				.mapToObj(id -> new NodePos(this.worldPosition, id))
				.toArray(i -> new NodePos[i]);
	}

	public NodePos[] getConnectionsOutput() {
		return IntStream
				.range(getElectricalConnectionCount() / 2, getElectricalConnectionCount())
				.mapToObj(id -> new NodePos(this.worldPosition, id))
				.toArray(i -> new NodePos[i]);
	}

	public ConduitNode[] getConduitNodes() {
		Vec3i size = Vec3i.fromVec(getMaxPos()).sub(Vec3i.fromVec(getMinPos())).add(1, 1, 1);
		
		return IntStream
				.range(0, getElectricalConnectionCount())
				.mapToObj(id -> {
					Vec3i pos;
					Vec3i offset;
					
					switch (this.getBlockState().getValue(BlockStateProperties.AXIS)) {
						case X: pos = new Vec3i(id, id, id); offset = new Vec3i(); break;
						case Y: pos = new Vec3i(id % size.x, (id / size.x / size.z) > 0 ? size.y : 0, (id / size.x) % size.z); offset = new Vec3i(8, 0, 8);  break;
						default:
						case Z: pos = new Vec3i(id, id, id); offset = new Vec3i();  break;
					}
					
					pos.subI(Vec3i.fromVec(this.worldPosition).sub(Vec3i.fromVec(getMinPos()).min(Vec3i.fromVec(getMaxPos()))));
					return pos.mul(16).add(offset);
				})
				.map(pos -> new ConduitNode(NodeTypes.ELECTRIC, ElectroMagneticCoilBlock.CONNECTION_PER_NODE, pos))
				.toArray(i -> new ConduitNode[i]);
	}
	
	public void setWires(ItemStack wires) {
		this.wires = wires;
		this.setChanged();
	}
	
	public Axis getAxis() {
		BlockState state = getBlockState();
		if (state.getBlock() instanceof ElectroMagneticCoilBlock) {
			return state.getValue(BlockStateProperties.AXIS);
		}
		return Axis.Y;
	}
	
	public Conduit getWireConduit() {
		if (this.wires.isEmpty()) return Conduits.NONE.get();
		if (this.wires.getItem() instanceof IConduitItem conduitItem) return conduitItem.getConduit();
		return Conduits.NONE.get();
	}
	
	public boolean isValidWireItem(ItemStack stack) {
		if (!wires.isEmpty() && stack.getItem() != stack.getItem()) return false;
		if (stack.getItem() instanceof IConduitItem conduitItem && conduitItem.getConduit() instanceof IElectricConduit) return true;
		return false;
	}

	public int getWiresPerWinding() {
		int windingLength = 4;
		switch (getAxis()) {
		case Y: windingLength = (this.getMaxPos().getX() - this.getMinPos().getX() + 1) * 2 + (this.getMaxPos().getZ() - this.getMinPos().getZ() + 1) * 2; break;
		case X: windingLength = (this.getMaxPos().getY() - this.getMinPos().getY() + 1) * 2 + (this.getMaxPos().getZ() - this.getMinPos().getZ() + 1) * 2; break;
		case Z: windingLength = (this.getMaxPos().getX() - this.getMinPos().getX() + 1) * 2 + (this.getMaxPos().getY() - this.getMinPos().getY() + 1) * 2; break;
		}
		return windingLength / Conduit.BLOCKS_PER_WIRE_ITEM;
	}
	
	public int getMaxWindings() {
		switch (getAxis()) {
		default:
		case Y: return (this.getMaxPos().getY() - this.getMinPos().getY() + 1) * 6;
		case X: return (this.getMaxPos().getX() - this.getMinPos().getX() + 1) * 6;
		case Z: return (this.getMaxPos().getZ() - this.getMinPos().getZ() + 1) * 6;
		}
	}
	
	public int getWindings() {
		return this.wires.getCount() / getWiresPerWinding();
	}
	
	public void dropWires() {
		if (!this.wires.isEmpty()) {
			GameUtility.dropItem(level, wires, Vec3f.fromVec(this.worldPosition).add(0.5F, 0.5F, 0.5F), 0.5F, 1F);
			this.wires = ItemStack.EMPTY;
		}
	}
	
	@Override
	public void saveAdditional(CompoundTag pTag) {
		super.saveAdditional(pTag);
		if (!this.isMaster()) return;
		pTag.put("Wires", wires.serializeNBT());
		pTag.putString("LiveWireLane", this.nodeLanes[0]);
		pTag.putString("NeutralWireLane", this.nodeLanes[1]);
	}
	
	@Override
	public void load(CompoundTag pTag) {
		super.load(pTag);
		this.wires = ItemStack.of(pTag.getCompound("Wires"));
		this.nodeLanes[0] = pTag.getString("LiveWireLane");
		this.nodeLanes[1] = pTag.getString("NeutralWireLane");
	}
	
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		tag.put("Wires", this.wires.serializeNBT());
		tag.putString("LiveWireLane", this.nodeLanes[0]);
		tag.putString("NeutralWireLane", this.nodeLanes[1]);
		return tag;
	}
	
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

}
