/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world;

import com.igteam.immersivegeology.common.block.IGOreBlock.OreRichness;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.TargetBlockState;

import java.util.List;

public class MineralEntry
{

	public static final Codec<MineralEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			MineralEnum.CODEC.fieldOf("mineral").forGetter(MineralEntry::getMineral),
			StoneEnum.CODEC.fieldOf("stone").forGetter(MineralEntry::getStone)
	).apply(instance, MineralEntry::new));

	private final MineralEnum mineral;
	private final StoneEnum stone;
	public MineralEntry(MineralEnum mineral, StoneEnum stone)
	{
		this.mineral = mineral;
		this.stone = stone;
	}

	public MineralEnum getMineral()
	{
		return mineral;
	}

	public StoneEnum getStone()
	{
		return stone;
	}

	public String getName()
	{
		return getMineral().getName().toLowerCase() + "_" + getStone().instance().getClass().getSimpleName().toLowerCase().replace("material","").toLowerCase();
	}

	public List<TargetBlockState> getTargetList(MineralEnum mineral)
	{
		return stone.getTargets(mineral);
	}
}
