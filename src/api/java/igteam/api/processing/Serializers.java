package igteam.api.processing;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import igteam.api.IGApi;
import igteam.api.processing.recipe.*;
import igteam.api.processing.serializers.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Serializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, IGApi.MODID);

    //Used in HydroJetRecipe - Dependent on HydroJetRecipeSerializer
    //Ensure Name used is the same as the one used in IGTileType, HydroJetRecipe and so on.
    public static final RegistryObject<IERecipeSerializer<HydrojetRecipe>> HYDROJET_CUTTER_SERIALIZER = RECIPE_SERIALIZERS.register(
            "hydrojet_cutter", HydrojetRecipeSerializer::new
    );

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
