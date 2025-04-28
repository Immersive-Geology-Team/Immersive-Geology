/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.part;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import com.igteam.immersivegeology.common.block.multiblocks.logic.GravitySeparatorLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.GravitySeparatorLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.skins.IGGravitySeparatorSkins;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class GravitySeparatorPart extends SkinableMultiblockPart<GravitySeparatorLogic.State, IGGravitySeparatorSkins>
{
	public static final EnumProperty<IGGravitySeparatorSkins> GRAVITY_SEPARATOR =
			EnumProperty.create("gravity_separator", IGGravitySeparatorSkins.class);

	public GravitySeparatorPart(Properties props, MultiblockRegistration<State> reg)
	{
		super(props, reg, GRAVITY_SEPARATOR, IGGravitySeparatorSkins.class, "block/multiblock/gravityseparator/");
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(GRAVITY_SEPARATOR);
	}
}
