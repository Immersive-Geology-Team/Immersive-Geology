/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators;

import com.igteam.immersivegeology.common.block.IGOreBlock;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class IGBlockLootProvider extends BlockLootSubProvider
{
	private final Set<Block> generatedLootTables = new HashSet<>();
	public IGBlockLootProvider()
	{
		super(Set.of(), FeatureFlags.REGISTRY.allFlags());
	}

	@Override
	protected void generate()
	{
		for(RegistryObject<Block> block_object : IGRegistrationHolder.getBlockRegistryMap().values())
		{
			if(block_object.isPresent())
			{
				Block block = block_object.get();
				if(block instanceof IGOreBlock ore)
				{
					dropOther(ore, ore.getDroppedItem());
				}
				continue;
			}
			IGLib.IG_LOGGER.warn("Failed to access Registry Object");
		}
	}

	@Override
	protected void add(@NotNull Block block, LootTable.Builder builder) {
		this.generatedLootTables.add(block);
		this.map.put(block.getLootTable(), builder);
	}

	@Override
	protected @NotNull Iterable<Block> getKnownBlocks() {
		return generatedLootTables;
	}
}
