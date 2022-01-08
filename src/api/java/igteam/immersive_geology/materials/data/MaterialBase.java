package igteam.immersive_geology.materials.data;

import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.materials.helper.IGRegistryProvider;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.materials.pattern.MiscPattern;
import igteam.immersive_geology.tags.IGTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.StageProvider;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static igteam.immersive_geology.materials.helper.IGRegistryProvider.getRegistryKey;

public abstract class MaterialBase {

    private Set<IGProcessingStage> stageSet = new HashSet<>();

    public MaterialBase(String name) {
        this.name = name;
    }

    public void build(){
        logger.log(Level.INFO, "Building " + getName() + " Processing Stages");

        //Recipes this material implements
        setupProcessingStages();

        //provide it to the Stage Provider
        StageProvider.add(this, stageSet);
    }

    protected String name;

    private final Logger logger = LogManager.getLogger(MaterialBase.class.getName());

    public abstract ResourceLocation getTextureLocation();

    public String getOverrideName(MaterialPattern pattern) { return null; }

    public Block getBlock(MaterialPattern p) {
        return IGRegistryProvider.IG_BLOCK_REGISTRY.get(getRegistryKey(this, p));
    };

    public Block getBlock(MaterialPattern p, MaterialInterface secondaryMaterial){
        return IGRegistryProvider.IG_BLOCK_REGISTRY.get(getRegistryKey(secondaryMaterial, p));
    }

    public Item getItem(MaterialPattern pattern) {
        return IGRegistryProvider.IG_ITEM_REGISTRY.get(getRegistryKey(this, pattern));
    };

    public Item getItem(MaterialPattern pattern, MaterialInterface secondaryMaterial) {
        return IGRegistryProvider.IG_ITEM_REGISTRY.get(getRegistryKey(this, secondaryMaterial, pattern));
    }

    public ItemStack getStack(MaterialPattern pattern) {
        return new ItemStack(getItem(pattern));
    };

    public ItemStack getStack(MaterialPattern pattern, MaterialInterface secondaryMaterial) {
        return new ItemStack(getItem(pattern, secondaryMaterial));
    };

    public Fluid getFluid(MaterialPattern pattern){
        Fluid f = IGRegistryProvider.IG_FLUID_REGISTRY.get(getRegistryKey(this, pattern));
        return f;
    }

    public Fluid getFluid(MaterialPattern pattern, MaterialInterface secondaryMaterial){
        Fluid f = IGRegistryProvider.IG_FLUID_REGISTRY.get(getRegistryKey(this, secondaryMaterial, pattern));
        return f;
    }

    public FluidStack getFluidStack(MaterialPattern pattern, int amount) {
        return new FluidStack(getFluid(pattern), amount);
    }

    public FluidStack getFluidStack(MaterialPattern pattern, MaterialInterface secondaryMaterial, int amount) {
        return new FluidStack(getFluid(pattern, secondaryMaterial), amount);
    }

    public Tag.Named<?> getTag(MaterialPattern pattern) {
        if(pattern instanceof ItemPattern i){
            HashMap<MaterialBase, Tag.Named<Item>> data_map = IGTags.IG_ITEM_TAGS.get(i);
            Tag.Named<Item> tag = data_map.get(this);
            return tag;
        }
        if(pattern instanceof BlockPattern b){
            HashMap<MaterialBase, Tag.Named<Block>> data_map = IGTags.IG_BLOCK_TAGS.get(b);
            Tag.Named<Block> tag = data_map.get(this);
            return tag;
        }
        if(pattern instanceof MiscPattern f){
            HashMap<MaterialBase, Tag.Named<Fluid>> data_map = IGTags.IG_FLUID_TAGS.get(f);
            Tag.Named<Fluid> tag = data_map.get(this);
            return tag;
        }
        return null;
    }

    public abstract int getColor();

    public abstract Rarity getRarity();

    protected abstract boolean hasStorageBlock();

    protected abstract boolean hasStairs();

