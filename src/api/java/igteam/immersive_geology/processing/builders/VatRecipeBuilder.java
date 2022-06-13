package igteam.immersive_geology.processing.builders;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.processing.Serializers;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;

import static java.util.Arrays.asList;

public class VatRecipeBuilder extends IEFinishedRecipe<VatRecipeBuilder> {

    public VatRecipeBuilder(){
        super(Serializers.CHEMICAL_VAT_SERIALIZER.get());
    }

    public static VatRecipeBuilder builder(ItemStack result, FluidStack fluid_result)
    {
        VatRecipeBuilder builder = new VatRecipeBuilder().addFluid("fluid_result", fluid_result);

        if(!result.isEmpty()){
            builder.addResult(result);
        }
        return builder;
    }

    public static VatRecipeBuilder builder(FluidStack fluid_result)
    {
        return new VatRecipeBuilder().addFluid("fluid_result", fluid_result);
    }

    public VatRecipeBuilder addItemInput(ItemStack input){
        if(!input.isEmpty()) {
            this.addIngredient("item_input", IngredientWithSize.of(input));
        }
        return this;
    }

    public VatRecipeBuilder addFluidInputs(FluidTagInput input_1, FluidTagInput input_2){
        this.addFluidTag("fluid_input1", input_1);
        if(input_2 != null)
        this.addFluidTag("fluid_input2", input_2);
        return this;
    }
    public static VatRecipeBuilder builder(FluidStack fluid_result, ItemStack result, FluidTagInput input_1, FluidTagInput input_2, IngredientWithSize input, int energy, int time)
    {
        VatRecipeBuilder builder = new VatRecipeBuilder().addFluid("fluid_result", fluid_result);
        if(!result.isEmpty()){
            builder.addResult(result);
        }

        if(input != null) {
            builder.addIngredient("item_input", input);
        }

        builder.addFluidTag("fluid_input1", input_1);
        builder.addFluidTag("fluid_input2", input_2);
        builder.setTime(time);
        builder.setEnergy(energy);

        return builder;
    }
}
