package com.igteam.immersivegeology.common.blocks;

import java.util.HashMap;
import java.util.Locale;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.materials.MaterialUseType.UseCategory;
import com.igteam.immersivegeology.client.menu.helper.ItemSubGroup;

import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class IGBlockMaterialItem extends IGBlockItem {
	
	HashMap<String, Material> allowedMaterials = new HashMap<>();
	HashMap<Material, Item> replacementItems = new HashMap<>();
	public IGBlockMaterialItem(Block b, Properties props, ItemSubGroup sub, MaterialUseType subtype) {
		super(b, props, sub, subtype);
		this.setRegistryName(b.getRegistryName());
	}
	
	@Override
	public ITextComponent getDisplayName(ItemStack stack)
	{
		Material material = getMaterialFromNBT(stack);
		String matName = I18n.format("material."+material.getModID()+"."+material.getName()+".name");
		return new TranslationTextComponent("block."+ImmersiveGeology.MODID+"."+ subtype.getName().toLowerCase(Locale.ENGLISH)+".name", matName);
	}

	@Override
	public Rarity getRarity(ItemStack stack)
	{
		return getMaterialFromNBT(stack).getRarity();
	}

	public Material getMaterialFromNBT(ItemStack stack)
	{
		String matName = ItemNBTHelper.getString(stack, "material");
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
