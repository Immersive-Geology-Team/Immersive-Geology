/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators;

import com.igteam.immersivegeology.common.data.TRSRModelBuilder;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public abstract class IGTRSRItemModelProvider extends ModelProvider<TRSRModelBuilder>
{
	public IGTRSRItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, IGLib.MODID, "item", TRSRModelBuilder::new, existingFileHelper);
	}
}
