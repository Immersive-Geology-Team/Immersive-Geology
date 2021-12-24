package com.igteam.immersive_geology.api.crafting.recipes.builders;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import com.igteam.immersive_geology.api.crafting.recipes.recipe.ReverberationRecipe;
import com.igteam.immersive_geology.common.crafting.Serializers;
import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class ReverberationRecipeBuilder extends IEFinishedRecipe<ReverberationRecipeBuilder> {

    private ReverberationRecipeBuilder() {
        super(Serializers.REVERBERATION_SERIALIZER.get());
    }

    public static ReverberationRecipeBuilder builder(ItemStack result){
        ReverberationRecipeBuilder builder = new ReverberationRecipeBuilder();
        if(!result.isEmpty()){
            builder.addResult(result);
        }
        return builder;
    }

    public ReverberationRecipeBuilder setWasteMult(float mult){
        return this.addWriter((jsonObject) -> {
            jsonObject.addProperty("wasteMultiplier", mult);
        });
    }

    public ReverberationRecipeBuilder addItemInput(ItemStack input){
        if(!input.isEmpty()) {
            this.addIngredient("item_input", IngredientWithSize.of(input));
        }
        return this;
    }
}
