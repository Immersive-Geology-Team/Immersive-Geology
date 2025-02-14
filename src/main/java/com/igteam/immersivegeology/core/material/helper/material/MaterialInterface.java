package com.igteam.immersivegeology.core.material.helper.material;

import com.igteam.immersivegeology.client.helper.IGVeinTextureType;

import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
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

    default int getColor(IFlagType<?> flag, int secondaryColors) {
        return instance().getColor(flag, secondaryColors);
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
    @Nullable
    default TagKey<Fluid> getFluidTag(BlockCategoryFlags flag, MaterialInterface<?>... extras) { return instance().getFluidTag(flag, extras);};
    @Nullable
    default TagKey<Fluid> getFluidTag(BlockCategoryFlags flag, MaterialHelper... extras) { return instance().getFluidTag(flag, extras);};

    default LinkedHashSet<MaterialInterface<?>> getDerivedMaterials() {return instance().getDerivedMaterials();};

	default void buildRecipe()
    {
        instance().buildRecipe();
    };

	default Set<IGRecipeStage> getStageSet() { return instance().getStageSet();};

    default Fluid getFluid(BlockCategoryFlags flag) {return instance().getFluid(flag);};

	default Block getBlock(BlockCategoryFlags flag) {return instance().getBlock(flag);};

    default IOreBlock getOreBlock(StoneEnum stone, OreRichness richness) {
        return instance().getOreBlock(stone, richness);
    };

    default IOreBlock getOreBlock(MaterialHelper stone, OreRichness richness) {
        return instance().getOreBlock(stone, richness);
    };

	default Item getItem(ItemCategoryFlags itemCategoryFlags) {return instance().getItem(itemCategoryFlags);};

    default boolean canTarnish() {return instance().canTarnish();};

	default IGVeinTextureType getVeinTextureType()
    {
        return instance().getVeinTextureType();
    }

    default MaterialInterface<?> getPrimaryProduct() {return instance().getPrimaryProduct();}
    default MaterialInterface<?> getSecondaryProduct() {return instance().getSecondaryProduct();}
    default MaterialInterface<?> getTraceProduct(int index) {return instance().getTraceProduct(index);}

    default boolean useSedimentaryTextures() {return instance().useSedimentaryTextures();}

    default Set<MaterialHelper> getOriginMaterials() {return instance().getOriginMaterials();};
}
