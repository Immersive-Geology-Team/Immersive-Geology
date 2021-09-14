package com.igteam.immersive_geology.common.crafting.serializers;

import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.StackWithChance;
import blusunrize.immersiveengineering.common.config.IEServerConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.igteam.immersive_geology.api.crafting.recipes.SeparatorRecipe;
import com.igteam.immersive_geology.core.registration.IGMultiblockRegistrationHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

public class SeparatorRecipeSerializer extends IERecipeSerializer<SeparatorRecipe>
{

    @Override
    public ItemStack getIcon() {
        return new ItemStack(IGMultiblockRegistrationHolder.Multiblock.gravityseparator);
    }

    @Override
    public SeparatorRecipe readFromJson(ResourceLocation recipeId, JsonObject json) {
        ItemStack output = readOutput(json.get("result"));
        Ingredient input = Ingredient.deserialize(JSONUtils.getJsonObject(json, "input"));
        Ingredient waste = Ingredient.deserialize(JSONUtils.getJsonObject(json, "waste"));
        JsonArray array = json.getAsJsonArray("secondaries");
        int energy = JSONUtils.getInt(json, "energy");
        SeparatorRecipe recipe = new SeparatorRecipe(recipeId, output, waste, input);
        for(int i = 0; i < array.size(); i++)
        {
            JsonObject element = array.get(i).getAsJsonObject();
            if(CraftingHelper.processConditions(element, "conditions"))
            {
                float chance = JSONUtils.getFloat(element, "chance");
                ItemStack stack = readOutput(element.get("output"));
                recipe.addToSecondaryOutput(new StackWithChance(stack, chance));
            }
        }
        return recipe;
    }

    @Override
    public SeparatorRecipe read(ResourceLocation resourceLocation, PacketBuffer packetBuffer) {
        return null;
    }

    @Override
    public void write(PacketBuffer packetBuffer, SeparatorRecipe separatorRecipe) {

    }
}
