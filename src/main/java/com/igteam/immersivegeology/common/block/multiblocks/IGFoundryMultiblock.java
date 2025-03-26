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


public class IGFoundryMultiblock extends IGTemplateMultiblock {

	public static final IGFoundryMultiblock INSTANCE = new IGFoundryMultiblock();

	public IGFoundryMultiblock() {
		super(new ResourceLocation(IGLib.MODID, "multiblocks/foundry"), new BlockPos(2,0,0), new BlockPos(1,1,0), new BlockPos(3,3, 2), IGMultiblockProvider.FOUNDRY);
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
		consumer.accept(new IGClientMultiblockProperties(this, 2.5, 0.5, 1.5));
	}

	@Override
	public String getName()
	{
		return "Foundry";
	}


	@Override
	public int getDefaultBatchInput()
	{
		return 4;
	}

	@Override
	public int getDefaultBatchOutput()
	{
		return 4;
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
