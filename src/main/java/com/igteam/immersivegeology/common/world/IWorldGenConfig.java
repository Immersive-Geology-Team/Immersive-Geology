package com.igteam.immersivegeology.common.world;

import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.helper.material.IStoneType;
import net.minecraft.block.state.IBlockState;

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

	Optional<Object> getPreferredBiome();

	GeologyMaterial instance();

	IOreBlock getOreBlock(IStoneType stone, OreRichness oreRichness);

	IBlockState getDefaultBlockstate();

	String name();

	default float getNoiseProbability()
	{
		return 1.0f;
	}
}
