/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.temp;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import com.igteam.immersivegeology.common.block.multiblocks.IGClientMultiblockProperties;
import com.igteam.immersivegeology.common.block.multiblocks.IGTemplateMultiblock;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class HRHTPortalMultiblock extends IGTemplateMultiblock
{
	public static final HRHTPortalMultiblock INSTANCE = new HRHTPortalMultiblock();
	public HRHTPortalMultiblock()
	{
		super(new ResourceLocation(IGLib.MODID, "multiblocks/techportal"), new BlockPos(1,0,1), new BlockPos(2,1,3), new BlockPos(15,9,10), IGMultiblockProvider.HRHTPortal);
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
