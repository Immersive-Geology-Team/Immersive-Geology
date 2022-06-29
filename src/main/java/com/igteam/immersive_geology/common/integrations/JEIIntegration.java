package com.igteam.immersive_geology.common.integrations;


import com.igteam.immersive_geology.core.lib.IGLib;
import igteam.immersive_geology.main.IGMultiblockProvider;
import igteam.immersive_geology.processing.recipe.*;
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
        registration.addRecipeCategories(new HydrojetCutterRecipeCategory(guiHelper));

    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(new ArrayList<>((CalcinationRecipe.recipes.values())),CalcinationRecipeCategory.ID);
        registration.addRecipes(new ArrayList<>((SeparatorRecipe.recipes.values())),SeparationRecipeCategory.ID);
        registration.addRecipes(new ArrayList<>((CrystalRecipe.recipes.values())),CrystalizationRecipeCategory.ID);
        registration.addRecipes(new ArrayList<>((BloomeryRecipe.recipes.values())),BloomeryRecipeCategory.ID);
        registration.addRecipes(new ArrayList<>((VatRecipe.recipes.values())),VatRecipeCategory.ID);
        registration.addRecipes(new ArrayList<>((ReverberationRecipe.recipes.values())),RoastingRecipeCategory.ID);
        registration.addRecipes(new ArrayList<>((HydrojetRecipe.recipes.values())), HydrojetCutterRecipeCategory.ID);


    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {

        registration.addRecipeCatalyst(new ItemStack(IGMultiblockProvider.rotarykiln),
                CalcinationRecipeCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(IGMultiblockProvider.gravityseparator),
                SeparationRecipeCategory.ID);

        registration.addRecipeCatalyst(new ItemStack(IGMultiblockProvider.crystallizer),
                CrystalizationRecipeCategory.ID);
        registration.addRecipeCatalyst(new ItemStack(IGMultiblockProvider.bloomery),
                BloomeryRecipeCategory.ID);

        registration.addRecipeCatalyst(new ItemStack(IGMultiblockProvider.hydrojet_cutter),
                HydrojetCutterRecipeCategory.ID);

        registration.addRecipeCatalyst(new ItemStack(IGMultiblockProvider.chemicalvat),
                VatRecipeCategory.ID);

        registration.addRecipeCatalyst(new ItemStack(IGMultiblockProvider.reverberation_furnace),
                RoastingRecipeCategory.ID);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        IModPlugin.super.registerGuiHandlers(registration);
    }


}
