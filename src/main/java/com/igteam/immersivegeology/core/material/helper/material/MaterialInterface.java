package com.igteam.immersivegeology.core.material.helper.material;

import blusunrize.immersiveengineering.api.crafting.IERecipeTypes.TypeWithClass;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.RevFurnaceRecipe;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

public interface MaterialInterface<T extends GeologyMaterial> {
    T instance();

    default ItemStack getStack(ItemCategoryFlags flag) {
        return instance().getStack(flag, 1);
    }

    default ItemStack getStack(IFlagType<?> flag) {
        return flag instanceof ItemCategoryFlags iflag ? getStack(iflag, 1) : (flag instanceof BlockCategoryFlags bFlag ? getStack(bFlag, 1) : null);
    }

    default ItemStack getStack(BlockCategoryFlags flag) {
        return instance().getStack(flag, 1);
    }

    default ItemStack getStack(ItemCategoryFlags flag, int amount) {
        return instance().getStack(flag, amount);
    }

    default ItemStack getStack(IFlagType<?> flag, int amount) {
        return flag instanceof ItemCategoryFlags iflag ? getStack(iflag, amount) : (flag instanceof BlockCategoryFlags bFlag ? getStack(bFlag, amount) : null);
    }

    default ItemStack getStack(BlockCategoryFlags flag, int amount) {
        return instance().getStack(flag, amount);
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

    default TagKey<Fluid> getFluidTag(BlockCategoryFlags flag) { return instance().getFluidTag(flag);};
    default TagKey<Fluid> getFluidTag() { return instance().getFluidTag();};
    default TagKey<Fluid> getFluidTag(BlockCategoryFlags flag, MaterialInterface<?>... extras) { return instance().getFluidTag(flag, extras);};

    default LinkedHashSet<MaterialInterface<?>> getSourceMaterials() {return instance().getSourceMaterials();};

	default void buildRecipe()
    {
        instance().buildRecipe();
    };

	default Set<IGRecipeStage> getStageSet() { return instance().getStageSet();};

    default Fluid getFluid(BlockCategoryFlags flag) {return instance().getFluid(flag);};

}
