package igteam.immersive_geology.tags;

import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.MineralEnum;
import igteam.immersive_geology.materials.StoneEnum;
import igteam.immersive_geology.materials.data.MaterialBase;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.materials.pattern.MiscPattern;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.Tag.Named;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.stringtemplate.v4.misc.Misc;

import java.util.*;

public class IGTags {
    private static Logger logger = IGApi.getNewLogger();

    //TODO - Refactor this to allow Multiple Material References, perhaps use String inplace of a MaterialBase Reference and
    // make a multi material name reference?

    public static HashMap<ItemPattern, HashMap<String, Named<Item>>> IG_ITEM_TAGS = new HashMap<>();
    public static HashMap<BlockPattern, HashMap<String, Named<Block>>> IG_BLOCK_TAGS = new HashMap<>();
    public static HashMap<MiscPattern, HashMap<String, Named<Fluid>>> IG_FLUID_TAGS = new HashMap<>();

    public static void initialize(){
        logger.log(Level.INFO, "Initializing Tags");
        for (ItemPattern pattern : ItemPattern.values()) {
            IG_ITEM_TAGS.put(pattern, new HashMap<String, Named<Item>>());

            for (MaterialInterface metal : MetalEnum.values()) {
                if (metal.hasPattern(pattern)) {
                    switch(pattern){
                        case ore_bit, ore_chunk, dirty_crushed_ore -> {
                            for (StoneEnum stone : StoneEnum.values()) {
                                createWrapperForPattern(pattern,  stone.get(), metal.get());
                            }
                        }
                        default -> {
                            createWrapperForPattern(pattern, metal.get());
                        }
                    }
                }
            }

            for (MaterialInterface stone : StoneEnum.values()) {
                if(stone.hasPattern(pattern)){
                    createWrapperForPattern(pattern, stone.get());
                }
            }

            for (MaterialInterface mineral : MineralEnum.values()) {
                if(mineral.hasPattern(pattern)){
                    switch(pattern){
                        case ore_bit, ore_chunk, dirty_crushed_ore -> {
                            for (StoneEnum stone : StoneEnum.values()) {
                                createWrapperForPattern(pattern,  stone.get(), mineral.get());
                            }
                        }
                        default -> {
                            createWrapperForPattern(pattern, mineral.get());
                        }
                    }
                }
            }
        }

        for (BlockPattern pattern : BlockPattern.values()) {
            IG_BLOCK_TAGS.put(pattern, new HashMap<String, Named<Block>>());
            for (MaterialInterface metal : MetalEnum.values()) {
                if (metal.hasPattern(pattern)) {
                    switch(pattern){
                        case ore -> {
                            for (StoneEnum stone : StoneEnum.values()) {
                                createWrapperForPattern(pattern,  stone.get(), metal.get());
                            }
                        }
                        default -> {
                            createWrapperForPattern(pattern, metal.get());
                        }
                    }
                }
            }
            for (MaterialInterface stone : StoneEnum.values()) {
                if(stone.hasPattern(pattern)){
                    createWrapperForPattern(pattern, stone.get());
                }
            }
        }
    }

    private static void createWrapperForPattern(MaterialPattern p, MaterialBase... materials){
        if(Arrays.stream(materials).anyMatch(m -> m.hasPattern(p))) {
            if (p instanceof ItemPattern i) {
                HashMap<String, Named<Item>> map = IG_ITEM_TAGS.get(i);
                LinkedHashSet<MaterialBase> materialSet = new LinkedHashSet<MaterialBase>(List.of(materials));

                map.put(IGApi.getWrapFromSet(materialSet), ItemTags.bind(
                        p.hasSuffix() ? wrapPattern(p, materialSet, p.getSuffix()).toString()
                        : wrapPattern(p, materialSet).toString()));
                IG_ITEM_TAGS.put(i, map);
            }

            if (p instanceof BlockPattern b) {
                HashMap<String, Named<Block>> map = IG_BLOCK_TAGS.get(b);
                LinkedHashSet<MaterialBase> materialSet = new LinkedHashSet<MaterialBase>(List.of(materials));
                map.put(IGApi.getWrapFromSet(materialSet), BlockTags.bind(
                        p.hasSuffix() ? wrapPattern(p, materialSet, p.getSuffix()).toString()
                                : wrapPattern(p, materialSet).toString()));

                IG_BLOCK_TAGS.put(b, map);
            }

            if (p instanceof MiscPattern m) {
                HashMap<String, Named<Fluid>> map = IG_FLUID_TAGS.get(m);
                LinkedHashSet<MaterialBase> materialSet = new LinkedHashSet<MaterialBase>(List.of(materials));

                map.put(IGApi.getWrapFromSet(materialSet), FluidTags.bind(
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
