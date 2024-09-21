package com.igteam.immersivegeology.core.material.helper.material;

import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;

import java.util.Properties;
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

	default boolean hasFlag(IFlagType<?> category)
    {
        return getFlags().contains(category);
    }

    default TagKey<Item> getItemTag(IFlagType<ItemCategoryFlags> itemFlag) { return instance().getItemTag(itemFlag); }

    default FluidType.Properties getFluidProperties() { return instance().getFluidProperties(BlockCategoryFlags.FLUID);}

    default IClientFluidTypeExtensions getFluidExtendedProperties() {return instance().getFluidExtendedProperties(BlockCategoryFlags.FLUID);};

	default boolean checkLoadedModFlags() {return instance().checkLoadedModFlags();};

	default TagKey<Fluid> getFluidTag() { return instance().getFluidTag();};
}
