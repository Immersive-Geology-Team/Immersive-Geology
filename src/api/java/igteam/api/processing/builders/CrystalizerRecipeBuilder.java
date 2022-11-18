package igteam.api.processing.builders;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import igteam.api.processing.Serializers;

import javax.annotation.Nonnull;

public class CrystalizerRecipeBuilder extends IEFinishedRecipe<CrystalizerRecipeBuilder> {
    private CrystalizerRecipeBuilder()    {
         super(Serializers.CRYSTALIZER_SERIALIZER.get());
    }

    public static CrystalizerRecipeBuilder builder()
    {
        CrystalizerRecipeBuilder builder = new CrystalizerRecipeBuilder();
        return builder;
    }

    public CrystalizerRecipeBuilder addFluidInput(@Nonnull FluidTagInput input){
        this.addFluidTag("fluid_input", input);
        return this;
    }

}
