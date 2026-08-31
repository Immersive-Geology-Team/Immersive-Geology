/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.recipe.builder;

import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.TurbineFuel;
import com.igteam.immersivegeology.common.recipe.IGGeoRecipe;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import java.util.Locale;

public class IGGeoHintBuilder extends IEFinishedRecipe<IGGeoHintBuilder>
{

	private static String capitalize(String input)
	{
		return input.substring(0,1).toUpperCase(Locale.ROOT) + input.substring(1);
	}

	private IGGeoHintBuilder(GeologyMaterial material) {
		super(IGGeoRecipe.SERIALIZER.get());

		int type = material instanceof MaterialMineral? 0 : 1;
		int index = type == 1 ? MetalEnum.valueOf(capitalize(material.getName())).ordinal() : MineralEnum.valueOf(capitalize(material.getName())).ordinal();


		this.addWriter((obj) -> {
			obj.addProperty("material_index", index);
		});
		this.addWriter((obj) -> {
			obj.addProperty("material_type", type);
		});
	}

	public static IGGeoHintBuilder builder(GeologyMaterial material) {
		return new IGGeoHintBuilder(material);
	}
}
