/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.part;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import com.igteam.immersivegeology.common.block.multiblocks.logic.BloomeryLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.BloomeryLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.skins.IGBloomerySkins;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class BloomeryPart extends SkinableMultiblockPart<BloomeryLogic.State, IGBloomerySkins>
{
	public static final EnumProperty<IGBloomerySkins> BLOOMERY =
			EnumProperty.create("bloomery", IGBloomerySkins.class);

	public BloomeryPart(Properties props, MultiblockRegistration<State> reg)
	{
		super(props, reg, BLOOMERY, IGBloomerySkins.class, "block/multiblock/bloomery/");
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(IEProperties.ACTIVE);
		builder.add(BLOOMERY);
	}
}
