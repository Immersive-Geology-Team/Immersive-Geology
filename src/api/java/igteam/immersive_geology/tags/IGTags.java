package igteam.immersive_geology.tags;

import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.materials.*;
import igteam.immersive_geology.materials.data.MaterialBase;
import igteam.immersive_geology.materials.data.fluid.MaterialBaseFluid;
import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.data.mineral.MaterialBaseMineral;
import igteam.immersive_geology.materials.data.slurry.variants.MaterialSlurryWrapper;
import igteam.immersive_geology.materials.data.stone.MaterialBaseStone;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.materials.pattern.MiscPattern;
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
    public static HashMap<ItemPattern, HashMap<String, ITag.INamedTag<Item>>> IG_ITEM_TAGS = new HashMap<>();
    public static HashMap<BlockPattern, HashMap<String, ITag.INamedTag<Block>>> IG_BLOCK_TAGS = new HashMap<>();
    public static HashMap<MiscPattern, HashMap<String, ITag.INamedTag<Fluid>>> IG_FLUID_TAGS = new HashMap<>();

    public static void initialize(){
        logger.log(Level.INFO, "Initializing Tags");
        for (ItemPattern pattern : ItemPattern.values()) {
            IG_ITEM_TAGS.put(pattern, new HashMap<String, ITag.INamedTag<Item>>());

            for (MaterialInterface<MaterialBaseMetal> metal : MetalEnum.values()) {
                if (metal.hasPattern(pattern)) {
                    switch(pattern){
                        case ore_bit:
                        case ore_chunk:
                        case dirty_crushed_ore: {
                            for (StoneEnum stone : StoneEnum.values()) {
                                createWrapperForPattern(pattern,  stone.get(), metal.get());
                            }
                        }
                        break;
                        default: {
                            createWrapperForPattern(pattern, metal.get());
                        }
                    }
                }
            }

            for (MaterialInterface<MaterialBaseStone> stone : StoneEnum.values()) {
                if(stone.hasPattern(pattern)){
                    createWrapperForPattern(pattern, stone.get());
                }
            }

            for (MaterialInterface<MaterialBaseMineral> mineral : MineralEnum.values()) {
                if(mineral.hasPattern(pattern)){
                    switch(pattern){
                        case ore_bit:
                        case ore_chunk:
                        case dirty_crushed_ore: {
                            for (StoneEnum stone : StoneEnum.values()) {
                                createWrapperForPattern(pattern,  stone.get(), mineral.get());
                            }
                        }
                        break;
                        default: {
                            createWrapperForPattern(pattern, mineral.get());
                        }
                    }
                }
            }
        }

        for(MiscPattern pattern : MiscPattern.values()){
            IG_FLUID_TAGS.put(pattern, new HashMap<String, ITag.INamedTag<Fluid>>());
            if(pattern == MiscPattern.fluid) {
                for (MaterialInterface<MaterialBaseFluid> fluid : FluidEnum.values()) {
                    createWrapperForPattern(pattern, fluid.get());
                }
            }
            if(pattern == MiscPattern.slurry){
                for (SlurryEnum slurry : SlurryEnum.values()) {
                    HashSet<MaterialSlurryWrapper> slurryWrappers = slurry.getEntries();
                    for (MaterialSlurryWrapper wrapper : slurryWrappers) {
                        FluidEnum fluid = wrapper.getFluidBase();
                        MaterialInterface<? extends MaterialBase> soluteMaterial = wrapper.getSoluteMaterial();

                        createWrapperForPattern(pattern, fluid.get(), soluteMaterial.get());
                    }
                }
            }
        }

        for (BlockPattern pattern : BlockPattern.values()) {
            IG_BLOCK_TAGS.put(pattern, new HashMap<String, ITag.INamedTag<Block>>());
            for (MaterialInterface<MaterialBaseMetal> metal : MetalEnum.values()) {
                if (metal.hasPattern(pattern)) {
                    switch(pattern){
                        case ore: {
                            for (StoneEnum stone : StoneEnum.values()) {
                                createWrapperForPattern(pattern,  stone.get(), metal.get());
                            }
                        }
                        break;
                        default :
                        {
                            createWrapperForPattern(pattern, metal.get());
                        }
                    }
                }
            }
            for (MaterialInterface<MaterialBaseStone> stone : StoneEnum.values()) {
                if(stone.hasPattern(pattern)){
                    createWrapperForPattern(pattern, stone.get());
                }
            }
        }
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
                LinkedHashSet<MaterialBase> materialSet = new LinkedHashSet<MaterialBase>(Arrays.asList(materials));
                map.put(IGApi.getWrapFromSet(materialSet), BlockTags.makeWrapperTag(
                        p.hasSuffix() ? wrapPattern(p, materialSet, p.getSuffix()).toString()
                                : wrapPattern(p, materialSet).toString()));

                IG_BLOCK_TAGS.put(b, map);
            }

            if (p instanceof MiscPattern) {
                MiscPattern m = (MiscPattern) p;
                HashMap<String, ITag.INamedTag<Fluid>> map = IG_FLUID_TAGS.get(m);
                LinkedHashSet<MaterialBase> materialSet = new LinkedHashSet<MaterialBase>(Arrays.asList(materials));

                map.put(IGApi.getWrapFromSet(materialSet), FluidTags.makeWrapperTag(
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
