package com.igteam.immersive_geology.api.materials.helper.processing.methods;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersive_geology.api.materials.helper.processing.IGProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.ProcessingMethod;
import net.minecraft.item.ItemStack;


public class IGArcFurnaceProcessingMethod extends IGProcessingMethod {

    private final int energyCost;
    private final int processingTime;
    private final IngredientWithSize[] additives;

    private ItemStack outputItem, inputItem, slagItem;


    public IGArcFurnaceProcessingMethod(int energyCost, int processingTime, IngredientWithSize... additives) {
        this.energyCost = energyCost;
        this.processingTime = processingTime;
        this.additives = additives;

    }

    public IngredientWithSize[] getAdditives() { return this.additives;}

    @Override
    public ProcessingMethod getKey() {
        return ProcessingMethod.MOLTEN_REDUCTION;
    }

    @Override
    public int getEnergyCost() {
        return energyCost;
    }

    @Override
    public int getProcessingTime() {
        return processingTime;
    }

    public IGArcFurnaceProcessingMethod addItemOutput(ItemStack outputItem){
        this.outputItem = outputItem.copy();
        return this;
    }

    public IGArcFurnaceProcessingMethod addItemInput(ItemStack inputItem){
        this.inputItem = inputItem.copy();
        return this;
    }

    public IGArcFurnaceProcessingMethod addItemSlag(ItemStack slagItem)
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