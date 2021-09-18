package com.igteam.immersive_geology.api.crafting.recipes.recipe;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.igteam.immersive_geology.api.crafting.IGMultiblockRecipe;
import com.igteam.immersive_geology.core.config.IGConfigurationHandler;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;

public class ElectrolizerRecipe extends IGMultiblockRecipe
{
    public static final IRecipeType<ElectrolizerRecipe> TYPE = IRecipeType.register(IGLib.MODID + ":electrolizer");
    public static Map<ResourceLocation, ElectrolizerRecipe> recipes = new HashMap<>();


    /** May return null! */
    public static ElectrolizerRecipe findRecipe(FluidStack fluidInput)
    {
        if(!recipes.isEmpty()){
            for(ElectrolizerRecipe r:recipes.values()){
                if(r.fluidInput != null && r.fluidInput.testIgnoringAmount(fluidInput)){
                    return r;
                }
            }
        }
        return null;
    }

    public static ElectrolizerRecipe loadFromNBT(CompoundNBT nbt){
        FluidStack fluidInput = FluidStack.loadFluidStackFromNBT(nbt.getCompound("fluid_input"));
        return findRecipe(fluidInput);
    }

    protected final FluidTagInput fluidInput;
    protected final FluidStack fluidOutput;
    protected final ItemStack itemOutput;


    protected ElectrolizerRecipe(ResourceLocation id, FluidStack fluidOutput, ItemStack itemOutput, FluidTagInput fluidInput, int energy, int time) {
        super(ItemStack.EMPTY, TYPE, id);
        this.fluidInput = fluidInput;
        this.fluidOutput = fluidOutput;
        this.itemOutput = itemOutput;

        this.fluidInputList = Collections.singletonList(fluidInput);
        this.fluidOutputList = Arrays.asList(this.fluidOutput);
        this.outputList = NonNullList.from(ItemStack.EMPTY, itemOutput);

        timeAndEnergy(time, energy);
        modifyTimeAndEnergy(IGConfigurationHandler.Server.MULTIBLOCK.electrolizer_energyModifier::get, IGConfigurationHandler.Server.MULTIBLOCK.electrolizer_timeModifier::get);
    }

    @Override
    protected IERecipeSerializer getIESerializer() {
        return null;//Serializers.CHEMICAL_VAT_SERIALIZER.get();
    }

    @Override
    public int getMultipleProcessTicks() {
        return 0;
    }

    @Override
    public List<FluidStack> getActualFluidOutputs(TileEntity tile) {
        NonNullList<FluidStack> output = NonNullList.create();
        output.add(fluidOutput);
        return output;
    }

    public FluidTagInput getInputFluid(){
        return this.fluidInput;
    }

}
