package com.igteam.immersive_geology.api.materials.helper.processing.methods;

import com.igteam.immersive_geology.api.materials.fluid.FluidEnum;
import com.igteam.immersive_geology.api.materials.fluid.SlurryEnum;
import com.igteam.immersive_geology.api.materials.helper.processing.IGProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.ProcessingMethod;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;


public class IGVatProcessingMethod extends IGProcessingMethod {

    private final int energyCost;
    private final int processingTime;

    private ItemStack outputItem, inputItem;
    private FluidStack primaryInputFluid, secondaryInputFluid, outputFluid;

    private IGVatProcessingMethod reversedFluids;

    public IGVatProcessingMethod(int energyCost, int processingTime) {
        this.energyCost = energyCost;
        this.processingTime = processingTime;
        this.reversedFluids = new IGVatProcessingMethod(energyCost, processingTime);
    }

    @Override
    public ProcessingMethod getKey() {
        return ProcessingMethod.ACID;
    }

    @Override
    public int getEnergyCost() {
        return energyCost;
    }

    @Override
    public int getProcessingTime() {
        return processingTime;
    }

    public IGVatProcessingMethod addItemOutput(ItemStack inputItem){
        this.outputItem = inputItem.copy();
        this.reversedFluids.outputItem = inputItem.copy();
        return this;
    }

    public IGVatProcessingMethod addFluidOutput(FluidEnum fluid, int fluidOutputAmount){
        this.outputFluid = new FluidStack(IGRegistrationHolder.getFluidByMaterial(fluid.getMaterial(), false), fluidOutputAmount);
        this.reversedFluids.outputFluid = outputFluid.copy();
        return this;
    }

    public IGVatProcessingMethod addFluidOutput(SlurryEnum slurry, int entry, int fluidOutputAmount){
        this.outputFluid = new FluidStack(IGRegistrationHolder.getSlurryByMaterials(slurry.getEntries().get(entry).getSoluteMaterial(), slurry.getEntries().get(entry).getBaseFluidMaterial(), false), fluidOutputAmount);
        this.reversedFluids.outputFluid = outputFluid.copy();
        return this;
    }

    public IGVatProcessingMethod addFluidOutput(Fluid fluid, int fluidOutputAmount){
        this.outputFluid = new FluidStack(fluid, fluidOutputAmount);
        this.reversedFluids.outputFluid = outputFluid.copy();
        return this;
    }

    public IGVatProcessingMethod addPrimaryFluidInput(SlurryEnum slurry, int entry, int primaryFluidAmount){
        this.primaryInputFluid = new FluidStack(IGRegistrationHolder.getSlurryByMaterials(slurry.getEntries().get(entry).getSoluteMaterial(), slurry.getEntries().get(entry).getBaseFluidMaterial(), false), primaryFluidAmount);
        this.reversedFluids.secondaryInputFluid = primaryInputFluid.copy();
        return this;
    }

    public IGVatProcessingMethod addPrimaryFluidInput(Fluid fluid, int primaryFluidAmount){
        this.primaryInputFluid = new FluidStack(fluid, primaryFluidAmount);
        this.reversedFluids.secondaryInputFluid = primaryInputFluid.copy();
        return this;
    }

    public IGVatProcessingMethod addPrimaryFluidInput(FluidEnum fluid, int primaryFluidAmount){
        this.primaryInputFluid = new FluidStack(IGRegistrationHolder.getFluidByMaterial(fluid.getMaterial(), false), primaryFluidAmount);
        this.reversedFluids.secondaryInputFluid = primaryInputFluid.copy();
        return this;
    }

    public IGVatProcessingMethod addSecondaryFluidInput(SlurryEnum slurry, int entry, int secondaryFluidAmount){
        this.secondaryInputFluid = new FluidStack(IGRegistrationHolder.getSlurryByMaterials(slurry.getEntries().get(entry).getSoluteMaterial(), slurry.getEntries().get(entry).getBaseFluidMaterial(), false), secondaryFluidAmount);
        this.reversedFluids.primaryInputFluid = secondaryInputFluid.copy();
        return this;
    }

    public IGVatProcessingMethod addSecondaryFluidInput(FluidEnum fluid, int secondaryFluidAmount){
        this.secondaryInputFluid = new FluidStack(IGRegistrationHolder.getFluidByMaterial(fluid.getMaterial(), false), secondaryFluidAmount);
        this.reversedFluids.primaryInputFluid = secondaryInputFluid.copy();
        return this;
    }

    public IGVatProcessingMethod addSecondaryFluidInput(Fluid fluid, int secondaryFluidAmount){
        this.secondaryInputFluid = new FluidStack(fluid, secondaryFluidAmount);
        this.reversedFluids.primaryInputFluid = secondaryInputFluid.copy();
        return this;
    }

    public IGVatProcessingMethod addItemInput(ItemStack input){
        this.inputItem = input.copy();
        this.reversedFluids.primaryInputFluid = secondaryInputFluid.copy();
        return this;
    }

    public ItemStack getOutputItem() {
        return outputItem;
    }

    public ItemStack getInputItem() {
        return inputItem;
    }

    public FluidStack getPrimaryInputFluid() {
        return primaryInputFluid;
    }

    public FluidStack getSecondaryInputFluid() {
        return secondaryInputFluid;
    }

    public FluidStack getOutputFluid() {
        return outputFluid;
    }

    public IGVatProcessingMethod getReversedProcess(){
        return this.reversedFluids;
    }
}
