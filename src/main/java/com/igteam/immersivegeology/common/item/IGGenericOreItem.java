/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.item;

import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.core.material.data.types.MaterialNativeMetal;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

public class IGGenericOreItem extends IGGenericItem
{
	private final OreRichness oreRichness;
	public IGGenericOreItem(ItemCategoryFlags flag, MaterialInterface<?> material)
	{
		super(flag, material);
		this.oreRichness = flag.equals(ItemCategoryFlags.RICH_ORE) ? OreRichness.RICH : (flag.equals(ItemCategoryFlags.NORMAL_ORE) ? OreRichness.NORMAL : OreRichness.POOR);
	}

	@Override
	public int getColor(int index) {
		return super.getColor(index);
	}

	@Override
	public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType)
	{
		return (super.getBurnTime(itemStack, recipeType) * (oreRichness.ordinal() + 1));
	}

	@Override
	protected Component materialName(MaterialInterface<?> material)
	{
		if(material.instance() instanceof MaterialNativeMetal)
		{
			return Component.translatable("material.immersivegeology.native_material", super.materialName(material));
		}
		return super.materialName(material);
	}

	public OreRichness getOreRichness()
	{
		return this.oreRichness;
	}
}
