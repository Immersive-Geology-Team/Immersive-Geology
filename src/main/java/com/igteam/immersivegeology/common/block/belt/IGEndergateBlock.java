/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt;

import com.igteam.immersivegeology.common.block.belt.entity.IGBeltBlockEntity;
import com.igteam.immersivegeology.common.block.belt.entity.IGEndergateBlockEntity;
import com.igteam.immersivegeology.common.block.belt.helper.*;
import com.igteam.immersivegeology.common.block.belt.network.BeltNode;
import com.igteam.immersivegeology.common.block.belt.shape.BeltModels;
import com.igteam.immersivegeology.common.block.belt.shape.BeltShape;
import com.igteam.immersivegeology.common.block.belt.shape.BeltShapeResolver;
import com.igteam.immersivegeology.common.block.belt.shape.BeltShapes;
import com.igteam.immersivegeology.common.block.belt.transport.BeltTransport;
import com.igteam.immersivegeology.common.block.belt.transport.Lane;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class IGEndergateBlock extends IGBeltBlock
{
	public static final EnumProperty<EndergateRole> ROLE = EnumProperty.create("role", EndergateRole.class);
	public static final EnumProperty<BeltShape> INPUTS = EnumProperty.create("inputs", BeltShape.class, BeltShape.STRAIGHT, BeltShape.FROM_LEFT, BeltShape.FROM_RIGHT, BeltShape.FROM_BOTH);
	private static final float ENTRY_SIDE_POINT = 0.25f;
	private static final float EXIT_SIDE_POINT = 0.75f;
	private static final float HIDDEN_IN_TUNNEL = 0.75f;

	private static final double TRAIL_HEIGHT = 0.35;
	private static final double TRAIL_SPACING = 0.25;
	private static final int TRAIL_BURST = 12;

	private final int range;

	public IGEndergateBlock(BeltStyle style, int range)
	{
		super(style);
		this.range = range;
	}

	public int range()
	{
		return range;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(FACING, ROLE, INPUTS);
	}

	@Override
	protected BlockState initialState(BlockState state)
	{
		return state.setValue(ROLE, EndergateRole.ENTRY).setValue(INPUTS, BeltShape.STRAIGHT);
	}

	@Override
	protected VoxelShape computeShape(BlockState state)
	{
		Direction facing = state.getValue(FACING);
		return BeltShapes.endergate(style().covered(), state.getValue(ROLE)==EndergateRole.EXIT?facing: facing.getOpposite());
	}

	@Override
	@Nullable
	public BeltNode beltNode(BlockState state)
	{
		Direction facing = state.getValue(FACING);
		if(state.getValue(ROLE)==EndergateRole.ENTRY)
			return BeltNode.endergateEntry(facing);
		return BeltNode.endergateExit(facing);
	}

	@Override
	public String modelPiece(BlockState state)
	{
		return BeltModels.endergatePiece(style(), state.getValue(ROLE), state.getValue(INPUTS));
	}

	@Override
	public String itemModelPiece()
	{
		return BeltModels.endergatePiece(style(), EndergateRole.ENTRY, BeltShape.STRAIGHT);
	}

	public Optional<BlockPos> partner(BlockGetter level, BlockPos pos, BlockState state)
	{
		return EndergateLinks.partner(level, pos, state, range);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		BlockState placed = placementState(context);
		BlockPos pos = context.getClickedPos();
		return resolve(new LevelBeltView(context.getLevel(), pos, placed), pos, placed);
	}

	private BlockState placementState(BlockPlaceContext context)
	{
		boolean unlocked = context.getPlayer()!=null&&context.getPlayer().isShiftKeyDown();
		if(!unlocked)
		{
			Optional<EndergateLinks.Lock> lock = EndergateLinks.lock(context.getLevel(), context.getItemInHand(), context.getClickedPos(), range);
			if(lock.isPresent()&&lock.get().distance() <= range)
				return defaultBlockState().setValue(FACING, lock.get().facing()).setValue(ROLE, EndergateRole.EXIT);
		}
		Direction facing = context.getHorizontalDirection();
		EndergateRole role = EndergateLinks.roleFor(context.getLevel(), context.getClickedPos(), facing, range);
		return defaultBlockState().setValue(FACING, facing).setValue(ROLE, role);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean moving)
	{
		super.onPlace(state, level, pos, oldState, moving);
		if(level instanceof ServerLevel server&&!oldState.is(this)&&state.getValue(ROLE)==EndergateRole.EXIT)
			partner(level, pos, state).ifPresent(entry -> connectionTrail(server, entry, pos));
	}

	private static void connectionTrail(ServerLevel level, BlockPos entry, BlockPos exit)
	{
		Vec3 from = Vec3.atBottomCenterOf(entry).add(0, TRAIL_HEIGHT, 0);
		Vec3 to = Vec3.atBottomCenterOf(exit).add(0, TRAIL_HEIGHT, 0);
		int steps = Math.max(1, (int)Math.ceil(from.distanceTo(to)/TRAIL_SPACING));
		for(int i = 0; i <= steps; i++)
		{
			Vec3 point = from.lerp(to, i/(double)steps);
			level.sendParticles(ParticleTypes.END_ROD, point.x, point.y, point.z, 1, 0, 0, 0, 0);
		}
		for(BlockPos end : new BlockPos[]{entry, exit})
			level.sendParticles(ParticleTypes.REVERSE_PORTAL, end.getX()+.5, end.getY()+.5, end.getZ()+.5, TRAIL_BURST, .25, .2, .25, .05);
	}

	@Override
	protected BlockState resolve(BeltView view, BlockPos pos, BlockState state)
	{
		return state.setValue(INPUTS, BeltShapeResolver.resolve(view, pos).shape().sideInputs());
	}

	@Override
	protected void refresh(Level level, BlockPos pos, BlockState state, BeltView view)
	{
		BlockState resolved = resolve(view, pos, state);
		if(resolved!=state)
			level.setBlock(pos, resolved, Block.UPDATE_CLIENTS);
	}

	@Override
	public float sideEntry(BlockState state)
	{
		return state.getValue(ROLE)==EndergateRole.ENTRY?ENTRY_SIDE_POINT: EXIT_SIDE_POINT;
	}

	@Override
	public boolean acceptsSideLane(BlockState state, Direction side, Direction laneSide)
	{
		Direction facing = state.getValue(FACING);
		return laneSide==(state.getValue(ROLE)==EndergateRole.ENTRY?facing.getOpposite(): facing);
	}

	@Override
	public boolean takesBothSides(BlockState state)
	{
		return true;
	}

	@Override
	public boolean drawsItem(BlockState state, float progress)
	{
		return state.getValue(ROLE)!=EndergateRole.ENTRY||progress <= HIDDEN_IN_TUNNEL;
	}

	@Override
	public boolean extendsLine()
	{
		return false;
	}

	@Override
	public BeltShape pathShape(BlockState state)
	{
		return BeltShape.STRAIGHT;
	}

	@Override
	public boolean acceptsItemsFrom(BlockState state, Direction side)
	{
		BeltNode node = beltNode(state);
		if(node==null)
			return false;
		return side==node.facing().getOpposite()?node.acceptsBack(): side!=node.facing();
	}

	@Override
	public ItemStack deliver(Level level, BlockPos pos, BlockState state, Lane lane, ItemStack stack, boolean simulate)
	{
		if(state.getValue(ROLE)!=EndergateRole.ENTRY)
			return BeltTransport.deliver(level, pos, state, this, lane, stack, simulate);
		Optional<BlockPos> exit = partner(level, pos, state);
		if(exit.isEmpty()||!(level.getBlockEntity(pos) instanceof IGEndergateBlockEntity entry))
			return stack;
		return entry.send(lane, stack, pos.distManhattan(exit.get()), simulate);
	}

	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state, IGBeltBlockEntity belt)
	{
		if(belt instanceof IGEndergateBlockEntity entry&&state.getValue(ROLE)==EndergateRole.ENTRY)
			entry.tickTransit(level, state, this);
	}

	@Override
	@Nullable
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state)
	{
		return new IGEndergateBlockEntity(pos, state);
	}
}
