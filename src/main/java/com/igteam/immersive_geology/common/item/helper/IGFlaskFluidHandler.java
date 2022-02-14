package com.igteam.immersive_geology.common.item.helper;

import com.igteam.immersive_geology.legacy_api.materials.MaterialEnum;
import com.igteam.immersive_geology.legacy_api.materials.MaterialUseType;
import com.igteam.immersive_geology.legacy_api.materials.material_bases.MaterialFluidBase;
import com.igteam.immersive_geology.common.fluid.IGFluid;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;


import javax.annotation.Nonnull;

public class IGFlaskFluidHandler extends FluidBucketWrapper {

    public IGFlaskFluidHandler(@Nonnull ItemStack container)
    {
        super(container);
    }
    @Override
    protected void setFluid(@Nonnull FluidStack fluidStack)
    {
        if (fluidStack.isEmpty())
            container = new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Glass.getMaterial(), MaterialUseType.FLASK));
        else
        {
            container = FluidUtil.getFilledBucket(fluidStack);
        }
    }

    @Override
    public boolean canFillFluidType(FluidStack fluid)
    {
        if (fluid.getFluid() instanceof IGFluid)
        {
            return ((IGFluid)fluid.getFluid()).hasFlask();

        }
        return false;
    }
}
