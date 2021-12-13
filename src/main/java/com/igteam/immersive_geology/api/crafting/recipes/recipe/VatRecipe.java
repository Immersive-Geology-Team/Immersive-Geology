package com.igteam.immersive_geology.api.crafting.recipes.recipe;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.crafting.IGMultiblockRecipe;
import com.igteam.immersive_geology.common.crafting.Serializers;
import com.igteam.immersive_geology.core.config.IGConfigurationHandler;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class VatRecipe extends IGMultiblockRecipe
{
    public static final IRecipeType<VatRecipe> TYPE = IRecipeType.register(IGLib.MODID + ":chemicalvat");
    public static Map<ResourceLocation, VatRecipe> recipes = new HashMap<>();

    public static Logger log = ImmersiveGeology.getNewLogger();

    /** May return null! */
    public static VatRecipe findRecipe(ItemStack itemInput, FluidStack fluid1, FluidStack fluid2)
    {
        if(!recipes.isEmpty()){
            for(VatRecipe r:recipes.values()){
                if(r.itemInput != null && r.fluidInput1 != null) {
                    if(r.itemInput.test(itemInput)) {
                        if(fluid2 != null && (!fluid2.isEmpty()) && r.fluidInput2 != null) {
                            if (r.fluidInput1.test(fluid1) && r.fluidInput2.test(fluid2)) {
                                return r;
                            }
                        } else {
                            if (r.fluidInput1.test(fluid1)) {
                                return r;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    public static VatRecipe loadFromNBT(CompoundNBT nbt){
        FluidStack fluidInput1 = FluidStack.loadFluidStackFromNBT(nbt.getCompound("fluid_input1"));
        FluidStack fluidInput2 = FluidStack.loadFluidStackFromNBT(nbt.getCompound("fluid_input2"));
        ItemStack itemInput =  ItemStack.read(nbt.getCompound("item_input"));
        return findRecipe(itemInput, fluidInput1, fluidInput2);
    }

    protected final FluidTagInput fluidInput1;
    protected final FluidTagInput fluidInput2;

    protected final FluidStack fluidOutput;
    protected final IngredientWithSize itemInput;
    protected final ItemStack itemOutput;

    public VatRecipe(ResourceLocation id, FluidStack fluidOutput, ItemStack itemOutput, FluidTagInput fluidInput1, FluidTagInput fluidInput2, IngredientWithSize itemInput, int energy, int time) {
        super(itemOutput, TYPE, id);
        this.fluidInput1 = fluidInput1;
        this.fluidInput2 = fluidInput2;
        this.fluidOutput = fluidOutput;
        this.itemInput = itemInput;
        this.itemOutput = itemOutput;

        this.fluidInputList = new ArrayList<>();

        if(fluidInput1 != null)
        fluidInputList.add(fluidInput1);

        if(fluidInput2 != null)
        fluidInputList.add(fluidInput2);

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
        return 1;
    }

    @Override
    public List<FluidStack> getActualFluidOutputs(TileEntity tile) {
        NonNullList<FluidStack> output = NonNullList.create();
        output.add(fluidOutput);
        return output;
    }

    @Override
    public List<FluidStack> getFluidOutputs() {
        return fluidOutputList;
    }

    @Override
    public List<IngredientWithSize> getItemInputs() {
        return Arrays.asList(itemInput);
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.itemOutput;
    }

    @Override
    public int getTotalProcessTime() {
        return this.totalProcessTime.get();
    }

    @Override
    public int getTotalProcessEnergy() {
        return this.totalProcessEnergy.get();
    }

    @Override
    public NonNullList<ItemStack> getItemOutputs() {
        return outputList;
    }

    public List<FluidTagInput> getInputFluids(){
        return fluidInputList;
    }
}
