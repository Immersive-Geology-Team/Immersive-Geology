package com.igteam.immersive_geology.common.integrations;

import com.igteam.immersive_geology.legacy_api.crafting.recipes.recipe.CalcinationRecipe;
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

public class CalcinationRecipeCategory extends  IGRecipeCategory<CalcinationRecipe> {

    public static final ResourceLocation ID = new ResourceLocation(IGLib.MODID, "rotarykiln");

    public CalcinationRecipeCategory(IGuiHelper guiHelper) {
        super(CalcinationRecipe.class, guiHelper, ID, "machine.immersive_geology.machine_steel_rotarykiln");
        ResourceLocation background = new ResourceLocation(IGLib.MODID, "textures/gui/jei/rotary_kiln.png");
        IDrawableStatic back = guiHelper.drawableBuilder(background, 0, 0, 101, 101)
                .setTextureSize(101,101).build();
        setBackground(back);
        setIcon(new ItemStack(IGMultiblockRegistrationHolder.Multiblock.rotarykiln));

    }

    @Override
    public void setIngredients(CalcinationRecipe recipe, IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(recipe.getItemInput().getMatchingStacks()));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());

    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CalcinationRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(0, true, 15, 32);
        guiItemStacks.set (0, ingredients.getInputs(VanillaTypes.ITEM).get(0));

        guiItemStacks.init(1, false, 66, 41);
        guiItemStacks.set (1, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }

    @Override
    public void draw(CalcinationRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        super.draw(recipe, matrixStack, mouseX, mouseY);
    }
}
