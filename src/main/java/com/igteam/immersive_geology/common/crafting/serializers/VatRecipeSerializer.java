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
        IngredientWithSize input = IngredientWithSize.deserialize((JSONUtils.getJsonObject(json, "item_input")));

        FluidStack fluid_input1 = ApiUtils.jsonDeserializeFluidStack(JSONUtils.getJsonObject(json, "fluid_input1"));
        FluidStack fluid_input2 = ApiUtils.jsonDeserializeFluidStack(JSONUtils.getJsonObject(json, "fluid_input2"));
        ItemStack item_output = readOutput(json.get("result"));
        FluidStack fluid_output = ApiUtils.jsonDeserializeFluidStack(JSONUtils.getJsonObject(json, "fluid_result"));

        int energy = JSONUtils.getInt(json, "energy");
        int time = JSONUtils.getInt(json, "time");

        VatRecipe recipe = new VatRecipe(recipeId, fluid_output, item_output, fluid_input1, fluid_input2, input, energy, time);
        return recipe;
    }

    @Override
    public VatRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {


        IngredientWithSize item_input = IngredientWithSize.read(buffer);
        FluidStack fluid_input1 = FluidStack.readFromPacket(buffer);
        FluidStack fluid_input2 = FluidStack.readFromPacket(buffer);
        ItemStack item_output = buffer.readItemStack();
        FluidStack fluid_output = buffer.readFluidStack();

        int energy = buffer.readInt();
        int time = buffer.readInt();

        return new VatRecipe(recipeId, fluid_output, item_output, fluid_input1, fluid_input2,item_input,energy,time);
    }

    @Override
    public void write(PacketBuffer buffer, VatRecipe recipe) {
        recipe.getItemInputs().get(0).write(buffer);

        recipe.getInputFluids()[0].writeToPacket(buffer);
        recipe.getInputFluids()[1].writeToPacket(buffer);

        buffer.writeItemStack(recipe.getItemOutputs().get(0));
        buffer.writeFluidStack(recipe.getFluidOutputs().get(0));

        buffer.writeInt(recipe.getTotalProcessEnergy());
        buffer.writeInt(recipe.getTotalProcessTime());
    }
}
