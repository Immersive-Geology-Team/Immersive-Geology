/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt;

import com.igteam.immersivegeology.common.block.belt.entity.IGBeltBlockEntity;
import com.igteam.immersivegeology.common.block.belt.entity.IGSplitterBlockEntity;
import com.igteam.immersivegeology.common.block.belt.helper.BeltStyle;
import com.igteam.immersivegeology.common.block.belt.helper.BeltView;
import com.igteam.immersivegeology.common.block.belt.helper.SplitterHalf;
import com.igteam.immersivegeology.common.block.belt.helper.SplitterPriority;
import com.igteam.immersivegeology.common.block.belt.network.BeltNode;
import com.igteam.immersivegeology.common.block.belt.network.LanePath;
import com.igteam.immersivegeology.common.block.belt.shape.*;
import com.igteam.immersivegeology.common.block.belt.transport.BeltTransport;
import com.igteam.immersivegeology.common.block.belt.transport.Lane;
import com.igteam.immersivegeology.common.block.belt.transport.LaneItem;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IGSplitterBlock extends IGBeltBlock
{
	public static final EnumProperty<SplitterHalf> HALF = EnumProperty.create("half", SplitterHalf.class);

	public IGSplitterBlock(BeltStyle style)
	{
		super(style);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(FACING, HALF);
	}

	@Override
	protected BlockState initialState(BlockState state)
	{
		return state.setValue(HALF, SplitterHalf.RIGHT);
	}

	@Override
	protected VoxelShape computeShape(BlockState state)
	{
		return BeltShapes.splitter(style().covered());
	}

	@Override
	@Nullable
	public BeltNode beltNode(BlockState state)
	{
		return BeltNode.splitter(state.getValue(FACING));
	}

	@Override
	public String modelPiece(BlockState state)
	{
		return BeltModels.splitterPiece(style());
	}

	@Override
	public boolean rendersModel(BlockState state)
	{
		return state.getValue(HALF)==SplitterHalf.RIGHT;
	}

	@Override
	public String itemModelPiece()
	{
		return BeltModels.splitterPiece(style());
	}

	public static Direction partnerDirection(BlockState state)
	{
		Direction facing = state.getValue(FACING);
		return state.getValue(HALF)==SplitterHalf.RIGHT?facing.getCounterClockWise(): facing.getClockWise();
	}

	public static BlockPos masterPos(BlockState state, BlockPos pos)
	{
		return state.getValue(HALF)==SplitterHalf.RIGHT?pos: pos.relative(partnerDirection(state));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		Direction facing = context.getHorizontalDirection();
		BlockPos pos = context.getClickedPos();
		Level level = context.getLevel();
		for(SplitterHalf half : new SplitterHalf[]{SplitterHalf.RIGHT, SplitterHalf.LEFT})
		{
			BlockState candidate = defaultBlockState().setValue(FACING, facing).setValue(HALF, half);
			BlockPos partner = pos.relative(partnerDirection(candidate));
			if(level.getBlockState(partner).canBeReplaced(context)&&level.getWorldBorder().isWithinBounds(partner))
				return candidate;
		}
		return null;
	}

	@Override
	public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack)
	{
		super.setPlacedBy(level, pos, state, placer, stack);
		if(!level.isClientSide)
			level.setBlock(pos.relative(partnerDirection(state)), state.setValue(HALF, state.getValue(HALF).other()), Block.UPDATE_ALL);
	}

	@Override
	@SuppressWarnings("deprecation")
	public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighbour, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighbourPos)
	{
		if(direction==partnerDirection(state)&&!isPartner(state, neighbour))
			return Blocks.AIR.defaultBlockState();
		return super.updateShape(state, direction, neighbour, level, pos, neighbourPos);
	}

	private boolean isPartner(BlockState state, BlockState other)
	{
		return other.is(this)&&other.getValue(FACING)==state.getValue(FACING)&&other.getValue(HALF)!=state.getValue(HALF);
	}

	@Override
	public void playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player)
	{
		if(!level.isClientSide&&player.isCreative()&&state.getValue(HALF)==SplitterHalf.LEFT)
		{
			BlockPos partner = pos.relative(partnerDirection(state));
			BlockState partnerState = level.getBlockState(partner);
			if(isPartner(state, partnerState))
			{
				level.setBlock(partner, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL|Block.UPDATE_SUPPRESS_DROPS);
				level.levelEvent(player, 2001, partner, Block.getId(partnerState));
			}
		}
		super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	@SuppressWarnings("deprecation")
	public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit)
	{
		if(player.isSecondaryUseActive())
			return super.use(state, level, pos, player, hand, hit);
		InteractionResult taken = takeItem(level, pos, state, player);
		if(taken!=InteractionResult.PASS)
			return taken;
		if(!level.isClientSide&&player instanceof ServerPlayer serverPlayer)
		{
			BlockPos master = masterPos(state, pos);
			if(level.getBlockEntity(master) instanceof IGSplitterBlockEntity splitter)
				NetworkHooks.openScreen(serverPlayer, splitter, master);
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Override
	@SuppressWarnings("deprecation")
	public @NotNull BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror)
	{
		if(mirror==Mirror.NONE)
			return state;
		return state.setValue(FACING, mirror.mirror(state.getValue(FACING))).setValue(HALF, state.getValue(HALF).other());
	}

	@Override
	protected BlockState resolve(BeltView view, BlockPos pos, BlockState state)
	{
		return state;
	}

	@Override
	protected void refresh(Level level, BlockPos pos, BlockState state, BeltView view)
	{
	}

	@Override
	@Nullable
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state)
	{
		return new IGSplitterBlockEntity(pos, state);
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

	private static BlockPos halfPos(BlockState state, BlockPos pos, SplitterHalf half)
	{
		return state.getValue(HALF)==half?pos: pos.relative(partnerDirection(state));
	}

	private ItemStack deliverFrom(Level level, BlockPos half, Lane lane, ItemStack stack, boolean simulate)
	{
		if(stack.isEmpty())
			return ItemStack.EMPTY;
		BlockState state = level.getBlockState(half);
		if(!state.is(this))
			return stack;
		return BeltTransport.deliver(level, half, state, this, lane, stack, simulate);
	}

	private static boolean waitingAtEnd(Level level, BlockPos half, Lane lane)
	{
		if(!(level.getBlockEntity(half) instanceof IGBeltBlockEntity belt))
			return false;
		var items = belt.lane(lane);
		return !items.isEmpty()&&items.get(0).progress() >= 1;
	}

	public static final float ROUTING_POINT = 0.5f;

	@Override
	public float holdPoint(BlockState state, LaneItem item)
	{
		return item.routed()?1: ROUTING_POINT;
	}

	@Override
	public ItemStack deliver(Level level, BlockPos pos, BlockState state, Lane lane, ItemStack stack, boolean simulate)
	{
		if(level.getBlockEntity(masterPos(state, pos)) instanceof IGSplitterBlockEntity master&&master.inputPriority()!=SplitterPriority.NONE)
		{
			BlockPos preferred = halfPos(state, pos, master.inputPriority()==SplitterPriority.LEFT?SplitterHalf.LEFT: SplitterHalf.RIGHT);
			if(!preferred.equals(pos)&&waitingAtEnd(level, preferred, lane))
				return stack;
		}
		ItemStack rest = BeltTransport.deliver(level, pos, state, this, lane, stack, simulate);
		if(rest.getCount()!=stack.getCount())
			return rest;
		if(level.getBlockEntity(masterPos(state, pos)) instanceof IGSplitterBlockEntity master&&master.hasFilter()&&master.outputPriority()!=SplitterPriority.NONE)
			return rest;
		return deliverFrom(level, halfPos(state, pos, state.getValue(HALF).other()), lane, stack, simulate);
	}

	@Override
	public void onItemMoved(Level level, BlockPos pos, BlockState state, IGBeltBlockEntity belt, Lane lane, LaneItem item, float before)
	{
		if(item.routed()||item.progress() < ROUTING_POINT)
			return;
		if(!(level.getBlockEntity(masterPos(state, pos)) instanceof IGSplitterBlockEntity master))
		{
			route(item, belt);
			return;
		}
		SplitterHalf own = state.getValue(HALF);
		BlockPos otherPos = halfPos(state, pos, own.other());
		BlockState otherState = level.getBlockState(otherPos);
		if(!otherState.is(this)||!(level.getBlockEntity(otherPos) instanceof IGBeltBlockEntity other))
		{
			route(item, belt);
			return;
		}
		LanePath otherPath = lanePath(otherState, lane, other.isSideLoad());
		ItemStack stack = item.stack();
		SplitterPriority output = master.outputPriority();
		if(output!=SplitterPriority.NONE)
		{
			SplitterHalf chosen = output==SplitterPriority.LEFT?SplitterHalf.LEFT: SplitterHalf.RIGHT;
			if(master.hasFilter())
			{
				SplitterHalf wanted = stack.is(master.filter().getItem())?chosen: chosen.other();
				if(wanted==own)
					route(item, belt);
				else if(other.insert(lane, ROUTING_POINT, stack, otherPath, true, true).isEmpty())
					move(item, belt, other, lane, stack, otherPath);
				return;
			}
			boolean ownFits = deliverFrom(level, pos, lane, stack, true).isEmpty();
			boolean wantOther = chosen!=own||!ownFits;
			if(wantOther&&canTake(level, otherPos, otherState, other, lane, stack, otherPath))
				move(item, belt, other, lane, stack, otherPath);
			else
				route(item, belt);
			return;
		}
		int count = stack.getCount();
		int toOther = master.alternateFor(lane)?count-count/2: count/2;
		ItemStack otherPart = ItemHandlerHelper.copyStackWithSize(stack, toOther);
		ItemStack ownPart = ItemHandlerHelper.copyStackWithSize(stack, count-toOther);
		boolean ownFits = ownPart.isEmpty()||deliverFrom(level, pos, lane, ownPart, true).isEmpty();
		boolean otherFits = otherPart.isEmpty()||canTake(level, otherPos, otherState, other, lane, otherPart, otherPath);
		if(ownFits&&otherFits)
		{
			if(!otherPart.isEmpty())
				other.insert(lane, ROUTING_POINT, otherPart, otherPath, false, true);
			item.setStack(ownPart);
			if(count%2==1)
				master.flipAlternate(lane);
			route(item, belt);
		}
		else if(!ownFits&&canTake(level, otherPos, otherState, other, lane, stack, otherPath))
			move(item, belt, other, lane, stack, otherPath);
		else
			route(item, belt);
	}

	private boolean canTake(Level level, BlockPos half, BlockState halfState, IGBeltBlockEntity belt, Lane lane, ItemStack stack, LanePath path)
	{
		return belt.insert(lane, ROUTING_POINT, stack, path, true, true).isEmpty()&&deliverFrom(level, half, lane, stack, true).isEmpty();
	}

	private static void route(LaneItem item, IGBeltBlockEntity belt)
	{
		item.setRouted(true);
		belt.markLanesChanged();
	}

	private static void move(LaneItem item, IGBeltBlockEntity from, IGBeltBlockEntity to, Lane lane, ItemStack stack, LanePath path)
	{
		to.insert(lane, ROUTING_POINT, stack, path, false, true);
		item.setStack(ItemStack.EMPTY);
		item.setRouted(true);
		from.markLanesChanged();
	}
}
