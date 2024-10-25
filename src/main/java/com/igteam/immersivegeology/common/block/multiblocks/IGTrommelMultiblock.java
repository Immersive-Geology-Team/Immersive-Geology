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

public class IGTrommelMultiblock extends IGTemplateMultiblock
{
	public static final IGTrommelMultiblock INSTANCE = new IGTrommelMultiblock();
	public IGTrommelMultiblock()
	{
		super(new ResourceLocation(IGLib.MODID, "multiblocks/trommel"), new BlockPos(7,0,2), new BlockPos(6,1,4), new BlockPos(9,4,5), IGMultiblockProvider.TROMMEL);
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
