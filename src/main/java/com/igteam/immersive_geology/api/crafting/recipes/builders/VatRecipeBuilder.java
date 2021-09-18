package com.igteam.immersive_geology.api.crafting.recipes.builders;

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
        return new VatRecipeBuilder().addResult(result).addFluid("fluid_result", fluid_result);
    }

    public static VatRecipeBuilder builder(FluidStack fluid_result)
    {
        return new VatRecipeBuilder().addFluid("fluid_result", fluid_result);
    }

    public VatRecipeBuilder addItemInput(IngredientWithSize input){
        this.addIngredient("item_input", input);
        return this;
    }

    public VatRecipeBuilder addFluidInputs(FluidStack input_1, FluidStack input_2){
        this.addFluid("fluid_input1", input_1);
        this.addFluid("fluid_input2", input_2);
        return this;
    }
}
