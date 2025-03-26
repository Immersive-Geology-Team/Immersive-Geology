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

public class IGPelletizerMultiblock extends IGTemplateMultiblock
{
	public static final IGPelletizerMultiblock INSTANCE = new IGPelletizerMultiblock();
	public IGPelletizerMultiblock()
	{
		super(new ResourceLocation(IGLib.MODID, "multiblocks/pelletizer"), new BlockPos(1, 0, 1), new BlockPos(0,1,3), new BlockPos(3,3,4), IGMultiblockProvider.PELLETIZER);
	}

	@Override
	public boolean canFormWithDefaultHammer()
	{
		return true;
	}

	@Override
	public float getManualScale()
	{
		return 17;
	}

	@Override
	public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer) {
		consumer.accept(new IGClientMultiblockProperties(this, 2.5, 0.5, 1.5));
	}

	@Override
	public String getName()
	{
		return "Pelletizer";
	}

	@Override
	public int getDefaultBatchInput()
	{
		return 8;
	}

	@Override
	public int getDefaultBatchOutput()
	{
		return 8;
	};

	@Override
	public int getDefaultTime()
	{
		return 800;
	};

	@Override
	public int getDefaultEnergy()
	{
		return 64000;
	};
}
