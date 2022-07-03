package com.igteam.immersive_geology.common.integrations;

import com.igteam.immersive_geology.core.lib.IGLib;
import com.mojang.blaze3d.matrix.MatrixStack;
import igteam.immersive_geology.main.IGMultiblockProvider;
import igteam.immersive_geology.processing.recipe.HydrojetRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

public class HydrojetCutterRecipeCategory extends  IGRecipeCategory<HydrojetRecipe> {

    public static final ResourceLocation ID = new ResourceLocation(IGLib.MODID, "hydrojet");

    public HydrojetCutterRecipeCategory(IGuiHelper guiHelper) {
        super(HydrojetRecipe.class, guiHelper, ID, "machine.immersive_geology.hydrojet");
        ResourceLocation background = new ResourceLocation(IGLib.MODID, "textures/gui/jei/hydrojet_cutter.png");
        IDrawableStatic back = guiHelper.drawableBuilder(background, 0, 0, 101, 101)
                .setTextureSize(101,101).build();
        setBackground(back);
        ResourceLocation icon = new ResourceLocation(IGLib.MODID, "textures/gui/jei/hydrojet_icon.png");
        IDrawableStatic usedIcon = guiHelper.drawableBuilder(icon, 0,0,16,16).setTextureSize(16,16).build();
        setIcon(usedIcon);

    }

    @Override
    public void setIngredients(HydrojetRecipe recipe, IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(recipe.getItemInput().getMatchingStacks()));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());

    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, HydrojetRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(0, true, 12, 35);
        guiItemStacks.set (0, ingredients.getInputs(VanillaTypes.ITEM).get(0));

        guiItemStacks.init(1, false, 66, 36);
        guiItemStacks.set (1, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }

    @Override
    public void draw(HydrojetRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        super.draw(recipe, matrixStack, mouseX, mouseY);
    }
}