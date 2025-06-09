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

public class IGSteamTurbineMultiblock extends IGTemplateMultiblock
{
	public static final IGSteamTurbineMultiblock INSTANCE = new IGSteamTurbineMultiblock();
	public IGSteamTurbineMultiblock()
	{
		super(new ResourceLocation(IGLib.MODID, "multiblocks/steam_turbine"), new BlockPos(1, 0, 1), new BlockPos(1,2,5), new BlockPos(14,8,7), IGMultiblockProvider.STEAM_TURBINE);
	}

	@Override
	public boolean canFormWithDefaultHammer()
	{
		return true;
	}

	@Override
	public float getManualScale()
	{
		return 8;
	}

	@Override
	public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer) {
		consumer.accept(new IGClientMultiblockProperties(this, 2.5, 0.5, 1.5));
	}

	@Override
	public String getName()
	{
		return "Steam Turbine";
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
