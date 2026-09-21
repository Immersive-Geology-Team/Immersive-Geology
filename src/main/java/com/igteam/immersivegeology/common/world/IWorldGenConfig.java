package com.igteam.immersivegeology.common.world;

import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.helper.material.IStoneType;
import net.minecraft.block.state.IBlockState;


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

	java.util.Optional<com.igteam.immersivegeology.core.lib.shim.MCShims.TagKey<com.igteam.immersivegeology.core.lib.shim.MCShims.Biome>> getPreferredBiome();

	GeologyMaterial instance();

	IOreBlock getOreBlock(IStoneType stone, OreRichness oreRichness);

	IOreBlock getOreBlock(com.igteam.immersivegeology.core.material.helper.material.MaterialHelper stone,
			OreRichness oreRichness);

	IBlockState getDefaultBlockstate();

	String name();

	default float getNoiseProbability()
	{
		return 1.0f;
	}

	default com.igteam.immersivegeology.common.world.features.helper.noise.IGGenerationType getGenerationType()
	{
		return com.igteam.immersivegeology.common.world.features.helper.noise.IGGenerationType.DEFAULT;
	}

	default IWorldGenConfig getConfig()
	{
		return this;
	}

	default long seed()
	{
		return 0L;
	}

	default double getMinSpawnTemp()
	{
		return 0d;
	}

	default double getMaxSpawnTemp()
	{
		return 2d;
	}

	default double getMinDownfall()
	{
		return 0d;
	}

	default double getMaxDownfall()
	{
		return 1d;
	}

	default double getAssociateMaterialChance()
	{
		return 1d;
	}

	default java.util.Set<com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGShimTypes.Pair<
			java.util.function.Function<Integer, com.igteam.immersivegeology.core.material.helper.material.MaterialHelper>, Integer>>
			getAssociateMaterialSet()
	{
		return java.util.Collections.emptySet();
	}
}
