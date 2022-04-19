package igteam.immersive_geology.processing;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.processing.recipe.*;
import igteam.immersive_geology.processing.serializers.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Serializers {
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, IGApi.MODID);

    public static final RegistryObject<IERecipeSerializer<ReverberationRecipe>> REVERBERATION_SERIALIZER = RECIPE_SERIALIZERS.register(
            "reverberation_furnace", ReverberationRecipeSerializer::new
    );

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

    public static final RegistryObject<IERecipeSerializer<CalcinationRecipe>> ROTARY_KILN_SERIALIZER = RECIPE_SERIALIZERS.register(
            "rotary_kiln", CalcinationRecipeSerializer::new
    );
}
