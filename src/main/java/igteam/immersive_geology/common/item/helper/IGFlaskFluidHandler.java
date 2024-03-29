package igteam.immersive_geology.common.item.helper;

import igteam.immersive_geology.common.fluid.IGFluid;
import igteam.api.materials.MiscEnum;
import igteam.api.materials.pattern.ItemPattern;
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
            container = MiscEnum.Glass.getStack(ItemPattern.flask);
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
            return ((IGFluid)fluid.getFluid()).getFluidMaterial().isFluidPortable(ItemPattern.flask);

        }
        return false;
    }
}
