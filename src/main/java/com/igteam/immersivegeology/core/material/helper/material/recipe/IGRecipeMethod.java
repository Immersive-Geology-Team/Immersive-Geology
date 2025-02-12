/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe;

import blusunrize.immersiveengineering.client.gui.info.FluidInfoArea;
import blusunrize.lib.manual.gui.ManualScreen;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeChain;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.function.Consumer;

public abstract class IGRecipeMethod
{
	protected final MaterialHelper parentMaterial;
	private IGRecipeNode node;

	protected int render_x_space;
	public IGRecipeMethod(IGRecipeStage stage)
	{
		stage.addMethod(this);
		this.parentMaterial = stage.getParentMaterial();
		this.render_x_space = 24;
	}
	public IGRecipeNode getNode() {
		return node;
	}

	public void setNode(IGRecipeNode node) {
		this.node = node;
	}

	public IGRecipeNode addToTree(IGRecipeChain chain) {
		chain.addMethod(this);
		return this.getNode();
	}

	public IGRecipeNode addToTree(IGRecipeChain chain, IGRecipeNode parent) {
		chain.addChild(parent.getMethod(), this);
		return this.getNode();
	}

	public IGRecipeNode joinBranches(IGRecipeChain chain, IGRecipeNode branch1, IGRecipeNode branch2) {
		chain.join(branch1, branch2, this);
		return this.getNode();
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

	public ItemStack getIconStack()
	{
		return ItemStack.EMPTY;
	}

	public abstract void basicRender(GuiGraphics graphics, ManualScreen screen, int x, int y, int mx, int my);

	public void renderItemStack(GuiGraphics graphics, ItemStack stack, int x, int y, int mx, int my)
	{
		graphics.renderItem(stack, x, y, mx, my);
		if(mx > x&&(x+16) > mx && my > y&&(y+16) > my)
		{
			graphics.renderTooltip(Minecraft.getInstance().font, stack, mx, my);
		}
	}

	public void renderFluidStack(GuiGraphics graphics, Fluid stack, int x, int y, int mx, int my)
	{
		FluidTank tank = new FluidTank(128);
		tank.setFluid(new FluidStack(stack, 128));
		FluidInfoArea fluid = new FluidInfoArea(tank, new Rect2i(x, y, 16, 16), 0,0, 0,0, IGLib.makeTextureLocation("reverberation_furnace"));
		fluid.draw(graphics);
		if(mx > x && (x+16) > mx && my > y && (y+16) > my)
		{
			graphics.renderTooltip(Minecraft.getInstance().font, tank.getFluid().getDisplayName(), mx,my);
		}
	}

	public void renderMB(GuiGraphics graphics, ItemStack stack, int x, int y, int mx, int my)
	{
		graphics.renderItem(stack, x, y, mx, my);
		if(mx > x&&(x+16) > mx && my > y&&(y+16) > my)
		{
			graphics.renderTooltip(Minecraft.getInstance().font, stack.getDisplayName(), x, y);
		}
	}

	public int getXSpace()
	{
		return render_x_space;
	}

	public abstract void renderOutput(GuiGraphics graphics, ItemStack iconStack, int methodNameX, int methodNameY, int mx, int my);

	public void renderAdditionalInputs(GuiGraphics graphics, ItemStack iconStack, int methodNameX, int methodNameY, int mx, int my)
	{
	};

	public boolean hasAdditionalInputRenders()
	{
		return false;
	}

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
		SYNTHESIS,
		CUTTING,
		REFINING,
		PELLETIZE;

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
				case SYNTHESIS -> {return ie("refinery");}
				case ARC_SMELTING -> {return ie("arc_furnace");}
				case CALCINATION -> {return ig("rotarykiln");}
				case BASIC_SMELTING -> {return mc("furnace");}
				case REFINING -> {return ig("ballmill");}
				case PELLETIZE -> {return ig("pelletizer");}
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

	protected String create_advanced_method_name(IFlagType<?> starting_form, IFlagType<?> output_form)
	{
		return starting_form.getName().toLowerCase() + "_" + parentMaterial.getName() + "_to_" + parentMaterial.getProductionMaterial().getName() + output_form.getName().toLowerCase();
	}

	protected String create_advanced_method_name(IFlagType<?> output_form)
	{
		return parentMaterial.getName() + "_to_" + parentMaterial.getProductionMaterial().getName() + output_form.getName().toLowerCase();
	}

	protected String create_basic_method_name(IFlagType<?> output_form)
	{
		return "create_" + parentMaterial.getName() + "_" + output_form.getName();
	}
}
