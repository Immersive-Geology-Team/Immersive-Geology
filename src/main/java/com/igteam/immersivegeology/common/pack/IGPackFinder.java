/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.pack;

import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import com.igteam.immersivegeology.core.material.data.stone.IGStoneTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.util.Optional;

/**
 * Puts {@link IGDeclaredStonePack} in front of the resource and data pack stacks.
 */
@EventBusSubscriber(modid = IGLib.MODID)
public class IGPackFinder
{
	@SubscribeEvent
	public static void addPackFinders(AddPackFindersEvent event)
	{
		// Decided from the configuration alone. Whether any ore blocks were registered for a declared rock type is
		// not knowable yet at this point in start-up, but we can tell if anything was declared.
		boolean anyDeclared = IGStoneTypes.all().stream().anyMatch(stone -> stone.getHostBlockId()!=null);
		if(!anyDeclared) return;

		PackType type = event.getPackType();
		event.addRepositorySource(consumer -> {
			Pack.ResourcesSupplier resources = new Pack.ResourcesSupplier()
			{
				@Override
				public PackResources openPrimary(PackLocationInfo location)
				{
					return new IGDeclaredStonePack(location, type);
				}

				@Override
				public PackResources openFull(PackLocationInfo location, Pack.Metadata metadata)
				{
					return openPrimary(location);
				}
			};
			Pack pack = Pack.readMetaAndCreate(
					new PackLocationInfo(
							IGDeclaredStonePack.PACK_ID,
							Component.literal("Immersive Geology declared stone types"),
							PackSource.BUILT_IN,
							Optional.empty()),
					resources,
					type,
					new PackSelectionConfig(true, Pack.Position.BOTTOM, false));
			if(pack!=null) consumer.accept(pack);
		});
	}
}
