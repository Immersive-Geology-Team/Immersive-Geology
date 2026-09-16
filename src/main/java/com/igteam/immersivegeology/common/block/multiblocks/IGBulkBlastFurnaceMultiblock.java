/*
 * Muddykat
 * Copyright (c) 2025
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

public class IGBulkBlastFurnaceMultiblock extends IGTemplateMultiblock
{
	public static final IGBulkBlastFurnaceMultiblock INSTANCE = new IGBulkBlastFurnaceMultiblock();

	public IGBulkBlastFurnaceMultiblock()
	{
		super(new ResourceLocation(IGLib.MODID, "multiblocks/bulk_blast_furnace"), new BlockPos(1, 0, 1), new BlockPos(1, 1, 2), new BlockPos(3, 8, 3), IGMultiblockProvider.BULK_BLAST_FURNACE);
	}

	@Override
	public boolean canFormWithDefaultHammer()
	{
		return true;
	}

	@Override
	public boolean canBeMirrored()
	{
		return false;
	}

	@Override
	public float getManualScale()
	{
		return 10;
	}

	@Override
	public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer)
	{
		consumer.accept(new IGClientMultiblockProperties(this, 1.5f, 0.5f, 1.5f));
	}

	@Override
	public String getName()
	{
		return "Bulk Blast Furnace";
	}
}
