package igteam.api.processing.serializers;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.gson.JsonObject;
import igteam.api.main.IGMultiblockProvider;
import igteam.api.processing.recipe.HydrojetRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

public class HydrojetRecipeSerializer extends IERecipeSerializer<HydrojetRecipe> {
    //Class is a used for HydroJetRecipe

    @Override
    public ItemStack getIcon() {
        return new ItemStack(IGMultiblockProvider.hydrojet_cutter);
    }

    @Override
    public HydrojetRecipe readFromJson(ResourceLocation recipeId, JsonObject json) {

        ItemStack output = readOutput(json.get("result"));
        IngredientWithSize input = IngredientWithSize.of(ItemStack.EMPTY);

        if(json.get("input") != null) {
            JsonObject inputArray = JSONUtils.getJsonObject(json, "input");
            input = IngredientWithSize.deserialize(inputArray);
        }

        FluidTagInput fluid = null;
        if(json.has("fluid")) {
            fluid = FluidTagInput.deserialize(JSONUtils.getJsonObject(json, "fluid"));
        }

        int energy = JSONUtils.getInt(json, "energy");
        int time = JSONUtils.getInt(json, "time");

        return new HydrojetRecipe(recipeId, output, fluid, input, energy, time);
    }

    @Nullable
    @Override
    public HydrojetRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        boolean hasItemInput = buffer.readBoolean();
        IngredientWithSize item_input = IngredientWithSize.of(ItemStack.EMPTY);

        if(hasItemInput) item_input = IngredientWithSize.read(buffer);

        FluidTagInput fluid_input1 = FluidTagInput.read(buffer);

        ItemStack item_output = buffer.readItemStack();

        int energy = buffer.readInt();
        int time = buffer.readInt();

        HydrojetRecipe recipe = new HydrojetRecipe(recipeId, item_output, fluid_input1, item_input,energy,time);
        return recipe;
    }

    @Override
    public void write(PacketBuffer buffer, HydrojetRecipe recipe) {
        boolean hasInputItem = recipe.getItemInput() != null;
        buffer.writeBoolean(hasInputItem);
        if(hasInputItem) {
            recipe.getItemInput().write(buffer);
        }

        List<FluidTagInput> inputFluids = recipe.getFluidInputs();
        inputFluids.get(0).write(buffer);

        buffer.writeItemStack(recipe.getItemOutput());

        buffer.writeInt(recipe.getTotalProcessEnergy());
        buffer.writeInt(recipe.getTotalProcessTime());
    }
}
