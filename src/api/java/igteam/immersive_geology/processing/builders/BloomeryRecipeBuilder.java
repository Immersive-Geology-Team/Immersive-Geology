package igteam.immersive_geology.processing.builders;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import igteam.immersive_geology.processing.Serializers;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;

public class BloomeryRecipeBuilder extends IEFinishedRecipe<BloomeryRecipeBuilder> {

    public BloomeryRecipeBuilder() {
        super(Serializers.BLOOMERY_SERIALIZER.get());
    }

    public static BloomeryRecipeBuilder builder(Item result)
    {
        return new BloomeryRecipeBuilder().addResult(result);
    }

    public static BloomeryRecipeBuilder builder(ItemStack result)
    {
        return new BloomeryRecipeBuilder().addResult(result);
    }

    public static BloomeryRecipeBuilder builder(ITag<Item> result, int count)
    {
        return new BloomeryRecipeBuilder().addResult(new IngredientWithSize(result, count));
    }

    public static BloomeryRecipeBuilder builder(IngredientWithSize result)
    {
        return new BloomeryRecipeBuilder().addResult(result);
    }

    public BloomeryRecipeBuilder addItemInput(ItemStack input){
        if(!input.isEmpty()) {
            this.addIngredient("item_input", IngredientWithSize.of(input));
        }
        return this;
    }
}
