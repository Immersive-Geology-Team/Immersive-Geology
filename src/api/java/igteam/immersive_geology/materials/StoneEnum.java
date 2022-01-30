package igteam.immersive_geology.materials;

import igteam.immersive_geology.materials.data.MaterialBase;
import igteam.immersive_geology.materials.data.stone.variants.MaterialDeepslate;
import igteam.immersive_geology.materials.data.stone.variants.MaterialDefaultStone;
import igteam.immersive_geology.materials.data.stone.variants.MaterialGranite;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import igteam.immersive_geology.processing.IGProcessingStage;

import java.util.ArrayList;
import java.util.Set;

public enum StoneEnum implements MaterialInterface {
    Stone(new MaterialDefaultStone()),
    Deepslate(new MaterialDeepslate()),
    Granite(new MaterialGranite());

    private final MaterialBase material;

    StoneEnum(MaterialBase m){
        this.material = m;
    }

    @Override
    public ItemStack getStack(MaterialPattern pattern) {
        return material.getStack(pattern);
    }

    @Override
    public ItemStack getStack(MaterialPattern pattern, MaterialInterface secondaryMaterial) {
        ItemStack i = material.getStack(pattern, secondaryMaterial);
        return i.isEmpty() ? secondaryMaterial.getStack(pattern, material) : i;
    }

    @Override
    public ItemStack getStack(MaterialPattern pattern, MaterialBase secondaryMaterial) {
        ItemStack i = material.getStack(pattern, secondaryMaterial);
        return i.isEmpty() ? secondaryMaterial.getStack(pattern, material) : i;
    }

    @Override
    public ItemStack getStack(MaterialPattern pattern, int amount) {
        return material.getStack(pattern, amount);
    }

    @Override
    public ItemStack getStack(MaterialPattern pattern, MaterialInterface secondaryMaterial, int amount) {
        ItemStack i = material.getStack(pattern, secondaryMaterial, amount);
        return i.isEmpty() ? secondaryMaterial.getStack(pattern, material, amount) : i;
    }

    @Override
    public ItemStack getStack(MaterialPattern pattern, MaterialBase secondaryMaterial, int amount) {
        ItemStack i = material.getStack(pattern, secondaryMaterial, amount);
        return i.isEmpty() ? secondaryMaterial.getStack(pattern, material, amount) : i;
    }

    @Override
    public Fluid getFluid(MaterialPattern pattern) {
        return material.getFluid(pattern);
    }

    @Override
    public Fluid getFluid(MaterialPattern pattern, MaterialInterface secondaryMaterial) {
        return material.getFluid(pattern, secondaryMaterial);
    }

    @Override
    public FluidStack getFluidStack(MaterialPattern pattern, int amount) {
        return material.getFluidStack(pattern, amount);
    }

    @Override
    public FluidStack getFluidStack(MaterialPattern pattern, MaterialInterface secondaryMaterial, int amount) {
        return material.getFluidStack(pattern, secondaryMaterial, amount);
    }

    @Override
    public Item getItem(MaterialPattern pattern) {
        return material.getItem(pattern);
    }

    @Override
    public Item getItem(MaterialPattern pattern, MaterialInterface secondaryMaterial) {
        Item i = material.getItem(pattern, secondaryMaterial);
        return i == null ? secondaryMaterial.getItem(pattern, material) : i;
    }

    @Override
    public Item getItem(MaterialPattern pattern, MaterialBase secondaryMaterial) {
        Item i = material.getItem(pattern, secondaryMaterial);
        return i == null ? secondaryMaterial.getItem(pattern, material) : i;
    }

    @Override
    public Block getBlock(MaterialPattern pattern, MaterialInterface secondaryMaterial) {
        Block i = material.getBlock(pattern, secondaryMaterial);
        return i == null ? secondaryMaterial.getBlock(pattern, material) : i;
    }

    @Override
    public Block getBlock(MaterialPattern pattern, MaterialBase secondaryMaterial) {
        Block b = material.getBlock(pattern, secondaryMaterial);
        return b == null ? secondaryMaterial.getBlock(pattern, material) : b;
    }

    @Override
    public Block getBlock(MaterialPattern pattern) {
        return material.getBlock(pattern);
    }

    @Override
    public Tag.Named<?> getTag(MaterialPattern pattern) {
        return material.getTag(pattern);
    }

    @Override
    public boolean hasPattern(MaterialPattern pattern) {
        return material.hasPattern(pattern);
    }

    @Override
    public String getName() {
        return material.getName();
    }

    @Override
    public Set<IGProcessingStage> getStages() {
        return material.getStages();
    }

    @Override
    public void build() {
        material.build();
    }

    @Override
    public Tag.Named<Item> getItemTag(ItemPattern pattern) {
        return material.getItemTag(pattern);
    }

    @Override
    public Tag.Named<Block> getBlockTag(BlockPattern pattern) {
        return material.getBlockTag(pattern);
    }

    @Override
    public Tag.Named<Item> getItemTag(ItemPattern pattern, MaterialBase... materials) {
        return material.getItemTag(pattern, materials);
    }

    @Override
    public Tag.Named<Block> getBlockTag(BlockPattern pattern, MaterialBase... materials) {
        return material.getBlockTag(pattern, materials);
    }

    @Override
    public int getColor(MaterialPattern p) {
        return material.getColor(p);
    }

    @Override
    public ResourceLocation getTextureLocation(MaterialPattern pattern) {
        return material.getTextureLocation(pattern);
    }

    @Override
    public boolean hasExistingImplementation() {
        return material.hasExistingImplementation();
    }

    @Override
    public boolean generateOreFor(MaterialInterface m) {
        return material.generateOreFor(m);
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return material.getCrystalFamily();
    }

    @Override
    public MaterialBase get() {
        return material;
    }
}
