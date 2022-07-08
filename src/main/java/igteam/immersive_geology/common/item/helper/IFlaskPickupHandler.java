package igteam.immersive_geology.common.item.helper;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public interface IFlaskPickupHandler {
    Fluid pickupFlaskFluid(IWorld var1, BlockPos var2, BlockState var3);
}
