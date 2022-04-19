package com.igteam.immersive_geology.common.integrations;

import igteam.immersive_geology.main.IGMultiblockProvider;
import igteam.immersive_geology.processing.recipe.SeparatorRecipe;
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

public class SeparationRecipeCategory extends  IGRecipeCategory<SeparatorRecipe> {

    public static final ResourceLocation ID = new ResourceLocation(IGLib.MODID, "gravityseparator");

    public SeparationRecipeCategory(IGuiHelper guiHelper) {
        super(SeparatorRecipe.class, guiHelper, ID, "machine.immersive_geology.machine_steel_gravityseparator");
        ResourceLocation background = new ResourceLocation(IGLib.MODID, "textures/gui/jei/gravity_separator.png");
        IDrawableStatic back = guiHelper.drawableBuilder(background, 0, 0, 101, 101).setTextureSize(101,101).build();
        setBackground(back);
        setIcon(new ItemStack(IGMultiblockProvider.gravityseparator));
    }

    @Override
    public void setIngredients(SeparatorRecipe recipe, IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(recipe.getItemInputs().get(0).getMatchingStacks()));
        ingredients.setOutputs(VanillaTypes.ITEM, Arrays.asList(recipe.output,
                recipe.waste.getMatchingStacks()[0]));

    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, SeparatorRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(0, true, 15, 32);
        guiItemStacks.set (0, ingredients.getInputs(VanillaTypes.ITEM).get(0));

        guiItemStacks.init(1, false, 66, 41);
        guiItemStacks.set (1, ingredients.getOutputs(VanillaTypes.ITEM).get(0));

        guiItemStacks.init(2, false, 66, 61);
        guiItemStacks.set (2, ingredients.getOutputs(VanillaTypes.ITEM).get(1));
    }

    @Override
    public void draw(SeparatorRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        super.draw(recipe, matrixStack, mouseX, mouseY);
    }


}
