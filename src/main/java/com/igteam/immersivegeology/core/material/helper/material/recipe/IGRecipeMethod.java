/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.function.Consumer;

public abstract class IGRecipeMethod
{
	protected final MaterialHelper parentMaterial;
	public IGRecipeMethod(IGRecipeStage stage)
	{
		stage.addMethod(this);
		this.parentMaterial = stage.getParentMaterial();
	}

	public abstract @Nonnull RecipeMethod getMethod();

	public abstract ResourceLocation getLocation();

	private static final HashMap<String, Integer> PATH_COUNT = new HashMap<>();
	protected ResourceLocation toRL(String s)
	{
		if(!s.contains("/"))
			s = "crafting/"+s;
		if(PATH_COUNT.containsKey(s))
		{
			int count = PATH_COUNT.get(s)+1;
			PATH_COUNT.put(s, count);
			return new ResourceLocation(IGLib.MODID, s+count);
		}
		PATH_COUNT.put(s, 1);
		return new ResourceLocation(IGLib.MODID, s);
	}

	public void clearRecipePath(){
		PATH_COUNT.clear();
	}

	public abstract String getName();

	public abstract boolean build(Consumer<FinishedRecipe> consumer);

	public enum RecipeMethod
	{
		CRAFTING,
		SEPARATOR,
		BLOOMERY,
		CHEMICAL,
		ROASTING,
		CALCINATION,
		CRYSTALLIZATION,
		BLASTING,
		CRUSHING,
		BASIC_SMELTING,
		ARC_SMELTING,
		SYNETHESIS,
		CUTTING;

		public String getMethodName()
		{
			switch(this)
			{
				case CRAFTING -> {return "crafting_table";}
				case CUTTING -> { return ig("hydrojet"); }
				case BLASTING -> {return ie("crude_blast_furnace");}
				case BLOOMERY -> {return ig("bloomery");}
				case CHEMICAL -> {return ig("chemical_reactor");}
				case CRUSHING -> {return ie("crusher");}
				case ROASTING -> {return ig("reverberation_furnace");}
				case SYNETHESIS -> {return ie("refinery");}
				case ARC_SMELTING -> {return ie("arc_furnace");}
				case CALCINATION -> {return ig("rotarykiln");}
				case BASIC_SMELTING -> {return mc("furnace");}
				case CRYSTALLIZATION -> {return ig("crystallizer");}
			}
			return "unknown";
		}
	}

	private static String ig(String suffix)
	{
		return IGLib.MODID + ":" + suffix;
	}

	private static String ie(String suffix)
	{
		return "immersiveengineering:" + suffix;
	}

	private static String mc(String suffix)
	{
		return "minecraft:" + suffix;
	}

	protected String create_basic_method_name(IFlagType<?> starting_form, IFlagType<?> output_form)
	{
		return starting_form.getName().toLowerCase() + "_" + parentMaterial.getName() + "_to_" + output_form.getName().toLowerCase();
	}
}
