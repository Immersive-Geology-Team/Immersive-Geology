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

public class IGBloomeryMultiblock extends IGTemplateMultiblock
{
	public static final IGBloomeryMultiblock INSTANCE = new IGBloomeryMultiblock();
	public IGBloomeryMultiblock()
	{
		super(new ResourceLocation(IGLib.MODID, "multiblocks/bloomery"), new BlockPos(0,0,0), new BlockPos(0,1,0), new BlockPos(1,2,1), IGMultiblockProvider.BLOOMERY);
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
