/*
 * BluSunrize
 * Copyright (c) 2024
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks.MultiblockManualData;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class IGIndustrialSluice extends IGTemplateMultiblock
{
	public IGIndustrialSluice(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size, MultiblockRegistration<?> logic)
	{
		super(new ResourceLocation(IGLib.MODID, "multiblocks/industrial_sluice"), new BlockPos(0,0,0), new BlockPos(6,1,4), new BlockPos(10,5,8), IGMultiblockProvider.INDUSTRIAL_SLUICE);
	}

	@Override
	public float getManualScale()
	{
		return 0;
	}

	@Override
	public void initializeClient(Consumer<MultiblockManualData> consumer)
	{

	}
}
