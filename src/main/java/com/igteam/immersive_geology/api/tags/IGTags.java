package com.igteam.immersive_geology.api.tags;


import blusunrize.immersiveengineering.api.Lib;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.fluid.FluidEnum;
import com.igteam.immersive_geology.api.materials.fluid.SlurryEnum;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMineralBase;
import com.igteam.immersive_geology.api.materials.material_data.fluids.slurry.MaterialSlurryWrapper;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class IGTags {

    private static final Map<INamedTag<Block>, INamedTag<Item>> toItemTag = new HashMap<>();

    private static final Map<Material, MaterialTags> materials = new HashMap<>();

    static
    {
        for(MaterialEnum m : MaterialEnum.values()){
            materials.put(m.getMaterial(), new MaterialTags(m.getMaterial()));
        }
        for (FluidEnum f : FluidEnum.values()) {
            materials.put(f.getMaterial(), new MaterialTags(f.getMaterial()));
        }
        for(SlurryEnum s : SlurryEnum.values()){
            for(MaterialSlurryWrapper slurry : s.getEntries()){
                materials.put(slurry, new MaterialTags(slurry));
            }
        }
    }

    public static MaterialTags getTagsFor(Material material) {
        return materials.get(material);
    }

    public static class MaterialTags {
        public final INamedTag<Item> nugget;
        public final INamedTag<Item> ingot;
        public final INamedTag<Item> ore_crushed;
        public final INamedTag<Item> dirty_ore_crushed;
        public final INamedTag<Item> dust;
        public final INamedTag<Item> plate;
        public final INamedTag<Item> rod;
        public final INamedTag<Item> gear;
        public final INamedTag<Item> wire;
        public final INamedTag<Item> brick;

        public final INamedTag<Fluid> fluid;
        public final INamedTag<Block> metal_block;

        private MaterialTags(Material material) {
            String name = material.getName();

            if(material.hasSubtype(MaterialUseType.NUGGET)) {
                nugget = createItemWrapper(getNugget(name));
            } else {
                nugget = null;
            }

            if(material.hasSubtype(MaterialUseType.INGOT)) {
                ingot = createItemWrapper(getIngot(name));
            } else {
                ingot = null;
            }

            if(material.hasSubtype(MaterialUseType.BRICK)){
                brick = createItemWrapper(getBrick(name));
            } else {
                brick = null;
            }

            if(material.hasSubtype(MaterialUseType.DUST)) {
                dust = createItemWrapper(getDust(name));
            } else {
                dust = null;
            }

            if(material.hasSubtype(MaterialUseType.DIRTY_CRUSHED_ORE)) {
                dirty_ore_crushed = createItemWrapper(getDirtyOre(name));
            } else {
                dirty_ore_crushed = null;
            }

            if(material.hasSubtype(MaterialUseType.CRUSHED_ORE)) {
                ore_crushed = createItemWrapper(getOreClumps(name));
            } else {
                ore_crushed = null;
            }

            if(material.hasSubtype(MaterialUseType.STORAGE_BLOCK)) {
                metal_block = createBlockWrapper(getStorageBlock(name));
            } else {
                metal_block = null;
            }

            if(material.hasSubtype(MaterialUseType.FLUIDS) || material.hasSubtype(MaterialUseType.SLURRY)) {
                fluid = createFluidWrapper(forgeLoc(name));
            } else {
                fluid = null;
            }

			if (material.hasSubtype(MaterialUseType.PLATE))
            {
                plate = createItemWrapper(getPlate(name));
            } else {
                plate = null;
            }

            if (material.hasSubtype(MaterialUseType.ROD))
            {
                rod = createItemWrapper(getRod(name));
            } else {
                rod = null;
            }
            if (material.hasSubtype(MaterialUseType.GEAR))
            {
                gear = createItemWrapper(getGear(name));
            } else {
                gear = null;
            }
            if (material.hasSubtype(MaterialUseType.WIRE))
            {
                wire = createItemWrapper(getWire(name));
            } else {
                wire = null;
            }
        }
    }

    public static INamedTag<Block> createBlockWrapper(ResourceLocation name)
    {
        return BlockTags.makeWrapperTag(name.toString());
    }

    public static INamedTag<Fluid> createFluidWrapper(ResourceLocation name){
        return FluidTags.makeWrapperTag(name.toString());
    }

    public static ResourceLocation getFluid(String type) {
        return forgeLoc("fluids/"+type);
    }

    private static INamedTag<Block> createBlockTag(ResourceLocation name)
    {
        INamedTag<Block> blockTag = createBlockWrapper(name);
        toItemTag.put(blockTag, createItemWrapper(name));
        return blockTag;
    }

    public static INamedTag<Item> createItemWrapper(ResourceLocation name)
    {
        return ItemTags.makeWrapperTag(name.toString());
    }

    private static ResourceLocation forgeLoc(String path)
    {
        return new ResourceLocation("forge", path);
    }

    public static ResourceLocation getOre(String type)
    {
        return forgeLoc("ores/"+type);
    }

    public static ResourceLocation getNugget(String type)
    {
        return forgeLoc("nuggets/"+type);
    }

    public static ResourceLocation getIngot(String type)
    {
        return forgeLoc("ingots/"+type);
    }


    public static ResourceLocation getBrick(String type)
    {
        return forgeLoc("bricks/"+type);
    }

    public static ResourceLocation getGem(String type)
    {
        return forgeLoc("crystals/"+type);
    }

    public static ResourceLocation getStorageBlock(String type)
    {
        return forgeLoc("storage_blocks/"+type);
    }

    public static ResourceLocation getOreClumps(String type){
        return forgeLoc("clean_chunk/"+type);
    }

    public static ResourceLocation getDirtyOre(String type){
        return forgeLoc("dirty_chunk/"+type);
    }

    public static ResourceLocation getDust(String type)
    {
        return forgeLoc("dusts/"+type);
    }

    public static ResourceLocation getPlate(String type)
    {
        return forgeLoc("plates/"+type);
    }

    public static ResourceLocation getRod(String type)
    {
        return forgeLoc("rods/"+type);
    }

    public static ResourceLocation getGear(String type)
    {
        return forgeLoc("gears/"+type);
    }

    public static ResourceLocation getWire(String type)
    {
        return forgeLoc("wires/"+type);
    }

    public static ResourceLocation getSheetmetalBlock(String type)
    {
        return forgeLoc("sheetmetals/"+type);
    }

    private static ResourceLocation rl(String path)
    {
        return new ResourceLocation(Lib.MODID, path);
    }
}
