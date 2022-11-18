package igteam.api.materials.data;

import blusunrize.immersiveengineering.api.EnumMetals;
import blusunrize.immersiveengineering.api.IETags;
import igteam.api.main.IGRegistryProvider;
import igteam.api.materials.helper.MaterialSourceWorld;
import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.pattern.FluidFamily;
import igteam.api.IGApi;
import igteam.api.materials.helper.CrystalFamily;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.BlockFamily;
import igteam.api.materials.pattern.ItemFamily;
import igteam.api.materials.pattern.MaterialPattern;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.StageProvider;
import igteam.api.tags.IGTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static igteam.api.main.IGRegistryProvider.getRegistryKey;

public abstract class MaterialBase {
    private Set<IGProcessingStage> stageSet = new HashSet<>();
    protected Function<MaterialPattern, Integer> colorFunction;
    protected Predicate<MaterialPattern> applyColorTint;

    protected MaterialSourceWorld sourceWorld;

    public MaterialBase(String name) {
        this.name = name;
        initializeColorMap((p) -> (p == ItemFamily.ingot ? 0xFF0000 :  0xFFFFFF));
        initializeColorTint((p) -> true); //default will be overridden later on in ClientProxy
        sourceWorld = MaterialSourceWorld.overworld;
    }

    public static boolean isExistingPattern(MaterialPattern pattern){
        return (pattern == ItemFamily.ingot || pattern == ItemFamily.dust || pattern == ItemFamily.plate || pattern == ItemFamily.nugget);
    }

    protected void initializeColorMap(Function<MaterialPattern, Integer> function) {
        colorFunction = function;
    }

    public void initializeColorTint(Predicate<MaterialPattern> predicate) {
        applyColorTint = predicate;
    }

    public void build() {
        logger.debug("Building " + getName() + " Processing Stages");

        //Recipes this material implements
        setupProcessingStages();

        //provide it to the Stage Provider
        StageProvider.add(this, stageSet);
    }

    protected String name;

    private final Logger logger = LogManager.getLogger(MaterialBase.class.getName());

    public Block getBlock(BlockFamily p) {
        return IGRegistryProvider.IG_BLOCK_REGISTRY.get(getRegistryKey(this, p));
    };

    public Block getBlock(MaterialPattern p, MaterialInterface secondaryMaterial) {
        return IGRegistryProvider.IG_BLOCK_REGISTRY.get(IGRegistryProvider.getRegistryKey(this, secondaryMaterial, p));
    }

    public Block getBlock(MaterialPattern p, MaterialBase secondaryMaterial) {
        return IGRegistryProvider.IG_BLOCK_REGISTRY.get(IGRegistryProvider.getRegistryKey(this, secondaryMaterial, p));
    }

    public Item getItem(MaterialPattern pattern) {
        if(getIEMetalEquiv() != null && isExistingPattern(pattern)) {
            logger.debug("Attempting to find IE Metal or MC Metal Item: " + getName() + " | " + pattern.getName());
            Item ieItem = IGApi.grabIEItemFromRegistry(pattern, getIEMetalEquiv());
            if (ieItem != null) {
                return ieItem;
            }
        }

        Item igItem = IGRegistryProvider.IG_ITEM_REGISTRY.get(IGRegistryProvider.getRegistryKey(this, pattern));
        if(igItem == null) {
            logger.error("Failed to get IG Item: " + IGRegistryProvider.getRegistryKey(this, pattern).toString());
        }

        return igItem;
    }

    private EnumMetals getIEMetalEquiv() {
        try {
            return EnumMetals.valueOf(getName().toUpperCase());
        } catch (IllegalArgumentException exception){
            return null;
        }
    };

    public Item getItem(MaterialPattern pattern, MaterialInterface secondaryMaterial) {
        return IGRegistryProvider.IG_ITEM_REGISTRY.get(IGRegistryProvider.getRegistryKey(this, secondaryMaterial, pattern));
    }

    public Item getItem(MaterialPattern pattern, MaterialBase secondaryMaterial) {
        return IGRegistryProvider.IG_ITEM_REGISTRY.get(IGRegistryProvider.getRegistryKey(this, secondaryMaterial, pattern));
    }

