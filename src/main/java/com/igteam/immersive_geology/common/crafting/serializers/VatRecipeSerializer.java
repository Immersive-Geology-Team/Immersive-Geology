package com.igteam.immersive_geology.common.crafting.serializers;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.StackWithChance;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.igteam.immersive_geology.api.crafting.recipes.recipe.SeparatorRecipe;
import com.igteam.immersive_geology.api.crafting.recipes.recipe.VatRecipe;
import com.igteam.immersive_geology.core.registration.IGMultiblockRegistrationHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;

public class VatRecipeSerializer extends IERecipeSerializer<VatRecipe> {

    @Override
    public ItemStack getIcon() {
        return new ItemStack(IGMultiblockRegistrationHolder.Multiblock.chemicalvat);
    }

    @Override
    public VatRecipe readFromJson(ResourceLocation recipeId, JsonObject json) {
        ItemStack item_output = readOutput(json.get("result"));

        IngredientWithSize input = IngredientWithSize.deserialize((JSONUtils.getJsonObject(json, "item_input")));
        FluidStack fluid_output = ApiUtils.jsonDeserializeFluidStack(JSONUtils.getJsonObject(json, "fluid_result"));
        FluidTagInput fluid_input1 = FluidTagInput.deserialize(JSONUtils.getJsonObject(json, "fluid_input1"));
        FluidTagInput fluid_input2 = FluidTagInput.deserialize(JSONUtils.getJsonObject(json, "fluid_input2"));
        int energy = JSONUtils.getInt(json, "energy");
        int time = JSONUtils.getInt(json, "time");
        VatRecipe recipe = new VatRecipe(recipeId, fluid_output, item_output, fluid_input1, fluid_input2, input, energy, time);
        return recipe;
    }

    @Override
    public VatRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        FluidStack fluid_output = buffer.readFluidStack();
        FluidTagInput fluid_input1 = FluidTagInput.read(buffer);
        FluidTagInput fluid_input2 = FluidTagInput.read(buffer);

        ItemStack item_output = buffer.readItemStack();
        IngredientWithSize item_input = IngredientWithSize.read(buffer);

        int energy = buffer.readInt();
        int time = buffer.readInt();

        return new VatRecipe(recipeId, fluid_output, item_output, fluid_input1,fluid_input2,item_input,energy,time);
    }

    @Override
    public void write(PacketBuffer buffer, VatRecipe recipe) {
        buffer.writeFluidStack(recipe.getFluidOutputs().get(0));
        recipe.getInputFluids()[0].write(buffer);
        recipe.getInputFluids()[1].write(buffer);

        buffer.writeItemStack(recipe.getItemOutputs().get(0));
        recipe.getItemInputs().get(0).write(buffer);

        buffer.writeInt(recipe.getTotalProcessEnergy());
        buffer.writeInt(recipe.getTotalProcessTime());
    }
}
