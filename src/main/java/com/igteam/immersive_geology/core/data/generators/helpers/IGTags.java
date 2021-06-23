package com.igteam.immersive_geology.core.data.generators.helpers;


import blusunrize.immersiveengineering.api.EnumMetals;
import blusunrize.immersiveengineering.api.Lib;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import java.util.HashMap;
import java.util.Map;

public class IGTags {

    private static final Map<INamedTag<Block>, INamedTag<Item>> toItemTag = new HashMap<>();

    private static final Map<MaterialEnum, MaterialTags> materials = new HashMap<>();

    static
    {
        for(MaterialEnum m : MaterialEnum.values()){
            materials.put(m, new MaterialTags(m));
        }
    }


    public static MaterialTags getTagsFor(MaterialEnum material) {
        return materials.get(material);
    }

    public static class MaterialTags {
        public final INamedTag<Item> nugget;
        public final INamedTag<Item> ingot;
        public final INamedTag<Block> metal_block;
        public final INamedTag<Item> crushed_ore;
		public final INamedTag<Fluid> fluid;
		public final INamedTag<Item> dust;


        public final INamedTag<Item> plate;
        public final INamedTag<Item> rod;
        public final INamedTag<Item> gear;
        public final INamedTag<Item> wire;
       
        private MaterialTags(MaterialEnum mat) {
            String name = mat.getMaterial().getName();
            Material material = mat.getMaterial();

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

            if(material.hasSubtype(MaterialUseType.DUST)) {
                dust = createItemWrapper(getDust(name));
            } else {
                dust = null;
            }

            if(material.hasSubtype(MaterialUseType.ORE_CRUSHED)) {
                crushed_ore = createItemWrapper(getOreClumps(name));
            } else {
                crushed_ore = null;
            }

            if(material.hasSubtype(MaterialUseType.STORAGE_BLOCK)) {
                metal_block = createBlockWrapper(getStorageBlock(name));
            } else {
                metal_block = null;
            }

            if(material.hasSubtype(MaterialUseType.FLUIDS)) {
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

    public static ResourceLocation getGem(String type)
    {
        return forgeLoc("gems/"+type);
    }

    public static ResourceLocation getStorageBlock(String type)
    {
        return forgeLoc("storage_blocks/"+type);
    }

    public static ResourceLocation getOreClumps(String type){
        return forgeLoc("clumps/"+type);
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
