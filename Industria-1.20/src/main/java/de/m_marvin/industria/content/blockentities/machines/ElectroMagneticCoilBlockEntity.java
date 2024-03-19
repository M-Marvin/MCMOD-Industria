package de.m_marvin.industria.content.blockentities.machines;

import java.util.stream.IntStream;

import de.m_marvin.industria.content.blocks.machines.ElectroMagneticCoilBlock;
import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.conduits.types.items.IConduitItem;
import de.m_marvin.industria.core.electrics.types.conduits.IElectricConduit;
import de.m_marvin.industria.core.registries.Conduits;
import de.m_marvin.industria.core.registries.NodeTypes;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.blocks.DynamicMultiBlockEntity;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
	protected ItemStack wiresPrimary = ItemStack.EMPTY;
	protected ItemStack wiresSecundary = ItemStack.EMPTY;
	
	public ElectroMagneticCoilBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(ModBlockEntityTypes.ELECTRO_MAGNETIC_COIL.get(), pPos, pBlockState);
		this.isMaster = true;
	}
	
	protected ElectroMagneticCoilBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
		super(pType, pPos, pBlockState);
		this.isMaster = true;
	}

	@Override
	public Class<ElectroMagneticCoilBlockEntity> getMultiBlockTypeClass() {
		return ElectroMagneticCoilBlockEntity.class;
	}
	
	public ItemStack getWiresPrimary() {
		return wiresPrimary;
	}
	
	public ItemStack getWiresSecundary() {
		return wiresSecundary;
	}

	public void setWiresPrimary(ItemStack wires) {
		this.wiresPrimary = wires;
	}
	
	public void setWiresSecundary(ItemStack wires) {
		this.wiresSecundary = wires;
		this.setChanged();
	}
	
	public Direction getFacing() {
		BlockState state = getBlockState();
		if (state.getBlock() instanceof ElectroMagneticCoilBlock) {
			return state.getValue(BlockStateProperties.FACING);
		}
		return Direction.UP;
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
		
		switch (this.getBlockState().getValue(BlockStateProperties.FACING).getAxis()) {
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

	public ConduitNode[] getConduitNodes() {
		Vec3i size = Vec3i.fromVec(getMaxPos()).sub(Vec3i.fromVec(getMinPos())).add(1, 1, 1);
		
		return IntStream
				.range(0, getElectricalConnectionCount())
				.mapToObj(id -> {
					Vec3i pos;
					Vec3i offset;
					Direction facing = getFacing();
					
					switch (facing.getAxis()) {
						case X: pos = new Vec3i(((id / size.z / size.y) > 0 ^ (facing == Direction.WEST)) ? size.x : 0, (id / size.z) % size.y, id % size.z); offset = new Vec3i(0, 8, 8); break;
						case Y: pos = new Vec3i(id % size.x, ((id / size.x / size.z) > 0 ^ (facing == Direction.DOWN)) ? size.y : 0, (id / size.x) % size.z); offset = new Vec3i(8, 0, 8); break;
						default:
						case Z: pos = new Vec3i(id % size.x, (id / size.x) % size.y, ((id / size.x / size.y) > 0 ^ (facing == Direction.NORTH)) ? size.z : 0); offset = new Vec3i(8, 8, 0); break;
					}
					
					pos.subI(Vec3i.fromVec(this.worldPosition).sub(Vec3i.fromVec(getMinPos()).min(Vec3i.fromVec(getMaxPos()))));
					return pos.mul(16).add(offset);
				})
				.map(pos -> new ConduitNode(NodeTypes.ELECTRIC, ElectroMagneticCoilBlock.CONNECTION_PER_NODE, pos))
				.toArray(i -> new ConduitNode[i]);
	}
	
	public Conduit getWireConduitPrimary() {
		if (this.wiresPrimary.isEmpty()) return Conduits.NONE.get();
		if (this.wiresPrimary.getItem() instanceof IConduitItem conduitItem) return conduitItem.getConduit();
		return Conduits.NONE.get();
	}

	public Conduit getWireConduitSecundary() {
		if (this.wiresSecundary.isEmpty()) return Conduits.NONE.get();
		if (this.wiresSecundary.getItem() instanceof IConduitItem conduitItem) return conduitItem.getConduit();
		return Conduits.NONE.get();
	}
	
	public boolean isValidWireItemPrimary(ItemStack stack) {
		if (stack.getItem() instanceof IConduitItem conduitItem && conduitItem.getConduit() instanceof IElectricConduit) {
			if (this.wiresPrimary.isEmpty()) return true;
			if (this.wiresPrimary.getItem() == conduitItem) return true;
		}
		return false;
	}
	
	public boolean isValidWireItemSecundary(ItemStack stack) {
		if (stack.getItem() instanceof IConduitItem conduitItem && conduitItem.getConduit() instanceof IElectricConduit) {
			if (this.wiresSecundary.isEmpty()) return true;
			if (this.wiresSecundary.getItem() == conduitItem) return true;
		}
		return false;
	}
	
	public int getWiresPerWinding() {
		int windingLength = 4;
		switch (getFacing().getAxis()) {
		case Y: windingLength = (this.getMaxPos().getX() - this.getMinPos().getX() + 1) * 2 + (this.getMaxPos().getZ() - this.getMinPos().getZ() + 1) * 2; break;
		case X: windingLength = (this.getMaxPos().getY() - this.getMinPos().getY() + 1) * 2 + (this.getMaxPos().getZ() - this.getMinPos().getZ() + 1) * 2; break;
		case Z: windingLength = (this.getMaxPos().getX() - this.getMinPos().getX() + 1) * 2 + (this.getMaxPos().getY() - this.getMinPos().getY() + 1) * 2; break;
		}
		return windingLength / Conduit.BLOCKS_PER_WIRE_ITEM;
	}
	
	public int getMaxWindings() {
		switch (getFacing().getAxis()) {
		default:
		case Y: return (this.getMaxPos().getY() - this.getMinPos().getY() + 1) * 6;
		case X: return (this.getMaxPos().getX() - this.getMinPos().getX() + 1) * 6;
		case Z: return (this.getMaxPos().getZ() - this.getMinPos().getZ() + 1) * 6;
		}
	}
	
	public int getWindingsPrimary() {
		return this.wiresPrimary.getCount() / getWiresPerWinding();
	}

	public int getWindingsSecundary() {
		return this.wiresSecundary.getCount() / getWiresPerWinding();
	}
	
	public void dropWires() {
		if (!this.wiresPrimary.isEmpty()) {
			GameUtility.dropItem(level, wiresPrimary, Vec3f.fromVec(this.worldPosition).add(0.5F, 0.5F, 0.5F), 0.5F, 1F);
			this.wiresPrimary = ItemStack.EMPTY;
		}
		if (!this.wiresSecundary.isEmpty()) {
			GameUtility.dropItem(level, wiresSecundary, Vec3f.fromVec(this.worldPosition).add(0.5F, 0.5F, 0.5F), 0.5F, 1F);
			this.wiresSecundary = ItemStack.EMPTY;
		}
	}
	
	@Override
	public void saveAdditional(CompoundTag pTag) {
		super.saveAdditional(pTag);
		if (!this.isMaster()) return;
		pTag.put("WiresPrimary", wiresPrimary.serializeNBT());
		pTag.put("WiresSecundary", wiresSecundary.serializeNBT());
		pTag.putString("LiveWireLane", this.nodeLanes[0]);
		pTag.putString("NeutralWireLane", this.nodeLanes[1]);
	}
	
	@Override
	public void load(CompoundTag pTag) {
		super.load(pTag);
		this.wiresPrimary = ItemStack.of(pTag.getCompound("WiresPrimary"));
		this.wiresSecundary = ItemStack.of(pTag.getCompound("WiresSecundary"));
		this.nodeLanes[0] = pTag.getString("LiveWireLane");
		this.nodeLanes[1] = pTag.getString("NeutralWireLane");
	}
	
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		tag.put("WiresPrimary", this.wiresPrimary.serializeNBT());
		tag.put("WiresSecundary", this.wiresSecundary.serializeNBT());
		tag.putString("LiveWireLane", this.nodeLanes[0]);
		tag.putString("NeutralWireLane", this.nodeLanes[1]);
		return tag;
	}
	
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

}
