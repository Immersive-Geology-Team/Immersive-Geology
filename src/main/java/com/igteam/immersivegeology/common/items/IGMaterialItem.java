package com.igteam.immersivegeology.common.items;

import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.blocks.EnumMaterials;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Pabilo8 on 26-03-2020.
 */
public class IGMaterialItem extends IGBaseItem
{
	public MaterialUseType subtype;
	List<Material> allowedMaterials = new ArrayList<>();
	HashMap<Material, Item> replacementItems = new HashMap<>();

	public IGMaterialItem(MaterialUseType type)
	{
		super(type.getName());
		this.subtype = type;
	}

	@Override
	public boolean hasCustomItemColours()
	{
		return true;
	}

	@Override
	public int getColourForIEItem(ItemStack stack, int pass)
	{
		return getMaterialFromNBT(stack).getColor(0);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack)
	{
		Material material = getMaterialFromNBT(stack);
		String matName = I18n.format("material."+material.getModID()+"."+material.getName()+".name");
		return new TranslationTextComponent("item."+ImmersiveGeology.MODID+"."+subtype.getName().toLowerCase(Locale.ENGLISH)+".name", matName);
	}

	@Override
	public Rarity getRarity(ItemStack stack)
	{
		return getMaterialFromNBT(stack).getRarity();
	}

	public Material getMaterialFromNBT(ItemStack stack)
	{
		try
		{
			return EnumMaterials.valueOf(ItemNBTHelper.getString(stack, "material")).material;
		} catch(IllegalArgumentException e)
		{
			//When item has no material or the material doesn't exist
			return EnumMaterials.Empty.material;
		}

	}

	public void addAllowedMaterial(Material material)
	{
		allowedMaterials.add(material);
	}

	public void addReplacementItem(Material material, Item item)
	{
		replacementItems.put(material, item);
	}

	public Item getReplacementItem(Material material)
	{
		return replacementItems.getOrDefault(material, allowedMaterials.contains(material)?this: Items.AIR);
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
	{
		if(this.isInGroup(group))
		{
			for(Material material : allowedMaterials)
			{
				CompoundNBT tag = new CompoundNBT();
				tag.putString("material", material.getName());
				items.add(new ItemStack(this, 1, tag));
			}
		}
	}

}
