/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class IGAdvancementProvider extends AdvancementProvider
{
	public IGAdvancementProvider(PackOutput pOutput, CompletableFuture<Provider> pRegistries, List<AdvancementSubProvider> pSubProviders)
	{
		super(pOutput, pRegistries, pSubProviders);
	}
}
