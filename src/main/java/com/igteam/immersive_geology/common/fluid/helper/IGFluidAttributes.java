package com.igteam.immersive_geology.common.fluid.helper;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.fluid.IGFluid;
import igteam.immersive_geology.materials.data.slurry.variants.MaterialSlurryWrapper;
import igteam.immersive_geology.materials.helper.MaterialTexture;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.materials.pattern.MiscPattern;
import net.minecraft.client.resources.I18n;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
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
            MaterialPattern pattern = fluid.getPattern();
            List<String> materialList = new ArrayList<>();
            if(pattern == MiscPattern.slurry){
                MaterialSlurryWrapper wrapper = (MaterialSlurryWrapper) fluid.getFluidMaterial();
                materialList.add(I18n.format("material.immersive_geology." + wrapper.getSoluteMaterial().getName()));
                materialList.add(I18n.format("component.immersive_geology." + wrapper.getFluidBase().getName()));
            } else {
                materialList.add(I18n.format("material.immersive_geology." + fluid.getFluidMaterial().getName()));
            }

            TranslationTextComponent name = new TranslationTextComponent("fluid.immersive_geology." +  pattern.getName(), materialList.toArray());
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