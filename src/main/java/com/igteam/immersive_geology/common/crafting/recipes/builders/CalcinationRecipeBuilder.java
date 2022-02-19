package com.igteam.immersive_geology.common.crafting.recipes.builders;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import com.igteam.immersive_geology.common.crafting.Serializers;
import net.minecraft.item.ItemStack;

public class CalcinationRecipeBuilder extends IEFinishedRecipe<CalcinationRecipeBuilder> {
    private CalcinationRecipeBuilder()    {
         super(Serializers.ROTARY_KILN_SERIALIZER.get());
    }

    public static CalcinationRecipeBuilder builder(ItemStack result)
    {
        CalcinationRecipeBuilder builder = new CalcinationRecipeBuilder();
        if(!result.isEmpty()){
            builder.addResult(result);
        }
        return builder;
    }

    public CalcinationRecipeBuilder addItemInput(ItemStack input){
        if(!input.isEmpty()) {
            this.addIngredient("item_input", IngredientWithSize.of(input));
        }
        return this;
    }
}
