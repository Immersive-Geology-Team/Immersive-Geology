/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class IGChemicalReactorMultiblock extends IGTemplateMultiblock
{
	public static final IGChemicalReactorMultiblock INSTANCE = new IGChemicalReactorMultiblock();
	public IGChemicalReactorMultiblock()
	{
		super(new ResourceLocation(IGLib.MODID, "multiblocks/chemical_reactor"), new BlockPos(4,0,4), new BlockPos(2,1,5), new BlockPos(9,6,9), IGMultiblockProvider.CHEMICAL_REACTOR);
	}

	@Override
	public float getManualScale()
	{
		return 12;
	}

	@Override
	public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer) {
		consumer.accept(new IGClientMultiblockProperties(this, 2.5, 0.5, 2.5));
	}
}
