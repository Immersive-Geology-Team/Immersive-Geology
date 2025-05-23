/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.item;

import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.item.helper.IGFlagItem;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IGCustomTool extends HoeItem implements IGFlagItem
{
	protected final Map<MaterialTexture, MaterialInterface<?>> materialMap = new HashMap<>();
	protected final ItemCategoryFlags category;
	public IGCustomTool(Tier tier, int damage, int speed, ItemCategoryFlags flag, MaterialInterface<?> material) {
		super(tier, damage, speed, new Item.Properties().fireResistant().stacksTo(1));
		this.materialMap.put(MaterialTexture.base, material);
		this.category = flag;
	}

	@Override
	public boolean isDamageable(ItemStack stack)
	{
		return false;
	}

	@Override
	public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced)
	{
		super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
		//TODO change this to be more dynamic, for now as only the unobtanium hoe uses this class we have it set manually.
		pTooltipComponents.add(Component.translatable("immersivegeology.bug_bounty.ktos").withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC));
	}

	public int getColor(int index) {
		if(getFlag().hasPalette() || getFlag().equals(ItemCategoryFlags.PELLET) || getFlag().equals(ItemCategoryFlags.OXIDE_PELLET) || getFlag().equals(ItemCategoryFlags.HAMMER) || getMaxStackSize(getDefaultInstance()) == 1) return 0xffffff;
		if (index >= materialMap.values().size()) index = index % materialMap.values().size();

		//let's use last available colour. map could not be empty
		return materialMap.get(MaterialTexture.values()[index]).getColor(category, 0);
	}

	@Override
	public ItemCategoryFlags getFlag() {
		return category;
	}

	@Override
	public ItemSubGroup getSubGroup() {
		return category.getSubGroup();
	}

	@Override
	public Collection<MaterialInterface<?>> getMaterials() {
		return materialMap.values();
	}

	@Override
	public MaterialInterface<?> getMaterial(MaterialTexture t) {
		return materialMap.get(t);
	}

}
