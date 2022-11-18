package igteam.api.processing.builders;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import igteam.api.processing.Serializers;
import net.minecraft.item.ItemStack;

public class CuttingRecipeBuilder extends IEFinishedRecipe<CuttingRecipeBuilder> {

    public CuttingRecipeBuilder(){
        super(Serializers.HYDROJET_CUTTER_SERIALIZER.get());
    }

    public static CuttingRecipeBuilder builder(ItemStack result)
    {
        CuttingRecipeBuilder builder = new CuttingRecipeBuilder();

        if(!result.isEmpty()){
            builder.addResult(result);
        }
        return builder;
    }

    public CuttingRecipeBuilder addItemInput(ItemStack input){
        if(!input.isEmpty()) {
            this.addIngredient("input", IngredientWithSize.of(input));
        }
        return this;
    }

    public CuttingRecipeBuilder addFluidInputs(FluidTagInput input_1){
        this.addFluidTag("fluid", input_1);
        return this;
    }
    public static CuttingRecipeBuilder builder(ItemStack result, FluidTagInput input_1, IngredientWithSize input, int energy, int time)
    {
        CuttingRecipeBuilder builder = new CuttingRecipeBuilder();
        if(!result.isEmpty()){
            builder.addResult(result);
        }

        if(input != null) {
            builder.addIngredient("input", input);
        }

        builder.addFluidTag("fluid", input_1);
        builder.setTime(time);
        builder.setEnergy(energy);

        return builder;
    }
}
