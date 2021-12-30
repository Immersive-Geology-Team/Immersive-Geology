package com.igteam.immersive_geology.common.integrations;


import com.igteam.immersive_geology.api.crafting.IGMultiblockRecipe;
import com.igteam.immersive_geology.api.crafting.recipes.recipe.*;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGMultiblockRegistrationHolder;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;

@JeiPlugin
public class JEIIntegration  implements IModPlugin{

    private static final ResourceLocation ID = new ResourceLocation(IGLib.MODID, "main");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new CalcinationRecipeCategory(guiHelper));
        registration.addRecipeCategories(new SeparationRecipeCategory(guiHelper));
        registration.addRecipeCategories(new CrystalizationRecipeCategory(guiHelper));
        registration.addRecipeCategories(new BloomeryRecipeCategory(guiHelper));
        registration.addRecipeCategories(new VatRecipeCategory(guiHelper));
        registration.addRecipeCategories(new RoastingRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(new ArrayList<>((CalcinationRecipe.recipes.values())),CalcinationRecipeCategory.ID);
        registration.addRecipes(new ArrayList<>((SeparatorRecipe.recipes.values())),SeparationRecipeCategory.ID);
        registration.addRecipes(new ArrayList<>((CrystalRecipe.recipes.values())),CrystalizationRecipeCategory.ID);
        registration.addRecipes(new ArrayList<>((BloomeryRecipe.recipes.values())),BloomeryRecipeCategory.ID);
        registration.addRecipes(new ArrayList<>((VatRecipe.recipes.values())),VatRecipeCategory.ID);
        registration.addRecipes(new ArrayList<>((ReverberationRecipe.recipes.values())),RoastingRecipeCategory.ID);

    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {

        registration.addRecipeCatalyst(new ItemStack(IGMultiblockRegistrationHolder.Multiblock.rotarykiln),
                CalcinationRecipeCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(IGMultiblockRegistrationHolder.Multiblock.gravityseparator),
                SeparationRecipeCategory.ID);

        registration.addRecipeCatalyst(new ItemStack(IGMultiblockRegistrationHolder.Multiblock.crystallizer),
                CrystalizationRecipeCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(IGMultiblockRegistrationHolder.Multiblock.bloomery),
                BloomeryRecipeCategory.ID);

        registration.addRecipeCatalyst(new ItemStack(IGMultiblockRegistrationHolder.Multiblock.chemicalvat),
                VatRecipeCategory.ID);

        registration.addRecipeCatalyst(new ItemStack(IGMultiblockRegistrationHolder.Multiblock.reverberation_furnace),
                RoastingRecipeCategory.ID);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        IModPlugin.super.registerGuiHandlers(registration);
    }


}
