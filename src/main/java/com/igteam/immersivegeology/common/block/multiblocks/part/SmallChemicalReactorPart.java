/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.part;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import com.igteam.immersivegeology.common.block.multiblocks.logic.SmallChemicalReactorLogic;
import com.igteam.immersivegeology.common.block.multiblocks.skins.IGSmallChemicalReactorSkins;
import com.igteam.immersivegeology.common.block.multiblocks.skins.IGSteamTurbineSkins;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;

public class SmallChemicalReactorPart extends SkinableMultiblockPart<SmallChemicalReactorLogic.State, IGSmallChemicalReactorSkins>
{
	public static final EnumProperty<IGSmallChemicalReactorSkins> SMALL_CHEMICAL_REACTOR =
			EnumProperty.create("small_chemical_reactor", IGSmallChemicalReactorSkins.class);

	public SmallChemicalReactorPart(Properties props, MultiblockRegistration<SmallChemicalReactorLogic.State> reg)
	{
		super(props, reg, SMALL_CHEMICAL_REACTOR, IGSmallChemicalReactorSkins.class, "block/multiblock/small_chemical_reactor/");
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(SMALL_CHEMICAL_REACTOR);
	}
}
