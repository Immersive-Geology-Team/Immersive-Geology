package com.igteam.immersive_geology.api.materials.helper.processing.methods;

import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.helper.processing.IGProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.ProcessingMethod;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class IGAcidProcessingMethod extends IGProcessingMethod {

    Pair<MaterialUseType, Material> resultingFluid;
    ItemStack input;
    Pair<FluidStack, FluidStack> fluidInput;
    ItemStack itemOutput;
    int fluidOutAmount, energyCost, ticksRequired;

    //Used for Vat Recipes
    public IGAcidProcessingMethod(ItemStack input, Pair<FluidStack, FluidStack> inputFluids, ItemStack itemOutput,  Pair<MaterialUseType, Material> fluidOutput, int resultingAmount, int energyCost, int ticksTaken){
        this.resultingFluid = fluidOutput;
        this.input = input;
        this.fluidInput = inputFluids;
        this.itemOutput = itemOutput == ItemStack.EMPTY ? new ItemStack(Blocks.COMMAND_BLOCK) : itemOutput;
        this.ticksRequired = ticksTaken;
        this.fluidOutAmount = resultingAmount;
        this.energyCost = energyCost;
    }

    @Override
    public ProcessingMethod getKey() {
        return ProcessingMethod.ACID;
    }

    @Override
    public Pair<MaterialUseType, Material> outputFluidData() {
        return resultingFluid;
    }

    @Override
    public int getEnergyCost() {
        return energyCost;
    }

    @Override
    public int getProcessingTime() {
        return ticksRequired;
    }

    @Override
    public FluidStack getSecondaryFluid() {
        return fluidInput.getSecond();
    }

    @Override
    public FluidStack getPrimaryFluid() {
        return fluidInput.getFirst();
    }

    @Override
    public ItemStack getItemInput() {
        return input;
    }

    @Override
    public ItemStack getItemOutput() {
        return itemOutput;
    }

    @Override
    public FluidStack getFluidResult() {
        if(resultingFluid.getFirst().equals(MaterialUseType.SLURRY)) {
            return new FluidStack(IGRegistrationHolder.getSlurryByMaterials(resultingFluid.getSecond(), resultingFluid.getSecond().getFluidsForSlurries()[0], false), fluidOutAmount);
        } else {
            return new FluidStack(IGRegistrationHolder.getFluidByMaterial(resultingFluid.getSecond(), false), fluidOutAmount);
        }
    }
}
