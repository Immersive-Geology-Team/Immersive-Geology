package igteam.immersive_geology.config;

import net.minecraftforge.common.ForgeConfigSpec;

import static net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class IGOreConfig {
    public IntValue veinSizeMin, veinSizeMax;
    public IntValue minY;
    public IntValue maxY;
    public IntValue veinsPerChunk;
    public IntValue spawnChance;


    public IGOreConfig(ForgeConfigSpec.Builder builder, String name, int minSize, int maxSize, int defMinY, int defMaxY, int defNumPerChunk, int chance)
    {
        builder.push(name);
        veinSizeMin = builder.comment("The maximum size of a vein. set to 0 to disable generation").defineInRange("vein_size_min", minSize, 0, Integer.MAX_VALUE);
        veinSizeMax = builder.comment("The minimum size of a vein. set to 0 to disable generation").defineInRange("vein_size_max", maxSize, 0, Integer.MAX_VALUE);
        minY = builder.comment("The minimum Y Coordinate this ore can spawn at").defineInRange("min_y", defMinY, Integer.MIN_VALUE, Integer.MAX_VALUE);
        maxY = builder.comment("The maximum Y Coordinate this ore can spawn at").defineInRange("max_y", defMaxY, Integer.MIN_VALUE, Integer.MAX_VALUE);
        veinsPerChunk = builder.comment("The average number of veins per chunk").defineInRange("avg_veins_per_chunk", defNumPerChunk, 0, Integer.MAX_VALUE);
        spawnChance = builder.comment("How likely it is to spawn per attempt (chance / 10000) Chance is normally set to between 50-100").defineInRange("spawn_chance", chance, 0, 9999);
        builder.pop();
    }
}
