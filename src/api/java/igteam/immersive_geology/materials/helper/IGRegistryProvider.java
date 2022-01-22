package igteam.immersive_geology.materials.helper;

import igteam.immersive_geology.materials.data.MaterialBase;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.HashMap;

public class IGRegistryProvider {
    public static final HashMap<ResourceLocation, Item> IG_ITEM_REGISTRY = new HashMap<>();
    public static final HashMap<ResourceLocation, Block> IG_BLOCK_REGISTRY = new HashMap<>();
    public static final HashMap<ResourceLocation, Fluid> IG_FLUID_REGISTRY = new HashMap<>();

    public static ResourceLocation getRegistryKey(MaterialInterface material, MaterialPattern pattern){
        return new ResourceLocation("immersive_geology",   pattern.getName() + "_" + material.getName());
    }

    public static ResourceLocation getRegistryKey(MaterialBase material, MaterialPattern pattern){
        return new ResourceLocation("immersive_geology",   pattern.getName() + "_" + material.getName());
    }

    public static ResourceLocation getRegistryKey(MaterialInterface material, MaterialInterface secondary, MaterialPattern pattern){
        return new ResourceLocation("immersive_geology",  pattern.getName() + "_" + material.getName() + "_" + secondary.getName() );
    }

    public static ResourceLocation getRegistryKey(MaterialBase material, MaterialInterface secondary, MaterialPattern pattern){
        return new ResourceLocation("immersive_geology",  pattern.getName() + "_" + material.getName() + "_" + secondary.getName() );
    }

    public static ResourceLocation getRegistryKey(MaterialBase material, MaterialBase secondary, MaterialPattern pattern){
        return new ResourceLocation("immersive_geology",  pattern.getName() + "_" + material.getName() + "_" + secondary.getName() );
    }
}
