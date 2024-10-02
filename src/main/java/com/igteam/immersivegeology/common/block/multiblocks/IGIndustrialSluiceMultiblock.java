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

public class IGIndustrialSluiceMultiblock extends IGTemplateMultiblock
{
	public static final IGIndustrialSluiceMultiblock INSTANCE = new IGIndustrialSluiceMultiblock();
	public IGIndustrialSluiceMultiblock()
	{
		super(new ResourceLocation(IGLib.MODID, "multiblocks/industrial_sluice"), new BlockPos(5,1,5), new BlockPos(5,1,5), new BlockPos(10,5,8), IGMultiblockProvider.INDUSTRIAL_SLUICE);
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
