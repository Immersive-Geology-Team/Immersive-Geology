/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.models;

import blusunrize.immersiveengineering.api.IEProperties.IEObjState;
import blusunrize.immersiveengineering.api.IEProperties.VisibilityList;
import blusunrize.immersiveengineering.api.client.ieobj.BlockCallback;
import blusunrize.immersiveengineering.common.items.EngineersBlueprintItem;
import com.igteam.immersivegeology.common.block.entity.DrawingTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class DrawingTableCallbacks implements BlockCallback<Integer>
{
	public static final DrawingTableCallbacks INSTANCE = new DrawingTableCallbacks();

	@Override
	public Integer extractKey(@NotNull BlockAndTintGetter blockAndTintGetter, @NotNull BlockPos blockPos, @NotNull BlockState blockState, BlockEntity blockEntity)
	{
		if(!(blockEntity instanceof DrawingTableBlockEntity table))
			return 0;

		DrawingTableBlockEntity master = table.master();
		if(master == null) return 0;

		NonNullList<ItemStack> inv = master.getInventory();
		if(inv.get(0).getCount() > 32) return 3;
		if(inv.get(0).getCount() > 15) return 2;
		if(inv.get(0).getCount() > 0) return 1;

		return 0;
	}

	@Override
	public Integer getDefaultKey()
	{
		return 0;
	}

	private static final IEObjState no_paper = new IEObjState(VisibilityList.show("base_model"));
	private static final IEObjState some_paper = new IEObjState(VisibilityList.show("base_model", "scroll_1"));
	private static final IEObjState paper = new IEObjState(VisibilityList.show("base_model", "scroll_1", "scroll_2"));
	private static final IEObjState much_paper = new IEObjState(VisibilityList.show("base_model", "scroll_1", "scroll_2", "scroll_3"));

	@Override
	public IEObjState getIEOBJState(Integer paper_level)
	{
		if(paper_level == 1) return some_paper;
		if(paper_level == 2) return paper;
		if(paper_level == 3) return much_paper;
		return no_paper;
	}
}
