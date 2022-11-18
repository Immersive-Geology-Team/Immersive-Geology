package igteam.api.processing.builders;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import igteam.api.processing.Serializers;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class SeparatorRecipeBuilder extends IEFinishedRecipe<SeparatorRecipeBuilder> {

    JsonArray secondaryArray = new JsonArray();

    public SeparatorRecipeBuilder(){
        super(Serializers.GRAVITY_SEPARATOR_SERIALIZER.get());
        addWriter(jsonObject -> jsonObject.add("secondaries", secondaryArray));
    }


    public static SeparatorRecipeBuilder builder(Item result)
    {
        return new SeparatorRecipeBuilder().addResult(result);
    }

    public static SeparatorRecipeBuilder builder(ItemStack result)
    {
        return new SeparatorRecipeBuilder().addResult(result);
    }

    public static SeparatorRecipeBuilder builder(TagKey<Item> result, int count)
    {
        return new SeparatorRecipeBuilder().addResult(new IngredientWithSize(result, count));
    }

    public static SeparatorRecipeBuilder builder(IngredientWithSize result)
    {
        return new SeparatorRecipeBuilder().addResult(result);
    }

    public SeparatorRecipeBuilder addSecondary(ItemLike itemProvider, float chance)
    {
        return this.addSecondary(new ItemStack(itemProvider), chance);
    }

    public SeparatorRecipeBuilder addWaste(ItemStack itemStack)
    {
        return addItem("waste", itemStack);
    }

    public SeparatorRecipeBuilder addWaste(ItemLike provider)
    {
        return addItem("waste", new ItemStack(provider));
    }

    public SeparatorRecipeBuilder addSecondary(ItemStack itemStack, float chance)
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("chance", chance);
        jsonObject.add("output", serializeItemStack(itemStack));
        secondaryArray.add(jsonObject);
        return this;
    }

    public SeparatorRecipeBuilder addSecondary(TagKey<Item> tag, float chance)
    {
        return addSecondary(new IngredientWithSize(tag), chance);
    }

    public SeparatorRecipeBuilder addSecondary(IngredientWithSize ingredient, float chance, ICondition... conditions)
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("chance", chance);
        jsonObject.add("output", ingredient.serialize());
        if(conditions.length > 0)
        {
            JsonArray conditionArray = new JsonArray();
            for(ICondition condition : conditions)
                conditionArray.add(CraftingHelper.serialize(condition));
            jsonObject.add("conditions", conditionArray);
        }
        secondaryArray.add(jsonObject);
        return this;
    }
}
