/*
 * BluSunrize
 * Copyright (c) 2024
 *
 * This code is licensed under "Blu's License of Common Sense"
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
	public IGIndustrialSluiceMultiblock()
	{
		super(new ResourceLocation(IGLib.MODID, "multiblocks/industrial_sluice"), new BlockPos(0,0,0), new BlockPos(4,1,6), new BlockPos(8,5,10), IGMultiblockProvider.INDUSTRIAL_SLUICE);
	}

	@Override
	public float getManualScale()
	{
		return 0;
	}

	@Override
	public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer) {
		consumer.accept(new IGClientMultiblockProperties(this, 2.5, 0.5, 2.5));
	}
}
