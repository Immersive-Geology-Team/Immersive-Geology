/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.entity.device;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.MapDecorations;
import net.minecraft.world.item.component.MapDecorations.Entry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class IGDepositMapMarks
{
	private IGDepositMapMarks()
	{
	}

	public static final String PREFIX = "ig_deposit_";

	private static final String DECORATIONS = "Decorations";
	private static final String NAME = "IGName";

	public record Mark(String id, Holder<MapDecorationType> type, double x, double z, Component name)
	{
	}

	public static String prefixFor(long detector)
	{
		return PREFIX+detector+"_";
	}

	public static void write(ItemStack stack, String prefix, List<Mark> marks, HolderLookup.Provider registries)
	{
		Map<String, Entry> decorations = new LinkedHashMap<>(stack.getOrDefault(DataComponents.MAP_DECORATIONS, MapDecorations.EMPTY).decorations());
		decorations.keySet().removeIf(id -> id.startsWith(prefix));

		CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		ListTag list = tag.getList(DECORATIONS, Tag.TAG_COMPOUND);
		list.removeIf(entry -> entry instanceof CompoundTag entryTag&&entryTag.getString("id").startsWith(prefix));

		for(Mark mark : marks)
		{
			decorations.put(mark.id(), new MapDecorations.Entry(mark.type(), mark.x(), mark.z(), 180.0F));
			CompoundTag entry = new CompoundTag();
			entry.putString("id", mark.id());
			entry.putString(NAME, Component.Serializer.toJson(mark.name(), registries));
			list.add(entry);
		}

		if(decorations.isEmpty()) stack.remove(DataComponents.MAP_DECORATIONS);
		else stack.set(DataComponents.MAP_DECORATIONS, new MapDecorations(Map.copyOf(decorations)));
		if(list.isEmpty()) tag.remove(DECORATIONS);
		else tag.put(DECORATIONS, list);
		CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
	}

	public static boolean hasMarks(ItemStack stack)
	{
		CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		if(!tag.contains(DECORATIONS, Tag.TAG_LIST)) return false;

		ListTag list = tag.getList(DECORATIONS, Tag.TAG_COMPOUND);
		for(int i = 0; i < list.size(); i++)
		{
			if(list.getCompound(i).getString("id").startsWith(PREFIX)) return true;
		}
		return false;
	}

	public static boolean restore(ItemStack stack, MapItemSavedData data, Level level)
	{
		CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		if(!tag.contains(DECORATIONS, Tag.TAG_LIST)) return false;

		boolean found = false;
		ListTag list = tag.getList(DECORATIONS, Tag.TAG_COMPOUND);
		MapDecorations decorations = stack.getOrDefault(DataComponents.MAP_DECORATIONS, MapDecorations.EMPTY);
		for(int i = 0; i < list.size(); i++)
		{
			CompoundTag entry = list.getCompound(i);
			String id = entry.getString("id");
			if(!id.startsWith(PREFIX)) continue;
			MapDecorations.Entry decorationsEntry = decorations.decorations().get(id);
			if(decorationsEntry==null) continue;
			found = true;

			if(!entry.contains(NAME, Tag.TAG_STRING)) continue;
			Component name = Component.Serializer.fromJson(entry.getString(NAME), level.registryAccess());
			if(name==null) continue;

			data.addDecoration(decorationsEntry.type(), level, id,
					decorationsEntry.x(), decorationsEntry.z(), decorationsEntry.rotation(), name);
		}
		return found;
	}
}
