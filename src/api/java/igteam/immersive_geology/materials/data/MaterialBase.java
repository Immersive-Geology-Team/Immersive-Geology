package igteam.immersive_geology.materials.data;

import blusunrize.immersiveengineering.api.EnumMetals;
import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.common.blocks.multiblocks.StaticTemplateManager;
import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.config.IGOreConfig;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.main.IGRegistryProvider;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.helper.MaterialSourceWorld;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.FluidPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.StageProvider;
import igteam.immersive_geology.tags.IGTags;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static igteam.immersive_geology.main.IGRegistryProvider.getRegistryKey;

public abstract class MaterialBase {
    private Set<IGProcessingStage> stageSet = new HashSet<>();
    protected Function<MaterialPattern, Integer> colorFunction;
    protected Predicate<MaterialPattern> applyColorTint;

    protected MaterialSourceWorld sourceWorld;

    public MaterialBase(String name) {
        this.name = name;
        initializeColorMap((p) -> (p == ItemPattern.ingot ? 0xFF0000 :  0xFFFFFF));
        initializeColorTint((p) -> true); //default will be overridden later on in ClientProxy
        sourceWorld = MaterialSourceWorld.overworld;
    }

    public static boolean isExistingPattern(MaterialPattern pattern){
        return (pattern == ItemPattern.ingot || pattern == ItemPattern.dust || pattern == ItemPattern.plate || pattern == ItemPattern.nugget);
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

    public Block getBlock(BlockPattern p) {
        return IGRegistryProvider.IG_BLOCK_REGISTRY.get(getRegistryKey(this, p));
    };

    public Block getBlock(MaterialPattern p, MaterialInterface secondaryMaterial) {
        return IGRegistryProvider.IG_BLOCK_REGISTRY.get(getRegistryKey(this, secondaryMaterial, p));
    }

    public Block getBlock(MaterialPattern p, MaterialBase secondaryMaterial) {
        return IGRegistryProvider.IG_BLOCK_REGISTRY.get(getRegistryKey(this, secondaryMaterial, p));
    }

    public Item getItem(MaterialPattern pattern) {
        if(getIEMetalEquiv() != null && isExistingPattern(pattern)) {
            logger.debug("Attempting to find IE Metal or MC Metal Item: " + getName() + " | " + pattern.getName());
            Item ieItem = IGApi.grabIEItemFromRegistry(pattern, getIEMetalEquiv());
            if (ieItem != null) {
                return ieItem;
            }
        }

        Item igItem = IGRegistryProvider.IG_ITEM_REGISTRY.get(getRegistryKey(this, pattern));
        if(igItem == null) {
            logger.error("Failed to get IG Item: " + getRegistryKey(this, pattern).toString());
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
        return IGRegistryProvider.IG_ITEM_REGISTRY.get(getRegistryKey(this, secondaryMaterial, pattern));
    }

    public Item getItem(MaterialPattern pattern, MaterialBase secondaryMaterial) {
        return IGRegistryProvider.IG_ITEM_REGISTRY.get(getRegistryKey(this, secondaryMaterial, pattern));
    }

    public ItemStack getStack(MaterialPattern pattern) {
        if(pattern instanceof ItemPattern) {
            return new ItemStack(getItem((ItemPattern) pattern));
        }
        if(pattern instanceof BlockPattern){
            return new ItemStack(getBlock((BlockPattern) pattern));
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
        Fluid f = IGRegistryProvider.IG_FLUID_REGISTRY.get(getRegistryKey(this, pattern));
        return f;
    }

    public Fluid getFluid(MaterialPattern pattern, MaterialInterface secondaryMaterial) {
        Fluid f = IGRegistryProvider.IG_FLUID_REGISTRY.get(getRegistryKey(this, secondaryMaterial, pattern));
        return f;
    }

    public FluidStack getFluidStack(MaterialPattern pattern, int amount) {
        return new FluidStack(getFluid(pattern), amount);
    }

    public FluidStack getFluidStack(MaterialPattern pattern, MaterialInterface secondaryMaterial, int amount) {
        return new FluidStack(getFluid(pattern, secondaryMaterial), amount);
    }

    public ITag.INamedTag<?> getTag(MaterialPattern pattern) {

        if (pattern instanceof ItemPattern) {
            ItemPattern i = (ItemPattern) pattern;
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

            HashMap<String, ITag.INamedTag<Item>> data_map = IGTags.IG_ITEM_TAGS.get(i);
            LinkedHashSet<MaterialBase> materials = new LinkedHashSet<>(Collections.singletonList(this));
            return data_map.get(IGApi.getWrapFromSet(materials));
        }

        if (pattern instanceof BlockPattern) {
            BlockPattern b = (BlockPattern) pattern;
            HashMap<String, ITag.INamedTag<Block>> data_map = IGTags.IG_BLOCK_TAGS.get(b);

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

        if (pattern instanceof FluidPattern) {
            FluidPattern f = (FluidPattern) pattern;
            HashMap<String, ITag.INamedTag<Fluid>> data_map = IGTags.IG_FLUID_TAGS.get(f);
            LinkedHashSet<MaterialBase> materials = new LinkedHashSet<>(Collections.singletonList(this));
            logger.info("Attempting to get Tag from Misc Pattern:" + f.getName());
            if(f == FluidPattern.slurry) {
                String wrap = IGApi.getWrapFromSet(materials);
                logger.info("material dump: " + wrap);
                logger.info(data_map.get(wrap).getName());
                return data_map.get(IGApi.getWrapFromSet(materials));
            }
            String wrap = IGApi.getWrapFromSet(materials);
            logger.info("material dump: " + wrap);
            logger.info(data_map.get(wrap).getName());
            return data_map.get(IGApi.getWrapFromSet(materials));
        }

        //Last Attempt to

        return null;
    }

    public ITag.INamedTag<?> getTag(MaterialPattern pattern, MaterialBase... materials) {
        if (pattern instanceof ItemPattern) {
            ItemPattern i = (ItemPattern) pattern;
            HashMap<String, ITag.INamedTag<Item>> data_map = IGTags.IG_ITEM_TAGS.get(i);
            List<MaterialBase> materialList = new ArrayList<>(Arrays.asList(materials));

            materialList.add(this);
            LinkedHashSet<MaterialBase> matSet = new LinkedHashSet<>(materialList);
            return data_map.get(IGApi.getWrapFromSet(matSet));
        }
        if (pattern instanceof BlockPattern) {
            BlockPattern b = (BlockPattern) pattern;
            HashMap<String, ITag.INamedTag<Block>> data_map = IGTags.IG_BLOCK_TAGS.get(b);
            List<MaterialBase> materialList = new ArrayList<>(Arrays.asList(materials));

            materialList.add(this);
            LinkedHashSet<MaterialBase> matSet = new LinkedHashSet<>(materialList);
            return data_map.get(IGApi.getWrapFromSet(matSet));
        }
        if (pattern instanceof FluidPattern) {
            FluidPattern f = (FluidPattern) pattern;
            HashMap<String, ITag.INamedTag<Fluid>> data_map = IGTags.IG_FLUID_TAGS.get(f);
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
        if (pattern instanceof ItemPattern) {
            ItemPattern p = (ItemPattern) pattern;
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
            }
            ;
        }

        if (pattern instanceof BlockPattern) {
            BlockPattern p = (BlockPattern) pattern;
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
                case machine: {
                    return isMachine();
                }
            }
            ;
        }

        if (pattern instanceof FluidPattern) {
            FluidPattern p = (FluidPattern) pattern;
            switch (p) {
                case fluid:
                    return isFluid();
                case slurry:
                    return isSlurry();
            }
            ;
        }

        return false;
    }

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
    public ITag.INamedTag<Item> getItemTag(MaterialPattern pattern) {
        return (ITag.INamedTag<Item>) getTag(pattern);
    }

    /**
     * @apiNote Wrapped version of the normal @getTag used to reduce castings
     */
    public ITag.INamedTag<Block> getBlockTag(BlockPattern pattern) {
        return (ITag.INamedTag<Block>) getTag(pattern);
    }


    /**
     * @apiNote Wrapped version of the normal @getTag used to reduce castings
     */
    public ITag.INamedTag<Fluid> getFluidTag(FluidPattern pattern) {
        return (ITag.INamedTag<Fluid>) getTag(pattern);
    }


    /**
     * @apiNote Wrapped version of the normal @getTag used to reduce castings
     */
    public ITag.INamedTag<Item> getItemTag(MaterialPattern pattern, MaterialBase... materials) {
        return (ITag.INamedTag<Item>) getTag(pattern, materials);
    }

    /**
     * @apiNote Wrapped version of the normal @getTag used to reduce castings
     */
    public ITag.INamedTag<Fluid> getFluidTag(FluidPattern pattern, MaterialBase... materials) {
        return (ITag.INamedTag<Fluid>) getTag(pattern, materials);
    }

    /**
     * @apiNote Wrapped version of the normal @getTag used to reduce castings
     */
    public ITag.INamedTag<Block> getBlockTag(BlockPattern pattern, MaterialBase... materials) {
        return (ITag.INamedTag<Block>) getTag(pattern, materials);
    }

    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.CUBIC;
    }

    public abstract ResourceLocation getTextureLocation(MaterialPattern pattern);

    public boolean generateOreFor(MaterialInterface m) {
        return true;
    }

    private IGOreConfig oreConfiguration;

    public IGOreConfig getGenerationConfig() {
        return oreConfiguration;
    }

    public abstract LinkedHashSet<PeriodicTableElement.ElementProportion> getElements();

    public void setGenerationConfiguration(IGOreConfig config) {
        this.oreConfiguration = config;
    }

    public abstract ResourceLocation getTextureLocation(MaterialPattern pattern, int subtype);

    public abstract boolean isFluidPortable(ItemPattern bucket);

    public MaterialSourceWorld getDimension() {
        return sourceWorld;
    }
}