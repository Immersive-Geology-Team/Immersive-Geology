/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.part;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import com.igteam.immersivegeology.common.block.multiblocks.logic.CoreDrillLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.PelletizerLogic;
import com.igteam.immersivegeology.common.block.multiblocks.skins.IGCoreDrillSkins;
import com.igteam.immersivegeology.common.block.multiblocks.skins.IGPelletizerSkins;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class CoreDrillPart extends SkinableMultiblockPart<CoreDrillLogic.State, IGCoreDrillSkins>
{
	public static final EnumProperty<IGCoreDrillSkins> COREDRILL =
			EnumProperty.create("coredrill", IGCoreDrillSkins.class);

	public CoreDrillPart(Properties props, MultiblockRegistration<CoreDrillLogic.State> reg)
	{
		super(props, reg, COREDRILL, IGCoreDrillSkins.class, "block/multiblock/coredrill/");
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(COREDRILL);
	}
}
