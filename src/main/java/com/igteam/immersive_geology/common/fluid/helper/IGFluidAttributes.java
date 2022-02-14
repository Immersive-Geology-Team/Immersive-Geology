package com.igteam.immersive_geology.common.fluid.helper;

import com.igteam.immersive_geology.common.fluid.IGFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.function.BiFunction;

public class IGFluidAttributes extends FluidAttributes {
    protected IGFluidAttributes(Builder builder, Fluid fluid) {
        super(builder, fluid);
    }

    public static IGBuilder builder(ResourceLocation stillTexture, ResourceLocation flowingTexture) {
        return new IGBuilder(stillTexture, flowingTexture, IGFluidAttributes::new);
    }

    @Override
    public ITextComponent getDisplayName(FluidStack stack) {
        ArrayList<String> localizedNames = new ArrayList<>();
        if (stack.getFluid() instanceof IGFluid) {
            IGFluid fluid = (IGFluid) stack.getFluid();
            String base_name = getTranslationKey();
//            if (fluid.getFluidMaterial() instanceof SlurryEnum) {
//                MaterialSlurryWrapper slurry = (MaterialSlurryWrapper) fluid.getFluidMaterial();
//                localizedNames.add(slurry.getSoluteMaterial().getDisplayName());
//                localizedNames.add(slurry.getBaseFluidMaterial().getComponentName());
//                base_name = "fluid."+IGLib.MODID+".slurry";
//            }
            TranslationTextComponent name = new TranslationTextComponent(base_name, localizedNames.toArray(new Object[localizedNames.size()]));
            return name;
        }
        return super.getDisplayName(stack);
    }

    //Create a wrapper to use the builder, doesn't like us using Builder directly
    public static class IGBuilder extends Builder {
        protected IGBuilder(ResourceLocation stillTexture, ResourceLocation flowingTexture, BiFunction<Builder, Fluid, FluidAttributes> factory) {
            super(stillTexture, flowingTexture, factory);
        }
    }
}