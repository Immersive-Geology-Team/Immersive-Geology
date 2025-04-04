/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.helper;

import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;
import java.util.List;

public interface IOreBlock
{
	BlockState getDefaultBlockState();

	Collection<MaterialInterface<?>> getMaterials();

	String getDescriptionId();

	Item asItem();
	Block asBlock();

	MaterialInterface<?> getMaterial(MaterialTexture t);

	OreRichness getOreRichness();

	StoneFormation getStoneFormation();

	IFlagType<?> getFlag();

	ModFlags getModFlag();

	ItemStack getItemDrop();

	List<Pair<ItemStack, Float>> getExtraDrops();
}
