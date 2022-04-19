package igteam.immersive_geology.processing.builders;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import igteam.immersive_geology.processing.Serializers;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class CrystalizerRecipeBuilder extends IEFinishedRecipe<CrystalizerRecipeBuilder> {
    private CrystalizerRecipeBuilder()    {
         super(Serializers.CRYSTALIZER_SERIALIZER.get());
    }

    public static CrystalizerRecipeBuilder builder(ItemStack result)
    {
        CrystalizerRecipeBuilder builder = new CrystalizerRecipeBuilder();
        if(!result.isEmpty()){
            builder.addResult(result);
        }
        return builder;
    }

    public CrystalizerRecipeBuilder addFluidInput(@Nonnull FluidTagInput input){
        this.addFluidTag("fluid_input", input);
        return this;
    }

}
