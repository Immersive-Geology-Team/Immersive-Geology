/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.part;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import com.igteam.immersivegeology.common.block.multiblocks.logic.PelletizerLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.SteamTurbineLogic;
import com.igteam.immersivegeology.common.block.multiblocks.skins.IGPelletizerSkins;
import com.igteam.immersivegeology.common.block.multiblocks.skins.IGSteamTurbineSkins;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class SteamTurbinePart extends SkinableMultiblockPart<SteamTurbineLogic.State, IGSteamTurbineSkins>
{
	public static final EnumProperty<IGSteamTurbineSkins> STEAM_TURBINE =
			EnumProperty.create("steam_turbine", IGSteamTurbineSkins.class);

	public SteamTurbinePart(Properties props, MultiblockRegistration<SteamTurbineLogic.State> reg)
	{
		super(props, reg, STEAM_TURBINE, IGSteamTurbineSkins.class, "block/multiblock/steam_turbine/");
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(STEAM_TURBINE);
	}
}
