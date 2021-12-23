package com.igteam.immersive_geology.api.materials.helper.processing.methods;

import com.igteam.immersive_geology.api.materials.helper.processing.IGProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.ProcessingMethod;
import net.minecraft.item.ItemStack;

public class IGRoastingProcessingMethod extends IGProcessingMethod {

    private final int processingTime;
    private final float wasteMultiplier;

    private ItemStack inputItem, outputItem;

    public IGRoastingProcessingMethod(int processingTime, float wasteOutputMult){
        this.processingTime = processingTime;
        this.wasteMultiplier = wasteOutputMult;
    }

    public float getWasteMultiplier(){
        return this.wasteMultiplier;
    }

    public IGRoastingProcessingMethod addItemInput(ItemStack input){
        this.inputItem = input;
        return this;
    }

    public IGRoastingProcessingMethod addItemOutput(ItemStack output){
        this.outputItem = output;
        return this;
    }

    @Override
    public ProcessingMethod getKey() {
        return ProcessingMethod.ROASTER;
    }

    @Override
    public int getEnergyCost() {
        return 0;
    }

    @Override
    public int getProcessingTime() {
        return processingTime;
    }

    public ItemStack getInputItem() {
        return inputItem;
    }

    public ItemStack getOutputItem() {
        return outputItem;
    }
}
