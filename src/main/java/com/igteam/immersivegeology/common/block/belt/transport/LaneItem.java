/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt.transport;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public final class LaneItem
{
	private static final String STACK = "stack";
	private static final String PROGRESS = "progress";
	private static final String ROUTED = "routed";

	private ItemStack stack;
	private float progress;
	private float previous;
	private boolean routed;

	public LaneItem(ItemStack stack, float progress)
	{
		this.stack = stack;
		this.progress = progress;
		this.previous = progress;
	}

	public ItemStack stack()
	{
		return stack;
	}

	public void setStack(ItemStack stack)
	{
		this.stack = stack;
	}

	public float progress()
	{
		return progress;
	}

	public boolean routed()
	{
		return routed;
	}

	public void setRouted(boolean routed)
	{
		this.routed = routed;
	}

	public float previous()
	{
		return previous;
	}

	public void advanceTo(float progress)
	{
		this.previous = this.progress;
		this.progress = progress;
	}

	public void hold()
	{
		this.previous = this.progress;
	}

	public CompoundTag save()
	{
		CompoundTag tag = new CompoundTag();
		tag.put(STACK, stack.save(new CompoundTag()));
		tag.putFloat(PROGRESS, progress);
		if(routed)
			tag.putBoolean(ROUTED, true);
		return tag;
	}

	public static LaneItem load(CompoundTag tag)
	{
		LaneItem item = new LaneItem(ItemStack.of(tag.getCompound(STACK)), tag.getFloat(PROGRESS));
		item.routed = tag.getBoolean(ROUTED);
		return item;
	}
}
