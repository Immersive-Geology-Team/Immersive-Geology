package com.igteam.immersive_geology.common.integrations;

import com.igteam.immersive_geology.api.crafting.recipes.recipe.CalcinationRecipe;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGMultiblockRegistrationHolder;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

public class CalcinationRecipeCategory extends  IGRecipeCategory<CalcinationRecipe> {

    public static final ResourceLocation ID = new ResourceLocation(IGLib.MODID, "rotarykiln");

    public CalcinationRecipeCategory(IGuiHelper guiHelper) {
        super(CalcinationRecipe.class, guiHelper, ID, "machine.immersive_geology.rotarykiln");
        ResourceLocation background = new ResourceLocation(IGLib.MODID, "textures/gui/jei/rotarykiln.png");
        setBackground(guiHelper.createDrawable(background, 0, 0, 128, 128));
        setIcon(new ItemStack(IGMultiblockRegistrationHolder.Multiblock.rotarykiln));

    }

    @Override
    public void setIngredients(CalcinationRecipe recipe, IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(recipe.getItemInputs().get(0).getMatchingStacks()));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getItemOutputs().get(0));

    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CalcinationRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(0, true, 5, 50);
        guiItemStacks.set (0, ingredients.getInputs(VanillaTypes.ITEM).get(0));

        guiItemStacks.init(1, false, 33, 0);
        guiItemStacks.set (1, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }

    @Override
    public void draw(CalcinationRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        super.draw(recipe, matrixStack, mouseX, mouseY);
    }
}
