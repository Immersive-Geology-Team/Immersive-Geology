package com.igteam.immersivegeology.common.items;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Locale;

/**
 * Created by Pabilo8 on 26-03-2020.
 */
public class IGMaterialItem extends IGBaseItem
{
	public MaterialUseType subtype;
	public Material material;

	public IGMaterialItem(MaterialUseType type, Material material)
	{
		super(type.getName());
		this.subtype = type;
		this.subGroup = type.getSubGroup();
		this.material = material;

	}

	@Override
	public boolean hasCustomItemColours()
	{
		return true;
	}

	@Override
	public int getColourForIEItem(ItemStack stack, int pass)
	{
		return material.getColor(0);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack)
	{
		String matName = I18n.format("material."+material.getModID()+"."+material.getName()+".name");
		return new TranslationTextComponent("item."+ImmersiveGeology.MODID+"."+subtype.getName().toLowerCase(Locale.ENGLISH)+".name", matName);
	}

	@Override
	public Rarity getRarity(ItemStack stack)
	{
		return material.getRarity();
	}

}
