/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.types;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.common.world.features.helper.IGGenerationType;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.Tags.Biomes;

import java.util.Optional;

public class MaterialEvaporateMineral extends MaterialMineral
{
	public MaterialEvaporateMineral()
	{
		super();
		addFlags(BlockCategoryFlags.EVAPORATE, BlockCategoryFlags.EVAPORATE_CRYSTAL, ItemCategoryFlags.CRYSTAL, ItemCategoryFlags.SEDIMENT);
		removeMaterialFlags(ItemCategoryFlags.SLAG, ItemCategoryFlags.POWDERED_SLAG,
				ItemCategoryFlags.NORMAL_ORE, ItemCategoryFlags.POOR_ORE,ItemCategoryFlags.RICH_ORE,
				ItemCategoryFlags.DIRTY_CRUSHED_ORE, ItemCategoryFlags.CRUSHED_ORE, BlockCategoryFlags.ORE_BLOCK);
		int minSea = 62;
		int maxSea = 72;

		CONFIG = new MineralConfig(8,50,1, minSea, maxSea,50, 0.5,true, Optional.of(Biomes.IS_SANDY), IGGenerationType.EVAPORATE);
	}
}
