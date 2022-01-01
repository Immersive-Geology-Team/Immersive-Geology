package com.igteam.immersive_geology.common.crafting.serializers;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.gson.JsonObject;
import com.igteam.immersive_geology.api.crafting.recipes.recipe.CalcinationRecipe;
import com.igteam.immersive_geology.api.crafting.recipes.recipe.CrystalRecipe;
import com.igteam.immersive_geology.core.registration.IGMultiblockRegistrationHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class CalcinationRecipeSerializer extends IERecipeSerializer<CalcinationRecipe> {

    @Override
    public ItemStack getIcon() {
        return new ItemStack(IGMultiblockRegistrationHolder.Multiblock.crystallizer);
    }

    @Override
    public CalcinationRecipe readFromJson(ResourceLocation recipeId, JsonObject json) {
        IngredientWithSize item_input = IngredientWithSize.deserialize((JSONUtils.getJsonObject(json, "item_input")));
        ItemStack item_output = readOutput(json.get("result"));

        int energy = JSONUtils.getInt(json, "energy");
        int time = JSONUtils.getInt(json, "time");
        CalcinationRecipe recipe = new CalcinationRecipe(recipeId,  item_output, item_input, energy, time);
        return recipe;
    }

    @Override
    public CalcinationRecipe read(ResourceLocation resourceLocation, PacketBuffer packetBuffer)
    {
        IngredientWithSize item_input = IngredientWithSize.read(packetBuffer);
        ItemStack item_output = packetBuffer.readItemStack();
        int energy = packetBuffer.readInt();
        int time = packetBuffer.readInt();
        CalcinationRecipe recipe = new CalcinationRecipe(resourceLocation,  item_output, item_input, energy, time);
        return recipe;

    }

    @Override
    public void write(PacketBuffer packetBuffer, CalcinationRecipe calcinationRecipe) {
        calcinationRecipe.getItemInput().write(packetBuffer);
        packetBuffer.writeItemStack(calcinationRecipe.getRecipeOutput());

        packetBuffer.writeInt(calcinationRecipe.getTotalProcessEnergy());
        packetBuffer.writeInt(calcinationRecipe.getTotalProcessTime());
    }
}
