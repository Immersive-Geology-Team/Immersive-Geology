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
import com.igteam.immersivegeology.common.block.multiblocks.logic.RotaryKilnLogic;
import com.igteam.immersivegeology.common.block.multiblocks.skins.IGRotaryKilnSkins;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class RotaryKilnPart extends SkinableMultiblockPart<RotaryKilnLogic.State, IGRotaryKilnSkins>
{
	public static final EnumProperty<IGRotaryKilnSkins> ROTARYKILN =
			EnumProperty.create("rotarykiln", IGRotaryKilnSkins.class);

	public RotaryKilnPart(Properties props, MultiblockRegistration<RotaryKilnLogic.State> reg)
	{
		super(props, reg, ROTARYKILN, IGRotaryKilnSkins.class, "block/multiblock/rotarykiln/");
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(ROTARYKILN);
	}
}
