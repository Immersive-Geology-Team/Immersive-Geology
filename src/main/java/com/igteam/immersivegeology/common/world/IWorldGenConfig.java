/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world;

import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.mojang.serialization.Codec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;

public interface IWorldGenConfig
{
	int getVeinSize();
	int getMinY();
	int getMaxY();
	int veinsPerChunk();
	int rarity();
	int generationChance();
	double density();
	boolean useSparsePlacement();

	Optional<TagKey<Biome>> getPreferredBiome();

	GeologyMaterial instance();

	IOreBlock getOreBlock(StoneEnum stone, OreRichness oreRichness);

	String name();

	Codec<IWorldGenConfig> CODEC = Codec.STRING.xmap(
			IWorldGenConfig::getValue, IWorldGenConfig::name
	);

	static IWorldGenConfig getValue(String e)
	{
		IWorldGenConfig v = null;
		try
		{
			v = MineralEnum.valueOf(e);
		} catch(Exception ex)
		{
			try
			{
				v = MetalEnum.valueOf(e);
				if(!v.instance().hasFlag(BlockCategoryFlags.ORE_BLOCK))
				{
					return null;
				}
			}catch(Exception ignored){};
		}

		return v;
	}

	String getName();
}
