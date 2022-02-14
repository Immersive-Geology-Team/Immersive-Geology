package igteam.immersive_geology.materials.helper;

import igteam.immersive_geology.materials.data.MaterialBase;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import igteam.immersive_geology.processing.IGProcessingStage;

import java.util.Set;

public interface MaterialInterface {
    ItemStack getStack(MaterialPattern ingot);
    ItemStack getStack(MaterialPattern pattern, MaterialInterface secondaryMaterial);
    ItemStack getStack(MaterialPattern pattern, MaterialBase secondaryMaterial);
    ItemStack getStack(MaterialPattern ingot, int amount);
    ItemStack getStack(MaterialPattern pattern, MaterialInterface secondaryMaterial, int amount);
    ItemStack getStack(MaterialPattern pattern, MaterialBase secondaryMaterial, int amount);

    Fluid getFluid(MaterialPattern pattern);
    Fluid getFluid(MaterialPattern pattern, MaterialInterface secondaryMaterial);
    FluidStack getFluidStack(MaterialPattern pattern, int amount);
    FluidStack getFluidStack(MaterialPattern pattern, MaterialInterface secondaryMaterial, int amount);

    Item getItem(MaterialPattern pattern, MaterialInterface secondaryMaterial);
    Item getItem(MaterialPattern pattern, MaterialBase secondaryMaterial);
    Item getItem(MaterialPattern pattern);

    Block getBlock(MaterialPattern pattern, MaterialInterface secondaryMaterial);
    Block getBlock(MaterialPattern pattern, MaterialBase secondaryMaterial);
    Block getBlock(MaterialPattern pattern);

    ITag.INamedTag<?> getTag(MaterialPattern pattern);
    ITag.INamedTag<Item> getItemTag(ItemPattern pattern);
    ITag.INamedTag<Block> getBlockTag(BlockPattern pattern);


    ITag.INamedTag<Item> getItemTag(ItemPattern pattern, MaterialBase... materials);
    ITag.INamedTag<Block> getBlockTag(BlockPattern pattern, MaterialBase... materials);

    boolean hasPattern(MaterialPattern gear);

    MaterialBase get();

    String getName();

    Set<IGProcessingStage> getStages();

    void build();

    int getColor(MaterialPattern p);

    CrystalFamily getCrystalFamily();

    ResourceLocation getTextureLocation(MaterialPattern pattern);

    boolean hasExistingImplementation();

    boolean generateOreFor(MaterialInterface m);
}
