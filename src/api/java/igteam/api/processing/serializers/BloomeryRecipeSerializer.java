package igteam.api.processing.serializers;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.gson.JsonObject;
import igteam.api.main.IGMultiblockProvider;
import igteam.api.processing.recipe.BloomeryRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class BloomeryRecipeSerializer extends IERecipeSerializer<BloomeryRecipe>
{

    @Override
    public ItemStack getIcon() {
        return new ItemStack(IGMultiblockProvider.bloomery);
    }

    @Override
    public BloomeryRecipe readFromJson(ResourceLocation recipeId, JsonObject json) {
        ItemStack output = readOutput(json.get("result"));
        IngredientWithSize input = IngredientWithSize.deserialize(JSONUtils.getJsonObject(json, "item_input"));
        Integer time = JSONUtils.getInt(json, "time");
        BloomeryRecipe recipe = new BloomeryRecipe(recipeId, output, input, time);
        return recipe;
    }

    @Override
    public BloomeryRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        ItemStack output = buffer.readItemStack();
        IngredientWithSize input = IngredientWithSize.read(buffer);
        Integer time = buffer.readInt();
        BloomeryRecipe recipe = new BloomeryRecipe(recipeId, output, input, time);
        return recipe;
    }

    @Override
    public void write(PacketBuffer buffer, BloomeryRecipe recipe) {
        buffer.writeItemStack(recipe.output);
        recipe.input.write(buffer);
        buffer.writeInt(recipe.getTime());
    }
}
