package com.igteam.immersive_geology.common.crafting.serializers;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.StackWithChance;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.igteam.immersive_geology.legacy_api.crafting.recipes.recipe.SeparatorRecipe;
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
    public SeparatorRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        ItemStack output = buffer.readItemStack();
        Ingredient input = Ingredient.read(buffer);
        Ingredient waste = Ingredient.read(buffer);
        int secondaryCount = buffer.readInt();
        SeparatorRecipe recipe = new SeparatorRecipe(recipeId, output, waste, input);
        for(int i = 0; i < secondaryCount; i++)
            recipe.addToSecondaryOutput(StackWithChance.read(buffer));
        return recipe;
    }

    @Override
    public void write(PacketBuffer buffer, SeparatorRecipe recipe) {
        buffer.writeItemStack(recipe.output);
        recipe.input.write(buffer);
        recipe.waste.write(buffer);
        buffer.writeInt(recipe.secondaryOutputs.size());
        for(StackWithChance secondaryOutput : recipe.secondaryOutputs)
            secondaryOutput.write(buffer);
    }
}
