package com.igteam.immersive_geology.common.crafting;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.igteam.immersive_geology.api.crafting.recipes.SeparatorRecipe;
import com.igteam.immersive_geology.api.crafting.recipes.VatRecipe;
import com.igteam.immersive_geology.common.crafting.serializers.ElectrolizerRecipeSerializer;
import com.igteam.immersive_geology.common.crafting.serializers.SeparatorRecipeSerializer;
import com.igteam.immersive_geology.common.crafting.serializers.VatRecipeSerializer;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Serializers {
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, IGLib.MODID);

    public static final RegistryObject<IERecipeSerializer<SeparatorRecipe>> GRAVITY_SEPARATOR_SERIALIZER = RECIPE_SERIALIZERS.register(
            "gravityseparator", SeparatorRecipeSerializer::new
    );
}
