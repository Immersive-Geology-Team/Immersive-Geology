package com.igteam.immersivegeology.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;

import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;

public final class IGBlocks
{
    private IGBlocks()
    {
    }

    public static Map<Block, SlabBlock> toSlab = new IdentityHashMap<>();

    public static final class StoneDecoration
    {

    }

    public static final class Multiblocks
    {

    }

    public static final class Metals
    {
        /*public static Map<EnumMetals, Block> ores = new EnumMap<>(EnumMetals.class); TODO add EnumMetals
        public static Map<EnumMetals, Block> storage = new EnumMap<>(EnumMetals.class);
        public static Map<EnumMetals, Block> sheetmetal = new EnumMap<>(EnumMetals.class);*/
    }

    public static final class WoodenDecoration
    {

    }

    public static final class MetalDecoration
    {

    }

    public static final class Misc
    {

    }

}
