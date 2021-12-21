package com.igteam.immersive_geology.common.crafting;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.igteam.immersive_geology.api.crafting.recipes.recipe.BloomeryRecipe;
import com.igteam.immersive_geology.api.crafting.recipes.recipe.CrystalRecipe;
import com.igteam.immersive_geology.api.crafting.recipes.recipe.SeparatorRecipe;
import com.igteam.immersive_geology.api.crafting.recipes.recipe.VatRecipe;
import com.igteam.immersive_geology.common.crafting.serializers.BloomeryRecipeSerializer;
import com.igteam.immersive_geology.common.crafting.serializers.CrystalizerRecipeSerializer;
import com.igteam.immersive_geology.common.crafting.serializers.SeparatorRecipeSerializer;
import com.igteam.immersive_geology.common.crafting.serializers.VatRecipeSerializer;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Serializers {
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, IGLib.MODID);

    public static final RegistryObject<IERecipeSerializer<BloomeryRecipe>> BLOOMERY_SERIALIZER = RECIPE_SERIALIZERS.register(
            "bloomery", BloomeryRecipeSerializer::new
    );

    public static final RegistryObject<IERecipeSerializer<VatRecipe>> CHEMICAL_VAT_SERIALIZER = RECIPE_SERIALIZERS.register(
            "chemicalvat", VatRecipeSerializer::new
    );

    public static final RegistryObject<IERecipeSerializer<SeparatorRecipe>> GRAVITY_SEPARATOR_SERIALIZER = RECIPE_SERIALIZERS.register(
            "gravityseparator", SeparatorRecipeSerializer::new
    );
    public static final RegistryObject<IERecipeSerializer<CrystalRecipe>> CRYSTALIZER_SERIALIZER = RECIPE_SERIALIZERS.register(
            "crystalizer", CrystalizerRecipeSerializer::new
    );
}
