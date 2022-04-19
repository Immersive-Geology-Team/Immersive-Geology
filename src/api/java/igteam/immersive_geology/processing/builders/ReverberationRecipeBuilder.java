package igteam.immersive_geology.processing.builders;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import igteam.immersive_geology.processing.Serializers;
import net.minecraft.item.ItemStack;

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
