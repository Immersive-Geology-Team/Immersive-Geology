package com.igteam.immersivegeology.common.items;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.materials.MaterialUtils;
import com.igteam.immersivegeology.api.util.IGMathHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Created by Pabilo8 on 26-03-2020.
 */
public class IGMaterialItem extends IGBaseItem
{
	public MaterialUseType useType;
	public Material[] materials;

	public IGMaterialItem(MaterialUseType type, Material... materials)
	{
		this("", type, materials);
	}

	public IGMaterialItem(String sub, MaterialUseType type, Material... materials)
	{
		super(MaterialUtils.generateMaterialName("item", type, materials));
		this.useType = type;
		this.subGroup = type.getSubGroup();
		this.materials = materials;
	}


	@Override
	public boolean hasCustomItemColours()
	{
		return true;
	}

	@Override
	public int getColourForIEItem(ItemStack stack, int pass)
	{
		return materials[IGMathHelper.clamp(pass, 0, materials.length-1)].getColor(0);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack)
	{
		ArrayList<String> localizedNames = new ArrayList<>();
		for(Material m : materials)
			localizedNames.add(I18n.format("material."+m.getModID()+"."+m.getName()+".name"));
		return new TranslationTextComponent("item."+ImmersiveGeology.MODID+"."+useType.getName().toLowerCase(Locale.ENGLISH)+".name", localizedNames.toArray(new String[localizedNames.size()]));
	}

	/**
	 * @return the base material
	 */
	public Material getMaterial()
	{
		return materials[0];
	}

	@Override
	public Rarity getRarity(ItemStack stack)
	{
		return getMaterial().getRarity();
	}

	public MaterialUseType getUseType()
	{
		return useType;
	}
}
