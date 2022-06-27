package igteam.immersive_geology.processing.serializers;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.google.gson.JsonObject;
import igteam.immersive_geology.main.IGMultiblockProvider;
import igteam.immersive_geology.processing.recipe.HydrojetRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class HydrojetRecipeSerializer extends IERecipeSerializer<HydrojetRecipe> {
    //Class is a used for HydroJetRecipe

    @Override
    public ItemStack getIcon() {
        return new ItemStack(IGMultiblockProvider.hydrojet_cutter);
    }

    @Override
    public HydrojetRecipe readFromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
        return null;
    }

    @Nullable
    @Override
    public HydrojetRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        return null;
    }

    @Override
    public void write(PacketBuffer buffer, HydrojetRecipe recipe) {

    }
}
