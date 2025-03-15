/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.event;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class IGCommonForgeEvents
{
	@SubscribeEvent
	public void interruptLootTableLoading(LootTableLoadEvent event)
	{
		String namespace = event.getName().getNamespace();
		if(!IGLib.MODID.equals(namespace)) return;

		// Used to remove loot tables for inactive content
		String path = event.getName().getPath();
		for(ModFlags mods : ModFlags.values())
		{
			if(path.contains(mods.getName()) &! mods.isLoaded())
			{
				event.setCanceled(true);
			}
		}
	}
}