    public ItemStack getStack(MaterialPattern pattern) {
        if(pattern instanceof ItemFamily) {
            return new ItemStack(getItem((ItemFamily) pattern));
        }
        if(pattern instanceof BlockFamily){
            return new ItemStack(getBlock((BlockFamily) pattern));
        }

        logger.error("Immersive Geology: Internal Method 'getStack(MaterialPattern pattern)' Was incorrectly referenced");
        return ItemStack.EMPTY;
    };

    public ItemStack getStack(MaterialPattern pattern, MaterialInterface secondaryMaterial) {
        return new ItemStack(getItem(pattern, secondaryMaterial));
    }

    ;

    public ItemStack getStack(MaterialPattern pattern, MaterialBase secondaryMaterial) {
        return new ItemStack(getItem(pattern, secondaryMaterial));
    }

    ;

    public Fluid getFluid(MaterialPattern pattern) {
        Fluid f = IGRegistryProvider.IG_FLUID_REGISTRY.get(IGRegistryProvider.getRegistryKey(this, pattern));
        return f;
    }

    public Fluid getFluid(MaterialPattern pattern, MaterialInterface secondaryMaterial) {
        Fluid f = IGRegistryProvider.IG_FLUID_REGISTRY.get(IGRegistryProvider.getRegistryKey(this, secondaryMaterial, pattern));
        return f;
    }

    public FluidStack getFluidStack(MaterialPattern pattern, int amount) {
        return new FluidStack(getFluid(pattern), amount);
    }

    public FluidStack getFluidStack(MaterialPattern pattern, MaterialInterface secondaryMaterial, int amount) {
        return new FluidStack(getFluid(pattern, secondaryMaterial), amount);
    }

    public TagKey<?> getTag(MaterialPattern pattern) {

        if (pattern instanceof ItemFamily) {
            ItemFamily i = (ItemFamily) pattern;
            try {
                EnumMetals IEMetal = EnumMetals.valueOf(this.name.toUpperCase());

                IETags.MetalTags ieMetalTags = IETags.getTagsFor(IEMetal);

                switch (i) {
                    case ingot:
                        return ieMetalTags.ingot;
                    case dust:
                        return ieMetalTags.dust;
                    case nugget:
                        return ieMetalTags.nugget;
                    case plate:
                        return ieMetalTags.plate;
                } // Now we can get IE Tags via IG Methods!
            } catch (IllegalArgumentException ignored){};

            HashMap<String, TagKey<Item>> data_map = IGTags.IG_ITEM_TAGS.get(i);
            LinkedHashSet<MaterialBase> materials = new LinkedHashSet<>(Collections.singletonList(this));
            return data_map.get(IGApi.getWrapFromSet(materials));
        }

        if (pattern instanceof BlockFamily) {
            BlockFamily b = (BlockFamily) pattern;
            HashMap<String, TagKey<Block>> data_map = IGTags.IG_BLOCK_TAGS.get(b);

            try {
                EnumMetals IEMetal = EnumMetals.valueOf(this.name.toUpperCase());

                IETags.MetalTags ieMetalTags = IETags.getTagsFor(IEMetal);
                switch (b) {
                    case storage:
                        return ieMetalTags.storage;
                    case sheetmetal:
                        return ieMetalTags.sheetmetal;
                } // Now we can get IE Tags via IG Methods!
            } catch (IllegalArgumentException ignored){};


            LinkedHashSet<MaterialBase> materials = new LinkedHashSet<>(Collections.singletonList(this));
            return data_map.get(IGApi.getWrapFromSet(materials));
        }

        if (pattern instanceof FluidFamily) {
            FluidFamily f = (FluidFamily) pattern;
            HashMap<String, TagKey<Fluid>> data_map = IGTags.IG_FLUID_TAGS.get(f);
            LinkedHashSet<MaterialBase> materials = new LinkedHashSet<>(Collections.singletonList(this));
            logger.info("Attempting to get Tag from Misc Pattern:" + f.getName());
            if(f == FluidFamily.slurry) {
                String wrap = IGApi.getWrapFromSet(materials);
                logger.info("material dump: " + wrap);
                logger.info(data_map.get(wrap).toString());
                return data_map.get(IGApi.getWrapFromSet(materials));
            }
            String wrap = IGApi.getWrapFromSet(materials);
            logger.info("material dump: " + wrap);
            logger.info(data_map.get(wrap).toString());
            return data_map.get(IGApi.getWrapFromSet(materials));
        }

        //Last Attempt to

        return null;
    }

