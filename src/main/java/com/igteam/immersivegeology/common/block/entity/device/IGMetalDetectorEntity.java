/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.entity.device;

import blusunrize.immersiveengineering.api.client.IModelOffsetProvider;
import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.common.blocks.IEBaseBlockEntity;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IHasDummyBlocks;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IPlayerInteraction;
import blusunrize.immersiveengineering.common.blocks.ticking.IEClientTickableBE;
import blusunrize.immersiveengineering.common.blocks.ticking.IEServerTickableBE;
import blusunrize.immersiveengineering.common.util.MultiblockCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class IGMetalDetectorEntity extends IEBaseBlockEntity implements IEServerTickableBE, IEClientTickableBE, IHasDummyBlocks, IPlayerInteraction, IModelOffsetProvider
{
	public MutableEnergyStorage energyStorage = new MutableEnergyStorage(8000);
	public int dummy = 0;
	public int process = 0;
	public boolean isRunning = false;
//
//	private final MultiblockCapability<IEnergyStorage> energyCap = MultiblockCapability.make(
//			this, be -> be.energyCap, IGMetalDetectorEntity::master, registerEnergyInput(energyStorage)
//	);

	public IGMetalDetectorEntity(BlockEntityType<IGMetalDetectorEntity> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}

	@Override
	public BlockPos getModelOffset(BlockState blockState, Vec3i vec3i)
	{
		return null;
	}

	@Override
	public void readCustomNBT(CompoundTag compoundTag, boolean b)
	{

	}

	@Override
	public void writeCustomNBT(CompoundTag compoundTag, boolean b)
	{

	}

	@Override
	public void placeDummies(BlockPlaceContext blockPlaceContext, BlockState blockState)
	{

	}

	@Override
	public void breakDummies(BlockPos blockPos, BlockState blockState)
	{

	}

	@Nullable
	@Override
	public IEBlockInterfaces.IGeneralMultiblock master()
	{
		return null;
	}

	@Override
	public boolean interact(Direction direction, Player player, InteractionHand interactionHand, ItemStack itemStack, float v, float v1, float v2)
	{
		return false;
	}

	@Override
	public void tickClient()
	{

	}

	@Override
	public void tickServer()
	{

	}
}
