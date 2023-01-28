package com.igteam.immersive_geology.core.material.helper;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.core.material.GeologyMaterial;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public interface MaterialInterface<T extends GeologyMaterial> {
    T instance();

    default ItemStack getStack(ItemCategoryFlags flag) {
        return instance().getStack(flag);
    }

    default ItemStack getStack(IFlagType<?> flag) {
        ImmersiveGeology.getNewLogger().info("FLAG GET: " + flag.toString());
        return flag instanceof ItemCategoryFlags iflag ? getStack(iflag) : (flag instanceof BlockCategoryFlags bFlag ? getStack(bFlag) : null);
    }

    default ItemStack getStack(BlockCategoryFlags flag) {
        return instance().getStack(flag);
    }

    default int getColor(IFlagType<?> flag) {
        return instance().getColor(flag);
    }

    default Set<Enum<?>> getFlags() {
        return instance().getFlags();
    };

    default String getName() {
        return instance().getName();
    };
}
