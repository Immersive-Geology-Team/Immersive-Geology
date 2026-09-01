/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.entity.device;

import blusunrize.immersiveengineering.common.blocks.ticking.IEServerTickableBE;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IGMetalDetector extends Block implements EntityBlock
{
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

	private static final VoxelShape UPPER_SHAPE = Block.box(0, 0, 0, 16, 12, 16);

	public IGMetalDetector()
	{
		super(BlockBehaviour.Properties.of()
				.mapColor(MapColor.METAL)
				.sound(SoundType.METAL)
				.requiresCorrectToolForDrops()
				.strength(3.5f, 12f)
				.noOcclusion()
				.isViewBlocking((state, level, pos) -> false)
				.pushReaction(PushReaction.BLOCK));
		registerDefaultState(getStateDefinition().any()
				.setValue(FACING, Direction.NORTH)
				.setValue(HALF, DoubleBlockHalf.LOWER));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(FACING, HALF);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		BlockPos pos = context.getClickedPos();
		if(pos.getY() >= context.getLevel().getMaxBuildHeight()-1) return null;
		if(!context.getLevel().getBlockState(pos.above()).canBeReplaced(context)) return null;

		return defaultBlockState()
				.setValue(FACING, context.getHorizontalDirection().getOpposite())
				.setValue(HALF, DoubleBlockHalf.LOWER);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
	{
		level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
	}

	@Override
	public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighbour,
										   LevelAccessor level, BlockPos pos, BlockPos neighbourPos)
	{
		DoubleBlockHalf half = state.getValue(HALF);
		boolean towardsOtherHalf = direction.getAxis()==Direction.Axis.Y
				&&(half==DoubleBlockHalf.LOWER)==(direction==Direction.UP);
		if(!towardsOtherHalf) return super.updateShape(state, direction, neighbour, level, pos, neighbourPos);

		boolean stillPaired = neighbour.is(this)&&neighbour.getValue(HALF)!=half;
		return stillPaired?state: net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
	{
		if(state.getValue(HALF)==DoubleBlockHalf.UPPER)
		{
			BlockState below = level.getBlockState(pos.below());
			return below.is(this)&&below.getValue(HALF)==DoubleBlockHalf.LOWER;
		}
		return super.canSurvive(state, level, pos);
	}

	@Override
	public void playerWillDestroy(Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player)
	{
		// Mirrors vanilla's double block behaviour
		if(!level.isClientSide&&player.isCreative())
		{
			if(state.getValue(HALF)==DoubleBlockHalf.UPPER)
			{
				BlockPos below = pos.below();
				BlockState belowState = level.getBlockState(below);
				if(belowState.is(this)&&belowState.getValue(HALF)==DoubleBlockHalf.LOWER)
				{
					level.setBlock(below, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), Block.UPDATE_SUPPRESS_DROPS|Block.UPDATE_ALL);
					level.levelEvent(player, 2001, below, Block.getId(belowState));
				}
			}
		}
		super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public boolean useShapeForLightOcclusion(@NotNull BlockState state)
	{
		return true;
	}

	@Override
	public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos)
	{
		return state.getValue(HALF)==DoubleBlockHalf.UPPER;
	}

	@Override
	public float getShadeBrightness(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos)
	{
		return 1.0f;
	}

	@Override
	public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean moving)
	{
		if(!state.is(newState.getBlock())&&state.getValue(HALF)==DoubleBlockHalf.LOWER
				&&level.getBlockEntity(pos) instanceof IGMetalDetectorEntity detector)
		{
			ItemStackHandler slots = detector.getMapSlots();
			for(int slot = 0; slot < slots.getSlots(); slot++)
			{
				Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), slots.getStackInSlot(slot));
			}
		}
		super.onRemove(state, level, pos, newState, moving);
	}

	@Override
	public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context)
	{
		return state.getValue(HALF)==DoubleBlockHalf.UPPER?UPPER_SHAPE: net.minecraft.world.phys.shapes.Shapes.block();
	}

	@Override
	public @NotNull BlockState rotate(BlockState state, Rotation rotation)
	{
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public @NotNull BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
										  @NotNull InteractionHand hand, @NotNull BlockHitResult hit)
	{
		if(level.isClientSide) return InteractionResult.SUCCESS;

		IGMetalDetectorEntity detector = getDetector(level, pos, state);
		if(detector==null||!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;

		NetworkHooks.openScreen(serverPlayer, detector, detector.getBlockPos());
		return InteractionResult.CONSUME;
	}

	@Nullable
	public static IGMetalDetectorEntity getDetector(BlockGetter level, BlockPos pos, BlockState state)
	{
		BlockPos lower = state.getValue(HALF)==DoubleBlockHalf.UPPER?pos.below(): pos;
		return level.getBlockEntity(lower) instanceof IGMetalDetectorEntity detector?detector: null;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state)
	{
		if(state.getValue(HALF)==DoubleBlockHalf.UPPER) return null;
		return new IGMetalDetectorEntity(IGRegistrationHolder.METAL_DETECTOR.get(), pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type)
	{
		if(level.isClientSide||state.getValue(HALF)==DoubleBlockHalf.UPPER) return null;
		return IEServerTickableBE.makeTicker();
	}
}
