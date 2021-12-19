package com.igteam.immersive_geology.api.materials.helper.processing.methods;

import com.igteam.immersive_geology.api.materials.fluid.SlurryEnum;
import com.igteam.immersive_geology.api.materials.helper.processing.IGProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.ProcessingMethod;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;


public class IGCrystalizerProcessingMethod extends IGProcessingMethod {

    private final int energyCost;
    private final int processingTime;

    private ItemStack outputItem;
    private FluidStack inputFluid;

    public IGCrystalizerProcessingMethod(int energyCost, int processingTime) {
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

    public IGCrystalizerProcessingMethod addItemOutput(ItemStack outputItem){
        this.outputItem = outputItem.copy();
        return this;
    }

    public IGCrystalizerProcessingMethod addFluidInput(SlurryEnum slurry, int entry, int fluidAmount){
        this.inputFluid = new FluidStack(IGRegistrationHolder.getSlurryByMaterials(slurry.getEntries().get(entry).getSoluteMaterial(), slurry.getEntries().get(entry).getBaseFluidMaterial(), false), fluidAmount);
        return this;
    }

    public ItemStack getOutputItem() {
        return outputItem;
    }
    public FluidStack getInputFluid() {
        return inputFluid;
    }


}