package com.igteam.immersive_geology.core.config;

import net.minecraftforge.common.ForgeConfigSpec;

import static net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class IGOreConfig {
    public IntValue veinSize;
    public IntValue minY;
    public IntValue maxY;
    public IntValue veinsPerChunk;


    public IGOreConfig(ForgeConfigSpec.Builder builder, String name, int defSize, int defMinY, int defMaxY, int defNumPerChunk)
    {
        builder.push(name);
        veinSize = builder.comment("The maximum size of a vein. set to 0 to disable generation").defineInRange("vein_size", defSize, 0, Integer.MAX_VALUE);
        minY = builder.comment("The minimum Y Coordinate this ore can spawn at").defineInRange("min_y", defMinY, Integer.MIN_VALUE, Integer.MAX_VALUE);
        maxY = builder.comment("The maximum Y Coordinate this ore can spawn at").defineInRange("max_y", defMaxY, Integer.MIN_VALUE, Integer.MAX_VALUE);
        veinsPerChunk = builder.comment("The average number of veins per chunk").defineInRange("avg_veins_per_chunk", defNumPerChunk, 0, Integer.MAX_VALUE);
        builder.pop();
    }
}
