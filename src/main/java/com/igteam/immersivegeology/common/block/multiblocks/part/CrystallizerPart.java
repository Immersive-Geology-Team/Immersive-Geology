/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.part;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelper;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import com.igteam.immersivegeology.common.block.multiblocks.logic.CrystallizerLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.CrystallizerLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.skins.IGCrystallizerSkins;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

public class CrystallizerPart extends SkinableMultiblockPart<CrystallizerLogic.State, IGCrystallizerSkins>
{
	public static final EnumProperty<IGCrystallizerSkins> CRYSTALLIZER =
			EnumProperty.create("crystallizer", IGCrystallizerSkins.class);

	public CrystallizerPart(Properties props, MultiblockRegistration<CrystallizerLogic.State> reg)
	{
		super(props, reg, CRYSTALLIZER, IGCrystallizerSkins.class, "block/multiblock/crystallizer/");
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(CRYSTALLIZER);
	}
}
