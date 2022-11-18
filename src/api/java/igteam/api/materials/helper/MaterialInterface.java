package igteam.api.materials.helper;

import igteam.api.materials.data.MaterialBase;
import igteam.api.materials.pattern.BlockFamily;
import igteam.api.materials.pattern.FluidFamily;
import igteam.api.materials.pattern.ItemFamily;
import igteam.api.materials.pattern.MaterialPattern;
import igteam.api.processing.IGProcessingStage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.Set;

public interface MaterialInterface<T extends MaterialBase> {
    T instance();
    default ItemStack getStack(MaterialPattern pattern) {
        return instance().getStack(pattern);
    }

    default ItemStack getStack(MaterialPattern pattern, MaterialInterface<?> secondaryMaterial) {
        ItemStack i = instance().getStack(pattern, secondaryMaterial);
        return i.isEmpty() ? secondaryMaterial.getStack(pattern, instance()) : i;
    }

    default ItemStack getStack(MaterialPattern pattern, MaterialBase secondaryMaterial) {
        ItemStack i = instance().getStack(pattern, secondaryMaterial);
        return i.isEmpty() ? secondaryMaterial.getStack(pattern, instance()) : i;
    }


    default ItemStack getStack(MaterialPattern pattern, int amount) {
        return instance().getStack(pattern, amount);
    }

    default ItemStack getStack(MaterialPattern pattern, MaterialInterface<? extends MaterialBase> secondaryMaterial, int amount) {
        ItemStack i = instance().getStack(pattern, secondaryMaterial, amount);
        return i.isEmpty() ? secondaryMaterial.getStack(pattern, instance(), amount) : i;
    }

    default ItemStack getStack(MaterialPattern pattern, MaterialBase secondaryMaterial, int amount) {
        ItemStack i = instance().getStack(pattern, secondaryMaterial, amount);
        return i.isEmpty() ? secondaryMaterial.getStack(pattern, instance(), amount) : i;
    }
    default FluidStack getFluidStack(MaterialPattern pattern, int amount) {
        return instance().getFluidStack(pattern, amount);
    }

    default FluidStack getFluidStack(MaterialPattern pattern, MaterialInterface<? extends MaterialBase> secondaryMaterial, int amount) {
        return instance().getFluidStack(pattern, secondaryMaterial, amount);
    }

    default Item getItem(MaterialPattern pattern) {
        return instance().getItem((ItemFamily) pattern);
    }

    default Item getItem(MaterialPattern pattern, MaterialInterface<? extends MaterialBase> secondaryMaterial) {
        Item i = instance().getItem(pattern, secondaryMaterial);
        return i == null ? secondaryMaterial.getItem(pattern, instance()) : i;
    }

    default Item getItem(MaterialPattern pattern, MaterialBase secondaryMaterial) {
        Item i = instance().getItem(pattern, secondaryMaterial);
        return i == null ? secondaryMaterial.getItem(pattern, instance()) : i;
    }

    default Block getBlock(MaterialPattern pattern, MaterialInterface<? extends MaterialBase> secondaryMaterial) {
        Block i = instance().getBlock(pattern, secondaryMaterial);
        return i == null ? secondaryMaterial.getBlock(pattern, instance()) : i;
    }

    default Block getBlock(MaterialPattern pattern) {
        return instance().getBlock((BlockFamily) pattern);
    }

    default Block getBlock(MaterialPattern pattern, MaterialBase secondaryMaterial) {
        Block b = instance().getBlock(pattern, secondaryMaterial);
        return b == null ? secondaryMaterial.getBlock(pattern, instance()) : b;
    }
    default TagKey<Fluid> getFluidTag(FluidFamily pattern) {
        return instance().getFluidTag(pattern);
    }

    default TagKey<Fluid> getFluidTag(FluidFamily pattern, MaterialBase... materials) {
        return instance().getFluidTag(pattern, materials);
    }

    default Fluid getFluid(MaterialPattern pattern) {
        return instance().getFluid(pattern);
    }

    default Fluid getFluid(MaterialPattern pattern, MaterialInterface<? extends MaterialBase> secondaryMaterial) {
        return instance().getFluid(pattern, secondaryMaterial);
    }
    default TagKey<?> getTag(MaterialPattern pattern) {
        return instance().getTag(pattern);
    }

    default boolean hasPattern(MaterialPattern pattern) {
        return instance().hasPattern(pattern);
    }

    default String getName() {
        return instance().getName();
    }

    default void build() {
        instance().build();
    }

    default int getColor(MaterialPattern p) {
        return instance().getColor(p);
    }

    default Set<IGProcessingStage> getStages() {
        return instance().getStages();
    }
    default TagKey<Item> getItemTag(MaterialPattern pattern) {
        return instance().getItemTag(pattern);
    }
    default TagKey<Block> getBlockTag(BlockFamily pattern) {
        return instance().getBlockTag(pattern);
    }

    default TagKey<Item> getItemTag(MaterialPattern pattern, MaterialBase... materials) {
        return instance().getItemTag(pattern, materials);
    }

    default TagKey<Block> getBlockTag(BlockFamily pattern, MaterialBase... materials) {
        return instance().getBlockTag(pattern, materials);
    }

    default ResourceLocation getTextureLocation(MaterialPattern pattern) {
        return instance().getTextureLocation(pattern);
    }

    default ResourceLocation getTextureLocation(MaterialPattern pattern, int subtype) {
        return instance().getTextureLocation(pattern, subtype);
    }

    default CrystalFamily getCrystalFamily() {
        return instance().getCrystalFamily();
    }

    default boolean hasExistingImplementation() {
        return instance().hasExistingImplementation();
    }

    default boolean generateOreFor(MaterialInterface<? extends MaterialBase> m) {
        return instance().generateOreFor(m);
    }

    default FeatureConfiguration getGenerationConfig() {
        return instance().getGenerationConfig();
    }

    default boolean generateForBlockFamily(BlockFamily family) {
        return instance().generateForBlockFamily(family);
    };
    default MaterialSourceWorld getDimension(){
        return instance().getDimension();
    }

    boolean isFluidPortable(ItemFamily bucket);
}
