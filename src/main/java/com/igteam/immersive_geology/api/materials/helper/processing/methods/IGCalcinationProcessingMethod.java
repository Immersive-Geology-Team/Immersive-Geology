package com.igteam.immersive_geology.api.materials.helper.processing.methods;

import com.igteam.immersive_geology.api.materials.helper.processing.IGProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.ProcessingMethod;
import net.minecraft.item.ItemStack;


public class IGCalcinationProcessingMethod extends IGProcessingMethod {

    private final int energyCost;
    private final int processingTime;

    private ItemStack outputItem, inputItem;


    public IGCalcinationProcessingMethod(int energyCost, int processingTime) {
        this.energyCost = energyCost;
        this.processingTime = processingTime;

    }


    @Override
    public ProcessingMethod getKey() {
        return ProcessingMethod.CALCINATION;
    }

    @Override
    public int getEnergyCost() {
        return energyCost;
    }

    @Override
    public int getProcessingTime() {
        return processingTime;
    }

    public IGCalcinationProcessingMethod addItemOutput(ItemStack outputItem){
        this.outputItem = outputItem.copy();
        return this;
    }

    public IGCalcinationProcessingMethod addItemInput(ItemStack inputItem){
        this.inputItem = inputItem.copy();
        return this;
    }


    public ItemStack getOutputItem() {
        return outputItem;
    }

    public ItemStack getInputItem() {
        return inputItem;
    }


}