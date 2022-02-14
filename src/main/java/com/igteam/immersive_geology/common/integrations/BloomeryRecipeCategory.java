package com.igteam.immersive_geology.common.integrations;

import com.igteam.immersive_geology.legacy_api.crafting.recipes.recipe.BloomeryRecipe;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGMultiblockRegistrationHolder;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

public class BloomeryRecipeCategory extends  IGRecipeCategory<BloomeryRecipe> {

    public static final ResourceLocation ID = new ResourceLocation(IGLib.MODID, "bloomery");

    public BloomeryRecipeCategory(IGuiHelper guiHelper) {
        super(BloomeryRecipe.class, guiHelper, ID, "machine.immersive_geology.bloomery");
        ResourceLocation background = new ResourceLocation(IGLib.MODID, "textures/gui/jei/bloomery.png");
        IDrawableStatic back = guiHelper.drawableBuilder(background, 0, 0, 101, 101)
                .setTextureSize(101,101).build();
        setBackground(back);
        setIcon(new ItemStack(IGMultiblockRegistrationHolder.Multiblock.bloomery));

    }

    @Override
    public void setIngredients(BloomeryRecipe recipe, IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(recipe.getRecipeInput().getMatchingStacks()));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());

    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, BloomeryRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(0, true, 16, 40);
        guiItemStacks.set (0, ingredients.getInputs(VanillaTypes.ITEM).get(0));

        guiItemStacks.init(1, false, 67, 40);
        guiItemStacks.set (1, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }

    @Override
    public void draw(BloomeryRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        super.draw(recipe, matrixStack, mouseX, mouseY);
    }
}
