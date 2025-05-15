// NOTICE: This file includes code adapted from Immersive Engineering.
// This code is used in accordance with the terms of the Blu's License of Common Sense,
// which requires disclosure of significant code usage.
// For more details, refer to the source at [https://github.com/BluSunrize/ImmersiveEngineering/tree/1.20.1].
//
// The original code has been modified to fit the requirements of this project.
// -\('-')/- ~Muddykat

package com.igteam.immersivegeology.common.data.generators.loot;

import com.google.common.collect.ImmutableList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class IGLootProvider extends LootTableProvider
{
	public IGLootProvider(PackOutput output)
	{
		super(output, Set.of(), List.of());
	}

	@Override
	public List<SubProviderEntry> getTables()
	{
		return ImmutableList.of(
				new SubProviderEntry(IGBlockLootProvider::new, LootContextParamSets.BLOCK),
				new SubProviderEntry(IGChestLootProvider::new, LootContextParamSets.CHEST)
		);
	}

	@Override
	protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker)
	{
		map.forEach((p_218436_2_, p_218436_3_) -> p_218436_3_.validate(validationtracker));
	}
}
