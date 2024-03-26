package com.igteam.immersivegeology.core.material.helper.material;

import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.Set;

public interface MaterialInterface<T extends GeologyMaterial> {
    T instance();

    default ItemStack getStack(ItemCategoryFlags flag) {
        return instance().getStack(flag);
    }

    default ItemStack getStack(IFlagType<?> flag) {
        return flag instanceof ItemCategoryFlags iflag ? getStack(iflag) : (flag instanceof BlockCategoryFlags bFlag ? getStack(bFlag) : null);
    }

    default ItemStack getStack(BlockCategoryFlags flag) {
        return instance().getStack(flag);
    }

    default int getColor(IFlagType<?> flag) {
        return instance().getColor(flag);
    }

    default Set<IFlagType<?>> getFlags() {
        return instance().getFlags();
    };

    default String getName() {
        return instance().getName();
    };

    default ResourceLocation getTextureLocation(IFlagType<?> flag) {
        return instance().getTextureLocation(flag);
    }

    default ForgeFlowingFluid.Properties getFluidProperties() { return instance().getFluidProperties(BlockCategoryFlags.FLUID);}
}
