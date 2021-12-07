package com.igteam.immersive_geology.common.fluid.helper;

import com.igteam.immersive_geology.api.materials.material_data.fluids.slurry.MaterialSlurryWrapper;
import com.igteam.immersive_geology.common.fluid.IGFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

public class IGFluidAttributes extends FluidAttributes {
    protected IGFluidAttributes(Builder builder, Fluid fluid) {
        super(builder, fluid);
    }

    @Override
    public ITextComponent getDisplayName(FluidStack stack) {
        if(stack.getFluid() instanceof IGFluid){
            IGFluid fluid = (IGFluid) stack.getFluid();
            if(fluid.getFluidMaterial() instanceof MaterialSlurryWrapper){

            }
        }
        return super.getDisplayName(stack);
    }
}
