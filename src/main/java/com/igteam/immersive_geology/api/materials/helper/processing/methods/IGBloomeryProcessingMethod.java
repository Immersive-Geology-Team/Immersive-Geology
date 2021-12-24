package com.igteam.immersive_geology.api.materials.helper.processing.methods;

import com.igteam.immersive_geology.api.materials.helper.processing.IGProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.ProcessingMethod;
import net.minecraft.item.ItemStack;

public class IGBloomeryProcessingMethod extends IGProcessingMethod {

    private final int energyCost;
    private final int processingTime;

    private ItemStack outputItem, inputItem;

    public IGBloomeryProcessingMethod(int fuelCost, int processingMult) {
        this.energyCost = fuelCost;
        this.processingTime = processingMult;
    }

    @Override
    public ProcessingMethod getKey() {
        return ProcessingMethod.BLOOMERY;
    }

    @Override
    public int getEnergyCost() {
        return energyCost;
    }

    @Override
    public int getProcessingTime() {
        return processingTime;
    }

    public IGBloomeryProcessingMethod addItemOutput(ItemStack outputItem){
        this.outputItem = outputItem.copy();
        return this;
    }

    public IGBloomeryProcessingMethod addItemInput(ItemStack inputItem){
        this.inputItem = inputItem.copy();
        return this;
    }

    public ItemStack getOutputItem() {
        return outputItem.copy();
    }

    public ItemStack getInputItem() {
        return inputItem.copy();
    }
}
