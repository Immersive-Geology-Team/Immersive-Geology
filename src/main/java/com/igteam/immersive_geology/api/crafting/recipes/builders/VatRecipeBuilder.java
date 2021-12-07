package com.igteam.immersive_geology.api.crafting.recipes.builders;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import com.igteam.immersive_geology.common.crafting.Serializers;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

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

    public VatRecipeBuilder addItemInput(IngredientWithSize input){
        this.addIngredient("item_input", input);
        return this;
    }

    public VatRecipeBuilder addFluidInputs(FluidTagInput input_1, FluidTagInput input_2){
        this.addFluidTag("fluid_input1", input_1);
        this.addFluidTag("fluid_input2", input_2);
        return this;
    }
}