    public TagKey<?> getTag(MaterialPattern pattern, MaterialBase... materials) {
        if (pattern instanceof ItemFamily) {
            ItemFamily i = (ItemFamily) pattern;
            HashMap<String, TagKey<Item>> data_map = IGTags.IG_ITEM_TAGS.get(i);
            List<MaterialBase> materialList = new ArrayList<>(Arrays.asList(materials));

            materialList.add(this);
            LinkedHashSet<MaterialBase> matSet = new LinkedHashSet<>(materialList);
            return data_map.get(IGApi.getWrapFromSet(matSet));
        }
        if (pattern instanceof BlockFamily) {
            BlockFamily b = (BlockFamily) pattern;
            HashMap<String, TagKey<Block>> data_map = IGTags.IG_BLOCK_TAGS.get(b);
            List<MaterialBase> materialList = new ArrayList<>(Arrays.asList(materials));

            materialList.add(this);
            LinkedHashSet<MaterialBase> matSet = new LinkedHashSet<>(materialList);
            return data_map.get(IGApi.getWrapFromSet(matSet));
        }
        if (pattern instanceof FluidFamily) {
            FluidFamily f = (FluidFamily) pattern;
            HashMap<String, TagKey<Fluid>> data_map = IGTags.IG_FLUID_TAGS.get(f);
            List<MaterialBase> materialList = new ArrayList<>(Arrays.asList(materials));

            materialList.add(this);
            LinkedHashSet<MaterialBase> matSet = new LinkedHashSet<>(materialList);
            return data_map.get(IGApi.getWrapFromSet(matSet));
        }
        return null;
    }

    public int getColor(MaterialPattern p) {
        return applyColorTint.test(p) ? colorFunction.apply(p) : 0xFFFFFF;
    }

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

    public boolean isSalt() { return false; }

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

    protected boolean hasDustBlock() { return false; }

    protected abstract boolean hasOreBit();

    protected abstract boolean hasOreChunk();

    protected abstract boolean hasStoneBit();

    protected abstract boolean hasCrushedOre();

    protected abstract boolean hasMetalOxide();

    protected abstract boolean hasStoneChunk();

    protected abstract boolean hasCompoundDust();

    protected abstract boolean hasDirtyCrushedOre();

    public boolean hasExistingImplementation() {
        return false;
    }

    public ItemStack getStack(MaterialPattern pattern, int amount) {
        ItemStack stack = getStack(pattern);
        stack.setCount(amount);
        return stack;
    }

    public ItemStack getStack(MaterialPattern pattern, MaterialInterface secondaryMaterial, int amount) {
        ItemStack stack = getStack(pattern, secondaryMaterial);
        stack.setCount(amount);
        return stack;
    }

    public ItemStack getStack(MaterialPattern pattern, MaterialBase secondaryMaterial, int amount) {
        ItemStack stack = getStack(pattern, secondaryMaterial);
        stack.setCount(amount);
        return stack;
    }

