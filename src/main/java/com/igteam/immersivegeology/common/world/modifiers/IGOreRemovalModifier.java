/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world.modifiers;

import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.world.IGWorldGen;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public record IGOreRemovalModifier() implements BiomeModifier
{
	@Override
	public Codec<? extends BiomeModifier> codec()
	{
		return IGWorldGen.ORE_MODIFIER_CODEC.get();
	}

	private Set<ResourceLocation> getBlacklistedBiomes()
	{
		return IGServerConfig.REMOVAL.biome_blacklist.get().stream().map(ResourceLocation::new).collect(Collectors.toSet());
	}

	private static final BooleanValue isDebugLogEnabled = IGServerConfig.REMOVAL.logProcess;
	@Override
	public void modify(Holder<Biome> holder, Phase phase, Builder builder)
	{
		if (phase == Phase.REMOVE)
		{
			boolean canLog = isDebugLogEnabled.get();
			if(holder.getTagKeys().anyMatch(((b) ->
			{
				if(getBlacklistedBiomes().contains(b.location()))
				{
					if(canLog)
					{
						IGLib.IG_LOGGER.info("Ore Removal Operation not permitted in Biomes with the '{}' tag", b);
						IGLib.IG_LOGGER.info("Change Server Configuration File if this is not desired");
					}
					return true;
				}
				return false;
			})))
			{
				return;
			}

			List<String> oresToRemove = new ArrayList<>();
			if(IGServerConfig.REMOVAL.shouldRemoveIron.get())
			{
				oresToRemove.add("minecraft:ore_iron");
				oresToRemove.add("minecraft:ore_iron_small");
			}

			if(IGServerConfig.REMOVAL.shouldRemoveCopper.get())
			{
				oresToRemove.add("minecraft:ore_copper_small");
				oresToRemove.add("minecraft:ore_copper_large");
			}

			if(IGServerConfig.REMOVAL.shouldRemoveGold.get())
			{
				oresToRemove.add("minecraft:ore_gold");
			}
			if(IGServerConfig.REMOVAL.shouldRemoveIEBauxite.get())
			{
				oresToRemove.add("immersiveengineering:bauxite");
			}
			if(IGServerConfig.REMOVAL.shouldRemoveIELead.get())
			{
				oresToRemove.add("immersiveengineering:lead");
			}
			if(IGServerConfig.REMOVAL.shouldRemoveIESilver.get())
			{
				oresToRemove.add("immersiveengineering:silver");
			}
			if(IGServerConfig.REMOVAL.shouldRemoveIEUranium.get())
			{
				oresToRemove.add("immersiveengineering:uranium");
			}
			if(IGServerConfig.REMOVAL.shouldRemoveIENickel.get())
			{
				oresToRemove.add("immersiveengineering:nickel");
				oresToRemove.add("immersiveengineering:deep_nickel");
			}

			BiomeGenerationSettingsBuilder generationSettings = builder.getGenerationSettings();

			for(Decoration step : Decoration.values())
			{
				List<Holder<PlacedFeature>> features = generationSettings.getFeatures(step);
				Objects.requireNonNull(features);
				features.removeIf(f -> {
					if(f.unwrapKey().isEmpty()) return false;
					return oresToRemove.contains(f.unwrapKey().get().location().toString());
				});
			}
		}
	}
}
