package com.igteam.immersive_geology.api.materials.helper.processing.methods;

import com.igteam.immersive_geology.api.materials.helper.processing.IGProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.ProcessingMethod;
import net.minecraft.item.ItemStack;

public class IGSeparationProcessingMethod extends IGProcessingMethod {

    private final int processingTime;


    private ItemStack inputItem, outputItem, wasteItem;

    public IGSeparationProcessingMethod(int processingTime){
        this.processingTime = processingTime;
    }

    public IGSeparationProcessingMethod addItemWaste (ItemStack wasteItem){
        this.wasteItem = wasteItem;
        return this;
    }

    public IGSeparationProcessingMethod addItemInput(ItemStack input){
        this.inputItem = input;
        return this;
    }

    public IGSeparationProcessingMethod addItemOutput(ItemStack output){
        this.outputItem = output;
        return this;
    }

    @Override
    public ProcessingMethod getKey() {
        return ProcessingMethod.SEPARATING;
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
    public ItemStack getWasteItem(){
        return this.wasteItem;
    }

}