    public boolean hasPattern(MaterialPattern pattern) {
        if (pattern instanceof ItemFamily) {
            ItemFamily p = (ItemFamily) pattern;
            switch (p) {
                case rod: {
                    return hasRod();
                }
                case gear: {
                    return hasGear();
                }
                case wire: {
                    return hasWire();
                }
                case ingot: {
                    return hasIngot();
                }
                case clay: {
                    return hasClay();
                }
                case dust: {
                    return hasDust();
                }
                case fuel: {
                    return hasFuel();
                }
                case slag: {
                    return hasSlag();
                }
                case plate: {
                    return hasPlate();
                }
                case nugget: {
                    return hasNugget();
                }
                case crystal: {
                    return hasCrystal();
                }
                case ore_bit: {
                    return hasOreBit();
                }
                case ore_chunk: {
                    return hasOreChunk();
                }
                case stone_bit: {
                    return hasStoneBit();
                }
                case crushed_ore: {
                    return hasCrushedOre();
                }
                case metal_oxide: {
                    return hasMetalOxide();
                }
                case stone_chunk: {
                    return hasStoneChunk();
                }
                case compound_dust: {
                    return hasCompoundDust();
                }
                case dirty_crushed_ore: {
                    return hasDirtyCrushedOre();
                }
                case bucket: {
                    return hasBucket();
                }
                case flask: {
                    return hasFlask();
                }
                case block_item: {
                    return false;
                }
            };
        }

        if (pattern instanceof BlockFamily) {
            BlockFamily p = (BlockFamily) pattern;
            switch (p) {
                case slab: {
                    return hasSlab();
                }
                case block: {
                    return hasDefaultBlock();
                }
                case geode: {
                    return hasGeodeBlock();
                }
                case ore: {
                    return hasOreBlock();
                }
                case stairs: {
                    return hasStairs();
                }
                case storage: {
                    return hasStorageBlock();
                }
                case sheetmetal: {
                    return hasSheetmetalBlock();
                }
                case dust_block: {
                    return hasDustBlock();
                }
                case machine: {
                    return isMachine();
                }
            };
        }

        if (pattern instanceof FluidFamily) {
            FluidFamily p = (FluidFamily) pattern;
            switch (p) {
                case fluid:
                    return isFluid();
                case slurry:
                    return isSlurry();
                case gas:
                    return isGas();
            }
            ;
        }

        return false;
    }

    protected boolean isGas() {return false;}

    protected boolean hasSheetmetalBlock() {return false;}

    public boolean hasFlask() {
        return false;
    }

    public boolean hasBucket() {
        return false;
    }

    protected void setupProcessingStages() {

    }

    public String getName() {
        return this.name;
    }

    public void addStage(IGProcessingStage igProcessingStage) {
        stageSet.add(igProcessingStage);
    }

    public Set<IGProcessingStage> getStages() {
        return StageProvider.get(this);
    }

    /**
     * @apiNote Wrapped version of the normal @getTag used to reduce castings
     */
    public TagKey<Item> getItemTag(MaterialPattern pattern) {
        return (TagKey<Item>) getTag(pattern);
    }

    /**
     * @apiNote Wrapped version of the normal @getTag used to reduce castings
     */
    public TagKey<Block> getBlockTag(BlockFamily pattern) {
        return (TagKey<Block>) getTag(pattern);
    }


    /**
     * @apiNote Wrapped version of the normal @getTag used to reduce castings
     */
    public TagKey<Fluid> getFluidTag(FluidFamily pattern) {
        return (TagKey<Fluid>) getTag(pattern);
    }


    /**
     * @apiNote Wrapped version of the normal @getTag used to reduce castings
     */
    public TagKey<Item> getItemTag(MaterialPattern pattern, MaterialBase... materials) {
        return (TagKey<Item>) getTag(pattern, materials);
    }

    /**
     * @apiNote Wrapped version of the normal @getTag used to reduce castings
     */
    public TagKey<Fluid> getFluidTag(FluidFamily pattern, MaterialBase... materials) {
        return (TagKey<Fluid>) getTag(pattern, materials);
    }

    /**
     * @apiNote Wrapped version of the normal @getTag used to reduce castings
     */
    public TagKey<Block> getBlockTag(BlockFamily pattern, MaterialBase... materials) {
        return (TagKey<Block>) getTag(pattern, materials);
    }

    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.CUBIC;
    }

    public abstract ResourceLocation getTextureLocation(MaterialPattern pattern);

    public boolean generateOreFor(MaterialInterface m) {
        return true;
    }

    private FeatureConfiguration oreConfiguration;

    public FeatureConfiguration getGenerationConfig() {
        return oreConfiguration;
    }

    public abstract LinkedHashSet<PeriodicTableElement.ElementProportion> getElements();

    public void setGenerationConfiguration(FeatureConfiguration config) {
        this.oreConfiguration = config;
    }

    public abstract ResourceLocation getTextureLocation(MaterialPattern pattern, int subtype);

    public abstract boolean isFluidPortable(ItemFamily bucket);

    public MaterialSourceWorld getDimension() {
        return sourceWorld;
    }

    public boolean generateForBlockFamily(BlockFamily p) {
        return p.equals(BlockFamily.ore);
    }
}