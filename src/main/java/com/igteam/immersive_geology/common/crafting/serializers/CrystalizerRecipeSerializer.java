package com.igteam.immersive_geology.common.crafting.serializers;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.google.gson.JsonObject;
import com.igteam.immersive_geology.legacy_api.crafting.recipes.recipe.CrystalRecipe;
import com.igteam.immersive_geology.core.registration.IGMultiblockRegistrationHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class CrystalizerRecipeSerializer extends IERecipeSerializer<CrystalRecipe> {

    @Override
    public ItemStack getIcon() {
        return new ItemStack(IGMultiblockRegistrationHolder.Multiblock.crystallizer);
    }

    @Override
    public CrystalRecipe readFromJson(ResourceLocation recipeId, JsonObject json) {
        FluidTagInput fluid_input = FluidTagInput.deserialize(JSONUtils.getJsonObject(json, "fluid_input"));
        ItemStack item_output = readOutput(json.get("result"));

        int energy = JSONUtils.getInt(json, "energy");
        int time = JSONUtils.getInt(json, "time");
        CrystalRecipe recipe = new CrystalRecipe(recipeId,  item_output, fluid_input, energy, time);
        return recipe;
    }

    @Override
    public CrystalRecipe read(ResourceLocation resourceLocation, PacketBuffer packetBuffer)
    {
        FluidTagInput fluid_input = FluidTagInput.read(packetBuffer);
        ItemStack item_output = packetBuffer.readItemStack();
        int energy = packetBuffer.readInt();
        int time = packetBuffer.readInt();
        CrystalRecipe recipe = new CrystalRecipe(resourceLocation,  item_output, fluid_input, energy, time);
        return recipe;
    }

    @Override
    public void write(PacketBuffer packetBuffer, CrystalRecipe crystalRecipe) {
        crystalRecipe.getInputFluid().write(packetBuffer);
        packetBuffer.writeItemStack(crystalRecipe.getItemOutput());

        packetBuffer.writeInt(crystalRecipe.getTotalProcessEnergy());
        packetBuffer.writeInt(crystalRecipe.getTotalProcessTime());
    }
}
