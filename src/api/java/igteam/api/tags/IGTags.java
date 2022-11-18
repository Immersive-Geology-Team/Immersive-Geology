package igteam.api.tags;

import igteam.api.materials.*;
import igteam.api.materials.data.MaterialBase;
import igteam.api.materials.data.fluid.MaterialBaseFluid;
import igteam.api.materials.data.gas.MaterialBaseGas;
import igteam.api.materials.data.metal.MaterialBaseMetal;
import igteam.api.materials.data.stone.MaterialBaseStone;
import igteam.api.materials.helper.APIMaterials;
import igteam.api.materials.pattern.FluidFamily;
import igteam.api.IGApi;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.data.slurry.variants.MaterialSlurryWrapper;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.BlockFamily;
import igteam.api.materials.pattern.ItemFamily;
import igteam.api.materials.pattern.MaterialPattern;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class IGTags {
    private static Logger logger = IGApi.getNewLogger();
    public static HashMap<MaterialPattern, HashMap<String, TagKey<Item>>> IG_ITEM_TAGS = new HashMap<>();
    public static HashMap<BlockFamily, HashMap<String, TagKey<Block>>> IG_BLOCK_TAGS = new HashMap<>();
    public static HashMap<FluidFamily, HashMap<String, TagKey<Fluid>>> IG_FLUID_TAGS = new HashMap<>();

    public static HashMap<MaterialPattern, TagKey<Item>> IG_PATTERN_GROUP_TAGS = new HashMap<>();

    public static void initialize(){
        logger.log(Level.INFO, "Immersive Geology: Initializing Tags");

        for (ItemFamily pattern : ItemFamily.values()) {
            IG_ITEM_TAGS.put(pattern, new HashMap<String, TagKey<Item>>());

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

        for(FluidFamily pattern : FluidFamily.values()){
            IG_FLUID_TAGS.put(pattern, new HashMap<String, TagKey<Fluid>>());
            if(pattern == FluidFamily.fluid) {
                for (MaterialInterface<MaterialBaseFluid> fluid : FluidEnum.values()) {
                    createWrapperForPattern(pattern, fluid.instance());
                }
            }

            if(pattern == FluidFamily.gas) {
                for (MaterialInterface<MaterialBaseGas> gas : GasEnum.values()) {
                    createWrapperForPattern(pattern, gas.instance());
                }
            }

            if(pattern == FluidFamily.slurry){
                logger.info("Creating Slurry Tags");
                for (SlurryEnum slurry : SlurryEnum.values()) {
                    HashSet<MaterialSlurryWrapper> slurryWrappers = slurry.getEntries();
                    for (MaterialSlurryWrapper wrapper : slurryWrappers) {
                        createWrapperForPattern(pattern, wrapper);
                    }
                }
            }
        }

        for (BlockFamily pattern : BlockFamily.values()) {
            IG_BLOCK_TAGS.put(pattern, new HashMap<String, TagKey<Block>>());
            IG_ITEM_TAGS.put(pattern, new HashMap<String, TagKey<Item>>()); //for BlockItem References

            for (MaterialInterface<?> genMaterial : APIMaterials.generatedMaterials()) {
                if (genMaterial.hasPattern(pattern)) {
                    if(pattern == BlockFamily.ore) {
                        for (StoneEnum stone : StoneEnum.values()) {
                            createWrapperForPattern(pattern, stone.instance(), genMaterial.instance());
                        }
                        createWrapperForPatternGroup(pattern);
                    }
                }
            }

            for (MaterialInterface<?> material : APIMaterials.all()) {
                if(pattern != BlockFamily.ore) {
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
        IG_PATTERN_GROUP_TAGS.putIfAbsent(pattern, ItemTags.create(loc));
    }

    private static void createWrapperForPattern(MaterialPattern p, MaterialBase... materials){
        if(Arrays.stream(materials).anyMatch(m -> m.hasPattern(p))) {
            if (p instanceof ItemFamily) {
                ItemFamily i = (ItemFamily) p;
                HashMap<String, TagKey<Item>> map = IG_ITEM_TAGS.get(i);
                LinkedHashSet<MaterialBase> materialSet = new LinkedHashSet<MaterialBase>(Arrays.asList(materials));

                map.put(IGApi.getWrapFromSet(materialSet), ItemTags.create(
                        p.hasSuffix() ? wrapPattern(p, materialSet, p.getSuffix())
                        : wrapPattern(p, materialSet)));
                IG_ITEM_TAGS.put(i, map);
            }

            if (p instanceof BlockFamily) {
                BlockFamily b = (BlockFamily) p;
                HashMap<String, TagKey<Block>> map = IG_BLOCK_TAGS.get(b);
                HashMap<String, TagKey<Item>> item_map = IG_ITEM_TAGS.get(b);//We need Item Tags for our blocks for Block Item References!

                LinkedHashSet<MaterialBase> materialSet = new LinkedHashSet<MaterialBase>(Arrays.asList(materials));
                String key = IGApi.getWrapFromSet(materialSet);

                TagKey<Block> block_value = BlockTags.create(p.hasSuffix() ?
                                wrapPattern(p, materialSet, p.getSuffix())
                                : wrapPattern(p, materialSet));

                TagKey<Item> item_value = ItemTags.create(
                        p.hasSuffix() ? wrapPattern(p, materialSet, p.getSuffix())
                                : wrapPattern(p, materialSet));

                map.put(key, block_value);
                item_map.put(key, item_value);

                IG_ITEM_TAGS.put(b, item_map);
                IG_BLOCK_TAGS.put(b, map);
            }

            if (p instanceof FluidFamily) {
                FluidFamily m = (FluidFamily) p;
                HashMap<String, TagKey<Fluid>> map = IG_FLUID_TAGS.get(m);

                    LinkedHashSet<MaterialBase> materialSet = new LinkedHashSet<MaterialBase>(Arrays.asList(materials));
                    String wrap = IGApi.getWrapFromSet(materialSet);
                    logger.debug("Putting wrap in Fluid Tags: " + wrap + " Pattern: " + p.getName());
                    map.put(wrap, FluidTags.create(
                            p.hasSuffix() ? wrapPattern(p, materialSet, p.getSuffix())
                                    : wrapPattern(p, materialSet)));

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
