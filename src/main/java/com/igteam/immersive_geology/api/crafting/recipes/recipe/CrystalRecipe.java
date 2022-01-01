package com.igteam.immersive_geology.api.crafting.recipes.recipe;

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

public class CrystalRecipe extends IGMultiblockRecipe
{
    public static final IRecipeType<CrystalRecipe> TYPE = IRecipeType.register(IGLib.MODID + ":crystalizer");
    public static Map<ResourceLocation, CrystalRecipe> recipes = new HashMap<>();


    /** May return null! */
    public static CrystalRecipe findRecipe(FluidStack fluidInput)
    {
        if(!recipes.isEmpty()){
            for(CrystalRecipe r:recipes.values()){
                if(r.fluidInput != null && r.fluidInput.testIgnoringAmount(fluidInput)){
                    return r;
                }
            }
        }
        return null;
    }

    public static CrystalRecipe loadFromNBT(CompoundNBT nbt){
        FluidStack fluidInput = FluidStack.loadFluidStackFromNBT(nbt.getCompound("fluid_input"));
        return findRecipe(fluidInput);
    }

    protected final FluidTagInput fluidInput;
    protected final ItemStack itemOutput;

    public CrystalRecipe(ResourceLocation id, ItemStack itemOutput, FluidTagInput fluidInput, int energy, int time) {
        super(ItemStack.EMPTY, TYPE, id);
        this.fluidInput = fluidInput;
        this.itemOutput = itemOutput;

        this.fluidInputList = Collections.singletonList(fluidInput);
        this.outputList = NonNullList.from(ItemStack.EMPTY, itemOutput);
        timeAndEnergy(time, energy);
        modifyTimeAndEnergy(IGConfigurationHandler.Server.MULTIBLOCK.electrolizer_energyModifier::get, IGConfigurationHandler.Server.MULTIBLOCK.electrolizer_timeModifier::get);
    }

    @Override
    protected IERecipeSerializer getIESerializer() {
        return Serializers.CRYSTALIZER_SERIALIZER.get();
    }

    @Override
    public boolean shouldCheckItemAvailability() {return false;}

    public ItemStack getItemOutput() {
        return itemOutput;
    }

    @Override
    public NonNullList<ItemStack> getItemOutputs() {
        return outputList;
    }

    @Override
    public int getMultipleProcessTicks() {
        return 0;
    }

    public FluidTagInput getInputFluid(){
        return this.fluidInput;
    }

}
