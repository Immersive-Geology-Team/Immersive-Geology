package igteam.api.materials.helper;

import igteam.api.materials.data.MaterialBase;
import igteam.api.materials.pattern.BlockPattern;
import igteam.api.materials.pattern.FluidPattern;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.materials.pattern.MaterialPattern;
import igteam.api.processing.IGProcessingStage;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.IFeatureConfig;
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
        return instance().getItem((ItemPattern) pattern);
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
        return instance().getBlock((BlockPattern) pattern);
    }

    default Block getBlock(MaterialPattern pattern, MaterialBase secondaryMaterial) {
        Block b = instance().getBlock(pattern, secondaryMaterial);
        return b == null ? secondaryMaterial.getBlock(pattern, instance()) : b;
    }
    default ITag.INamedTag<Fluid> getFluidTag(FluidPattern pattern) {
        return instance().getFluidTag(pattern);
    }

    default ITag.INamedTag<Fluid> getFluidTag(FluidPattern pattern, MaterialBase... materials) {
        return instance().getFluidTag(pattern, materials);
    }

    default Fluid getFluid(MaterialPattern pattern) {
        return instance().getFluid(pattern);
    }

    default Fluid getFluid(MaterialPattern pattern, MaterialInterface<? extends MaterialBase> secondaryMaterial) {
        return instance().getFluid(pattern, secondaryMaterial);
    }
    default ITag.INamedTag<?> getTag(MaterialPattern pattern) {
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
    default ITag.INamedTag<Item> getItemTag(MaterialPattern pattern) {
        return instance().getItemTag(pattern);
    }
    default ITag.INamedTag<Block> getBlockTag(BlockPattern pattern) {
        return instance().getBlockTag(pattern);
    }

    default ITag.INamedTag<Item> getItemTag(MaterialPattern pattern, MaterialBase... materials) {
        return instance().getItemTag(pattern, materials);
    }

    default ITag.INamedTag<Block> getBlockTag(BlockPattern pattern, MaterialBase... materials) {
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

    default IFeatureConfig getGenerationConfig() {
        return instance().getGenerationConfig();
    }

    default boolean generateForBlockPattern(BlockPattern pattern) {
        return instance().generateForBlockPattern(pattern);
    };
    default MaterialSourceWorld getDimension(){
        return instance().getDimension();
    }

    boolean isFluidPortable(ItemPattern bucket);

}
