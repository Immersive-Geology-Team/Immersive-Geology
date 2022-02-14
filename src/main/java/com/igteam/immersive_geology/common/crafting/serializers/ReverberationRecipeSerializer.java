package com.igteam.immersive_geology.common.crafting.serializers;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.gson.JsonObject;
import com.igteam.immersive_geology.legacy_api.crafting.recipes.recipe.ReverberationRecipe;
import com.igteam.immersive_geology.core.registration.IGMultiblockRegistrationHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class ReverberationRecipeSerializer extends IERecipeSerializer<ReverberationRecipe>
{

    @Override
    public ItemStack getIcon() {
        return new ItemStack(IGMultiblockRegistrationHolder.Multiblock.reverberation_furnace);
    }

    @Override
    public ReverberationRecipe readFromJson(ResourceLocation recipeId, JsonObject json) {
        ItemStack output = readOutput(json.get("result"));
        Integer time = JSONUtils.getInt(json, "time");
        Float wasteMult = JSONUtils.getFloat(json, "wasteMultiplier");
        IngredientWithSize input = IngredientWithSize.deserialize(JSONUtils.getJsonObject(json, "item_input"));
        ReverberationRecipe recipe = new ReverberationRecipe(recipeId, output, input, time, wasteMult);
        return recipe;
    }

    @Override
    public ReverberationRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        ItemStack output = buffer.readItemStack();
        IngredientWithSize input = IngredientWithSize.read(buffer);
        Integer time = buffer.readInt();
        Float wasteMult = buffer.readFloat();
        ReverberationRecipe recipe = new ReverberationRecipe(recipeId, output, input, time, wasteMult);
        return recipe;
    }

    @Override
    public void write(PacketBuffer buffer, ReverberationRecipe recipe) {
        buffer.writeItemStack(recipe.output);
        recipe.input.write(buffer);
        buffer.writeInt(recipe.getTime());
        buffer.writeFloat(recipe.getWasteMultipler());
    }
}
