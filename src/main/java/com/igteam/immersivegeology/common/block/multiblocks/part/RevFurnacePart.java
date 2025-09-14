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
import com.igteam.immersivegeology.common.block.multiblocks.logic.RevFurnaceLogic;
import com.igteam.immersivegeology.common.block.multiblocks.skins.IGBloomerySkins;
import com.igteam.immersivegeology.common.block.multiblocks.skins.IGRevFurnaceSkins;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class RevFurnacePart extends SkinableMultiblockPart<RevFurnaceLogic.State, IGRevFurnaceSkins>
{
	public static final EnumProperty<IGRevFurnaceSkins> REVERBERATION_FURNACE =
			EnumProperty.create("reverberation_furnace", IGRevFurnaceSkins.class);

	public RevFurnacePart(Properties props, MultiblockRegistration<RevFurnaceLogic.State> reg)
	{
		super(props, reg, REVERBERATION_FURNACE, IGRevFurnaceSkins.class, "block/multiblock/reverberation_furnace/");
		this.registerDefaultState(
				this.defaultBlockState()
						.setValue(IEProperties.MIRRORED, false)
						.setValue(REVERBERATION_FURNACE, IGRevFurnaceSkins.DEFAULT)
						.setValue(IEProperties.ACTIVE, false)
		);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(IEProperties.ACTIVE);
		builder.add(REVERBERATION_FURNACE);
	}
}
