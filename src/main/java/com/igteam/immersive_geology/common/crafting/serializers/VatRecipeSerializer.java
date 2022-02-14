package com.igteam.immersive_geology.common.crafting.serializers;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.gson.JsonObject;
import com.igteam.immersive_geology.legacy_api.crafting.recipes.recipe.VatRecipe;
import com.igteam.immersive_geology.core.registration.IGMultiblockRegistrationHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class VatRecipeSerializer extends IERecipeSerializer<VatRecipe> {

    @Override
    public ItemStack getIcon() {
        return new ItemStack(IGMultiblockRegistrationHolder.Multiblock.chemicalvat);
    }

    @Override
    public VatRecipe readFromJson(ResourceLocation recipeId, JsonObject json) {

        IngredientWithSize input = IngredientWithSize.of(ItemStack.EMPTY);

        if(json.get("item_input") != null)
            input = IngredientWithSize.deserialize((JSONUtils.getJsonObject(json, "item_input")));

        FluidTagInput fluid_input1 = FluidTagInput.deserialize(JSONUtils.getJsonObject(json, "fluid_input1"));

        FluidTagInput fluid_input2 = null;
        if(json.has("fluid_input2")) {
            fluid_input2 = FluidTagInput.deserialize(JSONUtils.getJsonObject(json, "fluid_input2"));
        }

        ItemStack item_output = ItemStack.EMPTY;
        if(json.get("result") != null) {
            item_output = readOutput(json.get("result"));
        }
        FluidStack fluid_output = ApiUtils.jsonDeserializeFluidStack(JSONUtils.getJsonObject(json, "fluid_result"));

        int energy = JSONUtils.getInt(json, "energy");
        int time = JSONUtils.getInt(json, "time");

        VatRecipe recipe = new VatRecipe(recipeId, fluid_output, item_output, fluid_input1, fluid_input2, input, energy, time);
        return recipe;
    }

    @Override
    public VatRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        boolean hasItemInput = buffer.readBoolean();
        IngredientWithSize item_input = IngredientWithSize.of(ItemStack.EMPTY);

        if(hasItemInput) item_input = IngredientWithSize.read(buffer);


        FluidTagInput fluid_input1 = FluidTagInput.read(buffer);

        boolean hasSecondaryFluidInput = buffer.readBoolean();

        FluidTagInput fluid_input2 = null;
        if(hasSecondaryFluidInput) fluid_input2 = FluidTagInput.read(buffer);


        ItemStack item_output = buffer.readItemStack();
        FluidStack fluid_output = buffer.readFluidStack();

        int energy = buffer.readInt();
        int time = buffer.readInt();

        VatRecipe recipe = new VatRecipe(recipeId, fluid_output, item_output, fluid_input1, fluid_input2,item_input,energy,time);
        return recipe;
    }

    @Override
    public void write(PacketBuffer buffer, VatRecipe recipe) {
        boolean hasInputItem = recipe.getItemInput() != null;
        buffer.writeBoolean(hasInputItem);
        if(hasInputItem) {
            recipe.getItemInput().write(buffer);
        }

        List<FluidTagInput> inputFluids = recipe.getInputFluids();
        inputFluids.get(0).write(buffer);

        boolean hasSecondaryFluidInput = inputFluids.size() > 1;
        buffer.writeBoolean(hasSecondaryFluidInput);
        if(hasSecondaryFluidInput)
        inputFluids.get(1).write(buffer);

        buffer.writeItemStack(recipe.getItemOutput());

        if(recipe.getFluidOutput() != null)
        buffer.writeFluidStack(recipe.getFluidOutput());

        buffer.writeInt(recipe.getTotalProcessEnergy());
        buffer.writeInt(recipe.getTotalProcessTime());
    }
}
