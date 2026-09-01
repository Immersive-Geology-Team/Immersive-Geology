/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.entity.device;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import java.util.List;

public final class IGDepositMapMarks
{
	private IGDepositMapMarks()
	{
	}

	public static final String PREFIX = "ig_deposit_";

	private static final String DECORATIONS = "Decorations";
	private static final String NAME = "IGName";

	public record Mark(String id, MapDecoration.Type type, double x, double z, Component name)
	{
	}

	public static String prefixFor(long detector)
	{
		return PREFIX+detector+"_";
	}

	public static void write(ItemStack stack, String prefix, List<Mark> marks)
	{
		ListTag list = stack.getOrCreateTag().getList(DECORATIONS, Tag.TAG_COMPOUND);
		list.removeIf(entry -> entry instanceof CompoundTag tag&&tag.getString("id").startsWith(prefix));

		for(Mark mark : marks)
		{
			CompoundTag entry = new CompoundTag();
			entry.putByte("type", mark.type().getIcon());
			entry.putString("id", mark.id());
			entry.putDouble("x", mark.x());
			entry.putDouble("z", mark.z());
			entry.putDouble("rot", 180.0);
			entry.putString(NAME, Component.Serializer.toJson(mark.name()));
			list.add(entry);
		}

		if(list.isEmpty()) stack.removeTagKey(DECORATIONS);
		else stack.addTagElement(DECORATIONS, list);
	}

	public static boolean hasMarks(ItemStack stack)
	{
		CompoundTag tag = stack.getTag();
		if(tag==null||!tag.contains(DECORATIONS, Tag.TAG_LIST)) return false;

		ListTag list = tag.getList(DECORATIONS, Tag.TAG_COMPOUND);
		for(int i = 0; i < list.size(); i++)
		{
			if(list.getCompound(i).getString("id").startsWith(PREFIX)) return true;
		}
		return false;
	}

	public static boolean restore(ItemStack stack, MapItemSavedData data, Level level)
	{
		CompoundTag tag = stack.getTag();
		if(tag==null||!tag.contains(DECORATIONS, Tag.TAG_LIST)) return false;

		boolean found = false;
		ListTag list = tag.getList(DECORATIONS, Tag.TAG_COMPOUND);
		for(int i = 0; i < list.size(); i++)
		{
			CompoundTag entry = list.getCompound(i);
			String id = entry.getString("id");
			if(!id.startsWith(PREFIX)) continue;
			found = true;

			if(!entry.contains(NAME, Tag.TAG_STRING)) continue;
			Component name = Component.Serializer.fromJson(entry.getString(NAME));
			if(name==null) continue;

			data.addDecoration(MapDecoration.Type.byIcon(entry.getByte("type")), level, id,
					entry.getDouble("x"), entry.getDouble("z"), entry.getDouble("rot"), name);
		}
		return found;
	}
}
