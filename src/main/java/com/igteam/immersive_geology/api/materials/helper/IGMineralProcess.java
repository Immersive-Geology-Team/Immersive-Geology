package com.igteam.immersive_geology.api.materials.helper;

import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import javafx.util.Pair;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.system.CallbackI;

public class IGMineralProcess {

    protected FluidStack processingFluid;
    protected MaterialEnum resultingMaterial;
    protected ItemStack processingCatalyst;
    protected MaterialUseType resultingType;
    protected Material processingMaterial;

    public IGMineralProcess(Pair<MaterialEnum, MaterialUseType> result, FluidStack fluid, ItemStack catalyst)
    {
        this(result);
        this.processingFluid = fluid;
        this.processingCatalyst = catalyst;
    }

    public IGMineralProcess(Pair<MaterialEnum, MaterialUseType> result, FluidStack fluid, Material catalyst, MaterialUseType type){
        this(result, fluid, new ItemStack(IGRegistrationHolder.getItemByMaterial(catalyst, type)));
        this.processingMaterial = catalyst;
    }

    public IGMineralProcess(Pair<MaterialEnum, MaterialUseType> result){
        this.resultingMaterial = result.getKey();
        this.resultingType = result.getValue();
    }

    public FluidStack getProcessingFluid() {
        return processingFluid;
    }

    public ItemStack getResultAsItemStack(){
        return resultingType != MaterialUseType.SLURRY ? new ItemStack(IGRegistrationHolder.getItemByMaterial(resultingMaterial.getMaterial(), resultingType)) : new ItemStack(Items.APPLE);
    }

    //TODO refactor getFluidByMaterial to allow selection of base fluid
    public FluidStack getResultingFluid(){
        return new FluidStack(IGRegistrationHolder.getSlurryByMaterial(processingMaterial, false), 125);
    }

    public MaterialEnum getResultingMaterial() {
        return resultingMaterial;
    }

    public MaterialUseType getResultingType(){
        return resultingType;
    }

    public ItemStack getProcessingCatalyst() {
        return processingCatalyst;
    }
}
