package com.igteam.immersive_geology.common.integrations;

import com.igteam.immersive_geology.api.crafting.recipes.recipe.CrystalRecipe;
import com.igteam.immersive_geology.api.crafting.recipes.recipe.VatRecipe;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGMultiblockRegistrationHolder;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VatRecipeCategory extends  IGRecipeCategory<VatRecipe> {

    public static final ResourceLocation ID = new ResourceLocation(IGLib.MODID, "chemicalvat");

    public VatRecipeCategory(IGuiHelper guiHelper) {
        super(VatRecipe.class, guiHelper, ID, "machine.immersive_geology.chemicalvat");
        ResourceLocation background = new ResourceLocation(IGLib.MODID, "textures/gui/jei/vat.png");
        IDrawableStatic back = guiHelper.drawableBuilder(background, 0, 0, 128, 101)
                .setTextureSize(128,101).build();
        setBackground(back);
        setIcon(new ItemStack(IGMultiblockRegistrationHolder.Multiblock.chemicalvat));

    }

    @Override
    public void setIngredients(VatRecipe recipe, IIngredients ingredients) {
        List<List<FluidStack>> l = new ArrayList();
        if (recipe.getInputFluids().size() >=1 && recipe.getInputFluids().get(0) != null) {
            l.add(recipe.getInputFluids().get(0).getMatchingFluidStacks());
        }

        if (recipe.getInputFluids().size() >=2 && recipe.getInputFluids().get(1) != null) {
            l.add(recipe.getInputFluids().get(1).getMatchingFluidStacks());
        }

        ingredients.setInputLists(VanillaTypes.FLUID, l);
        if (recipe.getItemInputs().size() != 0) {
            ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(recipe.getItemInputs().get(0).getMatchingStacks()));
        }

        if (recipe.getItemOutputs().size() != 0 && !recipe.getItemOutputs().get(0).isEmpty()){
            ingredients.setOutput(VanillaTypes.ITEM, recipe.getItemOutputs().get(0));
        }
        ingredients.setOutput(VanillaTypes.FLUID, recipe.getFluidOutputs().get(0));

    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, VatRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

        guiFluidStacks.init(0, true, 15, 45);
        guiFluidStacks.set (0, ingredients.getInputs(VanillaTypes.FLUID).get(0));

        if (ingredients.getInputs(VanillaTypes.FLUID).size() >1) {
            guiFluidStacks.init(1, true, 40, 59);
            guiFluidStacks.set(1, ingredients.getInputs(VanillaTypes.FLUID).get(1));
        }
        if (ingredients.getInputs(VanillaTypes.ITEM).size() != 0) {
            guiItemStacks.init(0, true, 14, 68);
            guiItemStacks.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        }

        guiFluidStacks.init(2, false, 93, 45);
        guiFluidStacks.set (2, ingredients.getOutputs(VanillaTypes.FLUID).get(0));

        if (ingredients.getOutputs(VanillaTypes.ITEM).size() != 0) {
            guiItemStacks.init(1, false, 92, 68);
            guiItemStacks.set(1, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
        }
    }

    @Override
    public void draw(VatRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        super.draw(recipe, matrixStack, mouseX, mouseY);
    }
}
