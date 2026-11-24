/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt.item;

import com.igteam.immersivegeology.common.block.belt.IGBeltBlock;
import com.igteam.immersivegeology.common.block.belt.IGRisingBeltBlock;
import com.igteam.immersivegeology.common.block.belt.helper.BeltCoverHandler;
import com.igteam.immersivegeology.common.block.belt.network.BeltLines;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class IGBeltItem extends BlockItem
{
	public static final int EXTEND_REACH = 7;
	private static final double NUDGE_CLEARANCE = 1e-3;

	public IGBeltItem(Block block, Properties properties)
	{
		super(block, properties);
	}

	@Override
	@Nullable
	public BlockPlaceContext updatePlacementContext(@NotNull BlockPlaceContext context)
	{
		Player player = context.getPlayer();
		if(player!=null&&player.isShiftKeyDown())
			return context;
		Level level = context.getLevel();
		BlockPos clicked = context.replacingClickedOnBlock()?context.getClickedPos(): context.getClickedPos().relative(context.getClickedFace().getOpposite());
		BlockState clickedState = level.getBlockState(clicked);
		if(getBlock() instanceof IGRisingBeltBlock&&clickedState.getBlock() instanceof IGRisingBeltBlock)
		{
			Direction facing = clickedState.getValue(IGBeltBlock.FACING);
			BlockPos top = columnTarget(level, clicked, context);
			return top==null?context: new LinePlaceContext(level, player, context.getHand(), context.getItemInHand(), top, facing);
		}
		if(!(getBlock() instanceof IGBeltBlock held)||!held.extendsLine())
			return context;
		if(!(clickedState.getBlock() instanceof IGBeltBlock line)||!line.extendsLine())
			return context;
		Direction facing = clickedState.getValue(IGBeltBlock.FACING);
		BlockPos target = extensionTarget(level, clicked, facing, context);
		if(target==null)
			return context;
		return new LinePlaceContext(level, player, context.getHand(), context.getItemInHand(), target, facing);
	}

	public boolean placesLines()
	{
		return getBlock().getClass()==IGBeltBlock.class;
	}

	public Optional<BeltLines.Line> line(Player player, BlockPlaceContext context)
	{
		if(!placesLines()||player.isShiftKeyDown())
			return Optional.empty();
		return BeltLines.line(context.getLevel(), player, getBlock(), context.getClickedPos());
	}

	@Override
	public @NotNull InteractionResult place(@NotNull BlockPlaceContext original)
	{
		Player player = original.getPlayer();
		if(player==null)
			return super.place(original);
		if(!placesLines())
			return placeSingle(original, player);
		BlockPlaceContext context = updatePlacementContext(original);
		if(context==null)
			return InteractionResult.FAIL;
		Level level = context.getLevel();
		Optional<BeltLines.Line> line = line(player, context);
		if(line.isPresent()&&!line.get().complete())
			return InteractionResult.FAIL;
		BlockPos pos = context.getClickedPos();
		ItemStack stack = context.getItemInHand();
		if(line.isPresent())
			context = new LinePlaceContext(level, player, context.getHand(), stack, pos, line.get().facing());
		InteractionResult result = super.place(context);
		if(!result.consumesAction())
			return result;
		BlockState placed = level.getBlockState(pos);
		if(!BeltLines.isFilled(placed, getBlock(), placed.getBlock() instanceof IGBeltBlock?placed.getValue(IGBeltBlock.FACING): Direction.NORTH))
		{
			BeltLines.forget(player, level);
			return result;
		}
		Direction facing = placed.getValue(IGBeltBlock.FACING);
		nudge(level, pos, placed);
		if(line.isPresent()&&!level.isClientSide)
		{
			int covers = fill(level, player, context.getHand(), stack, line.get(), coverBudget(player));
			if(covers > 0)
				cover(level, pos, player);
			for(int step = 0; step <= line.get().distance(); step++)
				nudge(level, line.get().cell(step), level.getBlockState(line.get().cell(step)));
		}
		else if(line.isEmpty()&&!level.isClientSide)
			cover(level, pos, player);
		BeltLines.remember(player, level, pos, facing, getBlock());
		return result;
	}

	private InteractionResult placeSingle(BlockPlaceContext original, Player player)
	{
		BlockPlaceContext context = updatePlacementContext(original);
		if(context==null)
			return InteractionResult.FAIL;
		BlockPos pos = context.getClickedPos();
		InteractionResult result = super.place(context);
		if(result.consumesAction()&&!context.getLevel().isClientSide)
			cover(context.getLevel(), pos, player);
		return result;
	}

	private void cover(Level level, BlockPos pos, Player player)
	{
		BeltCoverHandler.coverPlaced(level, pos, player, getBlock());
	}

	private int fill(Level level, Player player, InteractionHand hand, ItemStack stack, BeltLines.Line line, int covers)
	{
		boolean free = player.getAbilities().instabuild;
		Optional<IGBeltBlock> covered = BeltCoverHandler.counterpart(getBlock());
		for(int step = 1; step < line.distance(); step++)
		{
			BlockPos cell = line.cell(step);
			if(BeltLines.isFilled(level.getBlockState(cell), getBlock(), line.facing()))
				continue;
			if(!free&&BeltLines.count(player.getInventory(), this)==0)
				return covers;
			boolean cover = covers > 0&&covered.isPresent();
			Block block = cover?covered.get(): getBlock();
			LinePlaceContext context = new LinePlaceContext(level, player, hand, stack, cell, line.facing());
			BlockState state = block.getStateForPlacement(context);
			if(state==null||!canPlace(context, state)||!level.setBlock(cell, state, Block.UPDATE_ALL_IMMEDIATE))
				continue;
			nudge(level, cell, level.getBlockState(cell));
			if(!free)
				BeltLines.consume(player.getInventory(), this, stack);
			if(cover)
			{
				covers--;
				useCover(player);
			}
		}
		return covers;
	}

	private int coverBudget(Player player)
	{
		return BeltCoverHandler.offhandCovers(player, getBlock());
	}

	private static void useCover(Player player)
	{
		BeltCoverHandler.useOffhandCover(player);
	}

	@Override
	protected boolean canPlace(@NotNull BlockPlaceContext context, @NotNull BlockState state)
	{
		if(!placesLines())
			return super.canPlace(context, state);
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		if(mustSurvive()&&!state.canSurvive(level, pos))
			return false;
		VoxelShape shape = state.getCollisionShape(level, pos, CollisionContext.empty()).move(pos.getX(), pos.getY(), pos.getZ());
		if(shape.isEmpty())
			return true;
		return level.getEntities((Entity)null, shape.bounds(), entity -> !entity.isRemoved()&&entity.blocksBuilding&&!(entity instanceof Player)
				&&Shapes.joinIsNotEmpty(shape, Shapes.create(entity.getBoundingBox()), BooleanOp.AND)).isEmpty();
	}

	private static void nudge(Level level, BlockPos pos, BlockState state)
	{
		VoxelShape shape = state.getCollisionShape(level, pos, CollisionContext.empty()).move(pos.getX(), pos.getY(), pos.getZ());
		if(shape.isEmpty())
			return;
		for(LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, shape.bounds()))
		{
			AABB box = entity.getBoundingBox();
			double top = shape.toAabbs().stream().filter(part -> part.intersects(box)).mapToDouble(part -> part.maxY).max().orElse(box.minY);
			if(top > box.minY)
				entity.teleportRelative(0, top-box.minY+NUDGE_CLEARANCE, 0);
		}
	}

	@Nullable
	public static BlockPos columnTarget(Level level, BlockPos start, BlockPlaceContext context)
	{
		for(int step = 1; step <= EXTEND_REACH; step++)
		{
			BlockPos pos = start.above(step);
			if(!level.isInWorldBounds(pos))
				return null;
			BlockState state = level.getBlockState(pos);
			if(state.getBlock() instanceof IGRisingBeltBlock)
				continue;
			return state.canBeReplaced(context)?pos: null;
		}
		return null;
	}

	@Nullable
	public static BlockPos extensionTarget(Level level, BlockPos start, Direction facing, BlockPlaceContext context)
	{
		for(int step = 1; step <= EXTEND_REACH; step++)
		{
			BlockPos pos = start.relative(facing, step);
			if(!level.isInWorldBounds(pos)||!level.getWorldBorder().isWithinBounds(pos))
				return null;
			BlockState state = level.getBlockState(pos);
			if(state.getBlock() instanceof IGBeltBlock belt&&belt.extendsLine()&&state.getValue(IGBeltBlock.FACING)==facing)
				continue;
			return state.canBeReplaced(context)?pos: null;
		}
		return null;
	}

	private static final class LinePlaceContext extends BlockPlaceContext
	{
		private final Direction facing;

		private LinePlaceContext(Level level, @Nullable Player player, InteractionHand hand, ItemStack stack, BlockPos target, Direction facing)
		{
			super(level, player, hand, stack, new BlockHitResult(Vec3.atCenterOf(target), Direction.UP, target, false));
			this.facing = facing;
		}

		@Override
		public @NotNull Direction getHorizontalDirection()
		{
			return facing;
		}

		@Override
		public @NotNull Direction getNearestLookingDirection()
		{
			return facing;
		}
	}
}
