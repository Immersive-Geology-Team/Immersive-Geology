package com.igteam.immersivegeology.common.items;

import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.blocks.EnumMaterials;
import com.igteam.immersivegeology.common.util.IGLogger;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Pabilo8 on 26-03-2020.
 */
public class IGMaterialItem extends IGBaseItem
{
	public MaterialUseType subtype;
	HashMap<String, Material> allowedMaterials = new HashMap<>();
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
		String matName = ItemNBTHelper.getString(stack, "material");
		IGLogger.info(matName);
		return allowedMaterials.getOrDefault(matName, EnumMaterials.Empty.material);
	}

	public void addAllowedMaterial(Material material)
	{
		allowedMaterials.put(material.getName(), material);
	}

	public void addReplacementItem(Material material, Item item)
	{
		replacementItems.put(material, item);
	}

	public Item getReplacementItem(Material material)
	{
		return replacementItems.getOrDefault(material, allowedMaterials.containsValue(material)?this: Items.AIR);
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
	{
		if(this.isInGroup(group)||group.equals(ItemGroup.SEARCH))
		{
			for(Material material : allowedMaterials.values())
			{
				if(!replacementItems.containsKey(material))
				{
					CompoundNBT tag = new CompoundNBT();
					ItemStack stack = new ItemStack(this);
					ItemNBTHelper.putString(stack, "material", material.getName());
					items.add(stack);
				}
				else if(!group.equals(ItemGroup.SEARCH))
					items.add(new ItemStack(getReplacementItem(material)));
			}
		}
	}

}
