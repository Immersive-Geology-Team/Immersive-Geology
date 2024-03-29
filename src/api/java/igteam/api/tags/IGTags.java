package igteam.api.tags;

import igteam.api.materials.*;
import igteam.api.materials.data.MaterialBase;
import igteam.api.materials.data.fluid.MaterialBaseFluid;
import igteam.api.materials.data.gas.MaterialBaseGas;
import igteam.api.materials.data.metal.MaterialBaseMetal;
import igteam.api.materials.data.stone.MaterialBaseStone;
import igteam.api.materials.helper.APIMaterials;
import igteam.api.materials.pattern.FluidPattern;
import igteam.api.IGApi;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.data.slurry.variants.MaterialSlurryWrapper;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.BlockPattern;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.materials.pattern.MaterialPattern;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class IGTags {
    private static Logger logger = IGApi.getNewLogger();
    public static HashMap<MaterialPattern, HashMap<String, ITag.INamedTag<Item>>> IG_ITEM_TAGS = new HashMap<>();
    public static HashMap<BlockPattern, HashMap<String, ITag.INamedTag<Block>>> IG_BLOCK_TAGS = new HashMap<>();
    public static HashMap<FluidPattern, HashMap<String, ITag.INamedTag<Fluid>>> IG_FLUID_TAGS = new HashMap<>();

    public static HashMap<MaterialPattern, ITag.INamedTag<Item>> IG_PATTERN_GROUP_TAGS = new HashMap<>();

    public static void initialize(){
        logger.log(Level.INFO, "Immersive Geology: Initializing Tags");

        for (ItemPattern pattern : ItemPattern.values()) {
            IG_ITEM_TAGS.put(pattern, new HashMap<String, ITag.INamedTag<Item>>());

            for (MaterialInterface<MaterialBaseMetal> metal : MetalEnum.values()) {
                if (metal.hasPattern(pattern)) {
                    switch(pattern){
                        case ore_bit:
                        case ore_chunk:
                        case dirty_crushed_ore: {
                            for (StoneEnum stone : StoneEnum.values()) {
                                createWrapperForPattern(pattern,  stone.instance(), metal.instance());
                            }
                            createWrapperForPattern(pattern, metal.instance());
                            createWrapperForPatternGroup(pattern);
                        }
                        break;
                        default: {
                            createWrapperForPattern(pattern, metal.instance());
                            createWrapperForPatternGroup(pattern);
                        }
                    }
                }
            }

            for (MaterialInterface<MaterialBaseStone> stone : StoneEnum.values()) {
                if(stone.hasPattern(pattern)){
                    createWrapperForPattern(pattern, stone.instance());
                    createWrapperForPatternGroup(pattern);
                }
            }

            for (MaterialInterface<MaterialBaseMineral> mineral : MineralEnum.values()) {
                if(mineral.hasPattern(pattern)){
                    switch(pattern){
                        case ore_bit:
                        case ore_chunk:
                        case dirty_crushed_ore: {
                            for (StoneEnum stone : StoneEnum.values()) {
                                createWrapperForPattern(pattern,  stone.instance(), mineral.instance());
                            }
                            createWrapperForPattern(pattern, mineral.instance());
                        }
                        break;
                        default: {
                            createWrapperForPattern(pattern, mineral.instance());
                        }
                    }
                    createWrapperForPatternGroup(pattern);
                }
            }
        }

        for(FluidPattern pattern : FluidPattern.values()){
            IG_FLUID_TAGS.put(pattern, new HashMap<String, ITag.INamedTag<Fluid>>());
            if(pattern == FluidPattern.fluid) {
                for (MaterialInterface<MaterialBaseFluid> fluid : FluidEnum.values()) {
                    createWrapperForPattern(pattern, fluid.instance());
                }
            }

            if(pattern == FluidPattern.gas) {
                for (MaterialInterface<MaterialBaseGas> gas : GasEnum.values()) {
                    createWrapperForPattern(pattern, gas.instance());
                }
            }

            if(pattern == FluidPattern.slurry){
                logger.info("Creating Slurry Tags");
                for (SlurryEnum slurry : SlurryEnum.values()) {
                    HashSet<MaterialSlurryWrapper> slurryWrappers = slurry.getEntries();
                    for (MaterialSlurryWrapper wrapper : slurryWrappers) {
                        createWrapperForPattern(pattern, wrapper);
                    }
                }
            }
        }

        for (BlockPattern pattern : BlockPattern.values()) {
            IG_BLOCK_TAGS.put(pattern, new HashMap<String, ITag.INamedTag<Block>>());
            IG_ITEM_TAGS.put(pattern, new HashMap<String, ITag.INamedTag<Item>>()); //for BlockItem References

            for (MaterialInterface<?> genMaterial : APIMaterials.generatedMaterials()) {
                if (genMaterial.hasPattern(pattern)) {
                    if(pattern == BlockPattern.ore) {
                        for (StoneEnum stone : StoneEnum.values()) {
                            createWrapperForPattern(pattern, stone.instance(), genMaterial.instance());
                        }
                        createWrapperForPatternGroup(pattern);
                    }
                }
            }

            for (MaterialInterface<?> material : APIMaterials.all()) {
                if(pattern != BlockPattern.ore) {
                    if (material.hasPattern(pattern)) {
                        createWrapperForPattern(pattern, material.instance());
                        createWrapperForPatternGroup(pattern);
                    }
                }
            }
        }

        logger.log(Level.INFO, "Immersive Geology: Finished Initializing Tags");
    }

    private static void createWrapperForPatternGroup(MaterialPattern pattern) {
        String suffix = pattern.hasSuffix() ? "s" : "s";
        ResourceLocation loc = forgeLoc(pattern.getName() + suffix);
        IG_PATTERN_GROUP_TAGS.putIfAbsent(pattern, ItemTags.makeWrapperTag(loc.toString()));
    }

    private static void createWrapperForPattern(MaterialPattern p, MaterialBase... materials){
        if(Arrays.stream(materials).anyMatch(m -> m.hasPattern(p))) {
            if (p instanceof ItemPattern) {
                ItemPattern i = (ItemPattern) p;
                HashMap<String, ITag.INamedTag<Item>> map = IG_ITEM_TAGS.get(i);
                LinkedHashSet<MaterialBase> materialSet = new LinkedHashSet<MaterialBase>(Arrays.asList(materials));

                map.put(IGApi.getWrapFromSet(materialSet), ItemTags.makeWrapperTag(
                        p.hasSuffix() ? wrapPattern(p, materialSet, p.getSuffix()).toString()
                        : wrapPattern(p, materialSet).toString()));
                IG_ITEM_TAGS.put(i, map);
            }

            if (p instanceof BlockPattern) {
                BlockPattern b = (BlockPattern) p;
                HashMap<String, ITag.INamedTag<Block>> map = IG_BLOCK_TAGS.get(b);
                HashMap<String, ITag.INamedTag<Item>> item_map = IG_ITEM_TAGS.get(b);//We need Item Tags for our blocks for Block Item References!

                LinkedHashSet<MaterialBase> materialSet = new LinkedHashSet<MaterialBase>(Arrays.asList(materials));
                String key = IGApi.getWrapFromSet(materialSet);

                ITag.INamedTag<Block> block_value = BlockTags.makeWrapperTag(p.hasSuffix() ?
                                wrapPattern(p, materialSet, p.getSuffix()).toString()
                                : wrapPattern(p, materialSet).toString());

                ITag.INamedTag<Item> item_value = ItemTags.makeWrapperTag(
                        p.hasSuffix() ? wrapPattern(p, materialSet, p.getSuffix()).toString()
                                : wrapPattern(p, materialSet).toString());

                map.put(key, block_value);
                item_map.put(key, item_value);

                IG_ITEM_TAGS.put(b, item_map);
                IG_BLOCK_TAGS.put(b, map);
            }

            if (p instanceof FluidPattern) {
                FluidPattern m = (FluidPattern) p;
                HashMap<String, ITag.INamedTag<Fluid>> map = IG_FLUID_TAGS.get(m);

                    LinkedHashSet<MaterialBase> materialSet = new LinkedHashSet<MaterialBase>(Arrays.asList(materials));
                    String wrap = IGApi.getWrapFromSet(materialSet);
                    logger.debug("Putting wrap in Fluid Tags: " + wrap + " Pattern: " + p.getName());
                    map.put(wrap, FluidTags.makeWrapperTag(
                            p.hasSuffix() ? wrapPattern(p, materialSet, p.getSuffix()).toString()
                                    : wrapPattern(p, materialSet).toString()));

                    IG_FLUID_TAGS.put(m, map);
            }
        }
    }

    private static ResourceLocation wrapPattern(MaterialPattern pattern, Set<MaterialBase> matSet, String suffix) {
        StringJoiner matSetName = new StringJoiner("_");

        for(MaterialBase m : matSet) {
            matSetName.add(m.getName());
        }

        ResourceLocation loc = forgeLoc(pattern.getName() + suffix + "/" + matSetName.toString());
        return loc;
    }

    private static ResourceLocation wrapPattern(MaterialPattern pattern, Set<MaterialBase> m) {
        return wrapPattern(pattern, m, "s");
    }

    private static ResourceLocation forgeLoc(String path)
    {
        return new ResourceLocation("forge", path);
    }
}
