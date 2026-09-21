package com.igteam.immersivegeology.common.item;

import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class IGGenericItem extends Item
{
	protected final ItemCategoryFlags category;
	protected final MaterialInterface<?> material;

	public IGGenericItem(ItemCategoryFlags category, MaterialInterface<?> material)
	{
		this.category = category;
		this.material = material;
	}

	public ItemCategoryFlags getCategory()
	{
		return category;
	}

	public MaterialInterface<?> getMaterial()
	{
		return material;
	}

	public IFlagType<?> getFlag()
	{
		return category;
	}

	public int getColor(ItemStack stack, int tintIndex)
	{
		if(usesPaletteSprite()) return 0xFFFFFF;
		return material.getColor(category, tintIndex);
	}

	public boolean usesPaletteSprite()
	{
		return category.hasPalette()||category==ItemCategoryFlags.PELLET
				||category==ItemCategoryFlags.OXIDE_PELLET||category==ItemCategoryFlags.HAMMER;
	}
}
