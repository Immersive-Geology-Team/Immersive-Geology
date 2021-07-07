package com.igteam.immersive_geology.api.crafting.recipes;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.igteam.immersive_geology.api.crafting.IGMultiblockRecipe;
import com.igteam.immersive_geology.common.crafting.Serializers;
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

public class VatRecipe extends IGMultiblockRecipe
{
    public static final IRecipeType<VatRecipe> TYPE = IRecipeType.register(IGLib.MODID + ":chemicalvat");
    public static Map<ResourceLocation, VatRecipe> recipes = new HashMap<>();


    /** May return null! */
    public static VatRecipe findRecipe(ItemStack itemInput, FluidStack fluidInput)
    {
        if(!recipes.isEmpty()){
            for(VatRecipe r:recipes.values()){
                if(r.itemInput != null && r.fluidInput != null && r.fluidInput.testIgnoringAmount(fluidInput) && r.itemInput.equals(itemInput)){
                    return r;
                }
            }
        }
        return null;
    }

    public static VatRecipe loadFromNBT(CompoundNBT nbt){
        FluidStack fluidInput = FluidStack.loadFluidStackFromNBT(nbt.getCompound("fluid_input"));
        ItemStack itemInput =  ItemStack.read(nbt.getCompound("item_input"));
        return findRecipe(itemInput, fluidInput);
    }

    protected final FluidTagInput fluidInput;
    protected final FluidStack fluidOutput;
    protected final ItemStack itemInput, itemOutput;


    protected VatRecipe(ResourceLocation id, FluidStack fluidOutput, ItemStack itemOutput, FluidTagInput fluidInput, ItemStack itemInput, int energy, int time) {
        super(ItemStack.EMPTY, TYPE, id);
        this.fluidInput = fluidInput;
        this.fluidOutput = fluidOutput;
        this.itemInput = itemInput;
        this.itemOutput = itemOutput;

        this.fluidInputList = Collections.singletonList(fluidInput);
        this.fluidOutputList = Arrays.asList(this.fluidOutput);
        this.outputList = NonNullList.from(ItemStack.EMPTY, itemOutput);

        timeAndEnergy(time, energy);
        modifyTimeAndEnergy(IGConfigurationHandler.Server.MULTIBLOCK.chemicalVat_energyModifier::get, IGConfigurationHandler.Server.MULTIBLOCK.chemicalVat_timeModifier::get);
    }

    @Override
    protected IERecipeSerializer getIESerializer() {
        return Serializers.CHEMICAL_VAT_SERIALIZER.get();
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
