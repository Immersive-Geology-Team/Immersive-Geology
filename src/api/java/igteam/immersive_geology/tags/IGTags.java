package igteam.immersive_geology.tags;

import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.materials.MetalEnum;
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

import java.util.HashMap;
import java.util.Map;

public class IGTags {
    private static Logger logger = IGApi.getNewLogger();

    public static HashMap<ItemPattern, HashMap<MaterialBase, Named<Item>>> IG_ITEM_TAGS = new HashMap<>();
    public static HashMap<BlockPattern, HashMap<MaterialBase, Named<Block>>> IG_BLOCK_TAGS = new HashMap<>();
    public static HashMap<MiscPattern, HashMap<MaterialBase, Named<Fluid>>> IG_FLUID_TAGS = new HashMap<>();

    public static void initialize(){
        logger.log(Level.INFO, "Initializing Tags");
        for (ItemPattern pattern : ItemPattern.values()) {
            IG_ITEM_TAGS.put(pattern, new HashMap<MaterialBase, Named<Item>>());
            for (MaterialInterface metal : MetalEnum.values()) {
                createWrapperForPattern(pattern, metal);
            }
        }

        for (BlockPattern pattern : BlockPattern.values()) {
            IG_BLOCK_TAGS.put(pattern, new HashMap<MaterialBase, Named<Block>>());
            for (MaterialInterface metal : MetalEnum.values()) {
                createWrapperForPattern(pattern, metal);
            }
        }
    }

    private static void createWrapperForPattern(MaterialPattern p, MaterialInterface m){
        if(m.hasPattern(p)) {
            if (p instanceof ItemPattern) {
                HashMap<MaterialBase, Named<Item>> map = IG_ITEM_TAGS.get((ItemPattern) p);

                map.put(m.get(), ItemTags.bind(
                        p.hasSuffix() ? wrapPattern(p, m, p.getSuffix()).toString()
                        : wrapPattern(p, m).toString()));

                IG_ITEM_TAGS.put((ItemPattern) p, map);
            }

            if (p instanceof BlockPattern b) {
                HashMap<MaterialBase, Named<Block>> map = IG_BLOCK_TAGS.get(b);
                map.put(m.get(), BlockTags.bind(wrapPattern(b, m).toString()));
                IG_BLOCK_TAGS.put(b, map);
            }

            if (p instanceof MiscPattern f) {
                HashMap<MaterialBase, Named<Fluid>> map = IG_FLUID_TAGS.get(f);
                map.put(m.get(), FluidTags.bind(wrapPattern(f, m).toString()));
                IG_FLUID_TAGS.put(f, map);
            }
        }
    }

    private static ResourceLocation wrapPattern(MaterialPattern pattern, MaterialInterface m, String suffix) {
        return forgeLoc(pattern.getName() + suffix + "/" + m.getName());
    }

    private static ResourceLocation wrapPattern(MaterialPattern pattern, MaterialInterface m) {
        return wrapPattern(pattern, m, "s");
    }

    private static ResourceLocation forgeLoc(String path)
    {
        return new ResourceLocation("forge", path);
    }
}