    protected abstract boolean hasOreBlock();

    protected abstract boolean hasGeodeBlock();

    protected abstract boolean hasDefaultBlock();

    protected abstract boolean hasSlab();

    protected abstract boolean hasIngot();

    protected abstract boolean hasWire();

    protected abstract boolean hasGear();

    protected abstract boolean hasRod();

    protected abstract boolean isMachine();

    protected abstract boolean isSlurry();

    protected abstract boolean isFluid();

    protected abstract boolean hasClay();

    protected abstract boolean hasDust();

    protected abstract boolean hasFuel();

    protected abstract boolean hasSlag();

    protected abstract boolean hasPlate();

    protected abstract boolean hasNugget();

    protected abstract boolean hasCrystal();

    protected abstract boolean hasOreBit();

    protected abstract boolean hasOreChunk();

    protected abstract boolean hasStoneBit();

    protected abstract boolean hasCrushedOre();

    protected abstract boolean hasMetalOxide();

    protected abstract boolean hasStoneChunk();

    protected abstract boolean hasCompoundDust();

    protected abstract boolean hasDirtyCrushedOre();

    public boolean hasExistingImplementation(){
        return false;
    }

    public ItemStack getStack(MaterialPattern pattern, int amount){
        ItemStack stack = getStack(pattern);
        stack.setCount(amount);
        return stack;
    }

    public ItemStack getStack(MaterialPattern pattern, MaterialInterface secondaryMaterial, int amount){
        ItemStack stack = getStack(pattern, secondaryMaterial);
        stack.setCount(amount);
        return stack;
    }

    public boolean hasPattern(MaterialPattern pattern) {
        if(pattern instanceof ItemPattern){
            ItemPattern p = (ItemPattern) pattern;
            return switch (p) {
                case rod -> hasRod();
                case gear -> hasGear();
                case wire -> hasWire();
                case ingot -> hasIngot();
                case clay -> hasClay();
                case dust -> hasDust();
                case fuel -> hasFuel();
                case slag -> hasSlag();
                case plate -> hasPlate();
                case nugget -> hasNugget();
                case crystal -> hasCrystal();
                case ore_bit -> hasOreBit();
                case ore_chunk -> hasOreChunk();
                case stone_bit -> hasStoneBit();
                case crushed_ore -> hasCrushedOre();
                case metal_oxide -> hasMetalOxide();
                case stone_chunk -> hasStoneChunk();
                case compound_dust -> hasCompoundDust();
                case dirty_crushed_ore -> hasDirtyCrushedOre();
            };
        }

        if(pattern instanceof BlockPattern){
            BlockPattern p = (BlockPattern) pattern;
            return switch (p){
                case slab -> hasSlab();
                case block -> hasDefaultBlock();
                case geode -> hasGeodeBlock();
                case ore -> hasOreBlock();
                case stairs -> hasStairs();
                case storage -> hasStorageBlock();
            };
        }

        if(pattern instanceof MiscPattern){
            MiscPattern p = (MiscPattern) pattern;
            return switch (p){
                case fluid -> isFluid();
                case slurry -> isSlurry();
                case machine -> isMachine();
            };
        }

        return false;
    }

    protected void setupProcessingStages(){

    }

    public String getName(){
        return this.name;
    }

    public void addStage(IGProcessingStage igProcessingStage) {
        stageSet.add(igProcessingStage);
    }

    public Set<IGProcessingStage> getStages(){
        return StageProvider.get(this);
    }

    /**
     * @apiNote Wrapped version of the normal @getTag used to reduce castings
     */
    public Tag.Named<Item> getItemTag(ItemPattern pattern){
        return (Tag.Named<Item>) getTag(pattern);
    }

    /**
     * @apiNote Wrapped version of the normal @getTag used to reduce castings
     */
    public Tag.Named<Block> getBlockTag(BlockPattern pattern){
        return (Tag.Named<Block>) getTag(pattern);
    }
}
