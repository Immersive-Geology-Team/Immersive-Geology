/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks.MultiblockManualData;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class IGSmallChemicalReactor extends IGTemplateMultiblock
{
	public static final IGSmallChemicalReactor INSTANCE = new IGSmallChemicalReactor();

	public IGSmallChemicalReactor() {
		super(new ResourceLocation(IGLib.MODID, "multiblocks/small_chemical_reactor"),
				new BlockPos(1,0,1),
				new BlockPos(0,1,2),
				new BlockPos(4, 6, 3), IGMultiblockProvider.SMALL_CHEMICAL_REACTOR);
	}

	@Override
	public boolean canFormWithDefaultHammer()
	{
		return true;
	}

	@Override
	public float getManualScale() {
		return 12;
	}

	@Override
	public void initializeClient(Consumer<MultiblockManualData> consumer) {
		consumer.accept(new IGClientMultiblockProperties(this, 2.5, 4, 1.5));
	}

	@Override
	public String getName()
	{
		return "Small Chemical Reactor";
	}
}
