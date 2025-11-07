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
import com.igteam.immersivegeology.common.block.multiblocks.logic.GeothermalExchangerLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.GeothermalExchangerLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.skins.IGGeothermalSkins;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

public class GeothermalPart extends SkinableMultiblockPart<GeothermalExchangerLogic.State, IGGeothermalSkins>
{
	public static final EnumProperty<IGGeothermalSkins> GEOTHERMAL_EXCHANGER =
			EnumProperty.create("geothermal_exchanger", IGGeothermalSkins.class);

	public GeothermalPart(Properties props, MultiblockRegistration<GeothermalExchangerLogic.State> reg)
	{
		super(props.strength(60,1200), reg, GEOTHERMAL_EXCHANGER, IGGeothermalSkins.class, "block/multiblock/geothermal_exchanger/");
	}


	@SuppressWarnings("unchecked")
	@Override
	public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving)
	{
		if(level.getBlockEntity(pos) instanceof IMultiblockBE<?> be)
		{
			IMultiblockBEHelper<?> helper = be.getHelper();
			if(helper.getState() instanceof GeothermalExchangerLogic.State mbState)
			{
				mbState.invalidate((IMultiblockContext<State>) helper.getContext());
			}
		}
		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(GEOTHERMAL_EXCHANGER);
	}
}
