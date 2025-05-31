/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.part;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import com.igteam.immersivegeology.common.block.multiblocks.logic.GeothermalExchangerLogic;
import com.igteam.immersivegeology.common.block.multiblocks.skins.IGGeothermalSkins;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class GeothermalPart extends SkinableMultiblockPart<GeothermalExchangerLogic.State, IGGeothermalSkins>
{
	public static final EnumProperty<IGGeothermalSkins> GEOTHERMAL_EXCHANGER =
			EnumProperty.create("geothermal_exchanger", IGGeothermalSkins.class);

	public GeothermalPart(Properties props, MultiblockRegistration<GeothermalExchangerLogic.State> reg)
	{
		super(props.strength(60,1200), reg, GEOTHERMAL_EXCHANGER, IGGeothermalSkins.class, "block/multiblock/geothermal_exchanger/");
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(GEOTHERMAL_EXCHANGER);
	}
}
