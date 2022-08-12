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
    ItemStack getStack(MaterialPattern ingot);
    ItemStack getStack(MaterialPattern pattern, MaterialInterface<? extends MaterialBase> secondaryMaterial);
    ItemStack getStack(MaterialPattern pattern, MaterialBase secondaryMaterial);
    ItemStack getStack(MaterialPattern ingot, int amount);
    ItemStack getStack(MaterialPattern pattern, MaterialInterface<? extends MaterialBase> secondaryMaterial, int amount);
    ItemStack getStack(MaterialPattern pattern, MaterialBase secondaryMaterial, int amount);

    Fluid getFluid(MaterialPattern pattern);
    Fluid getFluid(MaterialPattern pattern, MaterialInterface<? extends MaterialBase> secondaryMaterial);
    FluidStack getFluidStack(MaterialPattern pattern, int amount);
    FluidStack getFluidStack(MaterialPattern pattern, MaterialInterface<? extends MaterialBase> secondaryMaterial, int amount);

    Item getItem(MaterialPattern pattern, MaterialInterface<? extends MaterialBase> secondaryMaterial);
    Item getItem(MaterialPattern pattern, MaterialBase secondaryMaterial);
    Item getItem(MaterialPattern pattern);

    Block getBlock(MaterialPattern pattern, MaterialInterface<? extends MaterialBase> secondaryMaterial);
    Block getBlock(MaterialPattern pattern, MaterialBase secondaryMaterial);
    Block getBlock(MaterialPattern pattern);

    ITag.INamedTag<?> getTag(MaterialPattern pattern);
    ITag.INamedTag<Item> getItemTag(MaterialPattern pattern);
    ITag.INamedTag<Block> getBlockTag(BlockPattern pattern);
    ITag.INamedTag<Fluid> getFluidTag(FluidPattern pattern);

    ITag.INamedTag<Item> getItemTag(MaterialPattern pattern, MaterialBase... materials);
    ITag.INamedTag<Block> getBlockTag(BlockPattern pattern, MaterialBase... materials);
    ITag.INamedTag<Fluid> getFluidTag(FluidPattern pattern, MaterialBase... materials);

    boolean hasPattern(MaterialPattern gear);

    T instance();

    String getName();

    Set<IGProcessingStage> getStages();

    void build();

    int getColor(MaterialPattern p);

    CrystalFamily getCrystalFamily();

    ResourceLocation getTextureLocation(MaterialPattern pattern);

    ResourceLocation getTextureLocation(MaterialPattern pattern, int subtype);

    boolean hasExistingImplementation();

    boolean generateOreFor(MaterialInterface m);

    IFeatureConfig getGenerationConfig();

    boolean isFluidPortable(ItemPattern bucket);

    default boolean generateForBlockPattern(BlockPattern pattern) {
        return instance().generateForBlockPattern(pattern);
    };

    default MaterialSourceWorld getDimension(){
        return instance().getDimension();
    }
}
