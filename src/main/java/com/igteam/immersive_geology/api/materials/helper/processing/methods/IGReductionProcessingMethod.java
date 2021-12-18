package com.igteam.immersive_geology.api.materials.helper.processing.methods;

import blusunrize.immersiveengineering.common.items.IEItems;
import com.igteam.immersive_geology.api.materials.fluid.FluidEnum;
import com.igteam.immersive_geology.api.materials.fluid.SlurryEnum;
import com.igteam.immersive_geology.api.materials.helper.processing.IGProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.ProcessingMethod;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;


public class IGReductionProcessingMethod extends IGProcessingMethod {

    private final int energyCost;
    private final int processingTime;

    private ItemStack outputItem, inputItem, slagItem;


    public IGReductionProcessingMethod(int energyCost, int processingTime) {
        this.energyCost = energyCost;
        this.processingTime = processingTime;

    }


    @Override
    public ProcessingMethod getKey() {
        return ProcessingMethod.BLASTING;
    }

    @Override
    public int getEnergyCost() {
        return energyCost;
    }

    @Override
    public int getProcessingTime() {
        return processingTime;
    }

    public IGReductionProcessingMethod addItemOutput(ItemStack outputItem){
        this.outputItem = outputItem.copy();
        return this;
    }

    public IGReductionProcessingMethod addItemInput(ItemStack inputItem){
        this.inputItem = inputItem.copy();
        return this;
    }

    public IGReductionProcessingMethod addItemSlag(ItemStack slagItem)
    {
       this.slagItem = slagItem;
        return this;
    }

    public ItemStack getOutputItem() {
        return outputItem;
    }

    public ItemStack getInputItem() {
        return inputItem;
    }

    public ItemStack getSlagItem() {
        return slagItem;
    }

}