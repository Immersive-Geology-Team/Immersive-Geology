package com.igteam.immersivegeology.core.material.helper.material;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Collections;
import java.util.List;

public class IGTag extends com.igteam.immersivegeology.core.lib.shim.MCShims.TagKey<net.minecraftforge.fluids.Fluid>
{
	private final String oreName;

	public IGTag(String oreName)
	{
		super(oreName==null?"": oreName);
		this.oreName = oreName==null?"": oreName;
	}

	@Override
	public String getName()
	{
		return oreName;
	}

	public boolean isEmpty()
	{
		return oreName.isEmpty();
	}

	public boolean exists()
	{
		return !isEmpty()&&OreDictionary.doesOreNameExist(oreName);
	}

	public List<ItemStack> resolve()
	{
		return exists()?OreDictionary.getOres(oreName, false): Collections.emptyList();
	}

	public String serialize()
	{
		return oreName;
	}

	public static int hash(String value)
	{
		return value==null?0: value.hashCode();
	}

	@Override
	public String toString()
	{
		return "IGTag["+oreName+"]";
	}

	@Override
	public boolean equals(Object other)
	{
		return other instanceof IGTag tag&&tag.oreName.equals(oreName);
	}

	@Override
	public int hashCode()
	{
		return oreName.hashCode();
	}
}
