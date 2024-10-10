/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.tags.ITag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

public abstract class IGRecipeStage
{
	private final String name;
	private String description;

	private Set<IGRecipeMethod> methods = new LinkedHashSet<>();
	private MaterialHelper material;

	public IGRecipeStage(MaterialHelper material, IGStageDesignation designation){
		this.name = designation.name();
		this.material = material;
		material.addStage(this);
		describe();
		this.description = material.getName() + " " + designation.name();
	}

	public IGRecipeStage(MaterialHelper material, IGStageDesignation designation, String description){
		this(material, designation);
		this.description = description;
	}

	public MaterialHelper getParentMaterial(){
		return material;
	}

	protected void describe(){};

	public Set<IGRecipeMethod> getMethods(){
		return methods;
	}

	public void addMethod(IGRecipeMethod m) {
		methods.add(m);
	}

	public String getStageName(){
		return name;
	}
}
