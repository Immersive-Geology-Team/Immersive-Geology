/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.part;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import com.igteam.immersivegeology.common.block.multiblocks.logic.AlternatorLogic;
import com.igteam.immersivegeology.common.block.multiblocks.skins.IGAlternatorSkins;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class AlternatorPart extends SkinableMultiblockPart<AlternatorLogic.State, IGAlternatorSkins>
{
	public static final EnumProperty<IGAlternatorSkins> ALTERNATOR = EnumProperty.create("alternator", IGAlternatorSkins.class);

	public AlternatorPart(Properties props, MultiblockRegistration<AlternatorLogic.State> reg)
	{
		super(props, reg, ALTERNATOR, IGAlternatorSkins.class, "block/multiblock/alternator/");
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(ALTERNATOR);
	}
}
