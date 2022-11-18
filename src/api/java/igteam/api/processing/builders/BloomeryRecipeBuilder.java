package igteam.api.processing.builders;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import igteam.api.processing.Serializers;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BloomeryRecipeBuilder extends IEFinishedRecipe<BloomeryRecipeBuilder> {

    public BloomeryRecipeBuilder() {
        super(Serializers.BLOOMERY_SERIALIZER.get());
    }

    public static BloomeryRecipeBuilder builder(Item result)
    {
        return new BloomeryRecipeBuilder().addResult(result);
    }

    public static BloomeryRecipeBuilder builder(ItemStack result, IngredientWithSize input){
        BloomeryRecipeBuilder builder = new BloomeryRecipeBuilder();
        builder.addResult(result);
        builder.addIngredient("item_input", input);

        return builder;
    }

    public static BloomeryRecipeBuilder builder(ItemStack result, ItemStack input){
        BloomeryRecipeBuilder builder = new BloomeryRecipeBuilder();
        builder.addResult(result);
        builder.addIngredient("item_input", IngredientWithSize.of(input));

        return builder;
    }

    public static BloomeryRecipeBuilder builder(TagKey<Item> result, int count)
    {
        return new BloomeryRecipeBuilder().addResult(new IngredientWithSize(result, count));
    }

    public static BloomeryRecipeBuilder builder(IngredientWithSize result)
    {
        return new BloomeryRecipeBuilder().addResult(result);
    }
}
