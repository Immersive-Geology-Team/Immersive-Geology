/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks.MultiblockManualData;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class IGAlternatorMultiblock extends IGTemplateMultiblock
{
	public static final IGAlternatorMultiblock INSTANCE = new IGAlternatorMultiblock();

	public IGAlternatorMultiblock() {
		super(new ResourceLocation(IGLib.MODID, "multiblocks/alternator"),
				new BlockPos(3,2,3),
				new BlockPos(3,4,4),
				new BlockPos(7, 7, 5), IGMultiblockProvider.ALTERNATOR);
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
		return "Alternator";
	}
}
