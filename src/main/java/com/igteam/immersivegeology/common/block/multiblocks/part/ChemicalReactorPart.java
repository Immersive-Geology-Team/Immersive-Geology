/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.part;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import com.igteam.immersivegeology.common.block.multiblocks.logic.ChemicalReactorLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.ChemicalReactorLogic.State;
import com.igteam.immersivegeology.common.block.multiblocks.skins.IGChemicalReactorSkins;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class ChemicalReactorPart extends SkinableMultiblockPart<ChemicalReactorLogic.State, IGChemicalReactorSkins>
{
	public static final EnumProperty<IGChemicalReactorSkins> CHEMICAL_REACTOR =
			EnumProperty.create("chemical_reactor", IGChemicalReactorSkins.class);

	public ChemicalReactorPart(Properties props, MultiblockRegistration<State> reg)
	{
		super(props, reg, CHEMICAL_REACTOR, IGChemicalReactorSkins.class, "block/multiblock/chemical_reactor/");
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(CHEMICAL_REACTOR);
	}
}
