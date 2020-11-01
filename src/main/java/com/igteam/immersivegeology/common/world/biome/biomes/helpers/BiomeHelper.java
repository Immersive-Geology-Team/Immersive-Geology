package com.igteam.immersivegeology.common.world.biome.biomes.helpers;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;

public class BiomeHelper {


    public static float getOceanRainfall(OceanType type){
        switch(type){
            case DEEP:
                return 15F;
            default:
                return 10F;
        }
    }

    public static float getForestTemperature(ForestType type){
        switch (type){
            case SNOWY:
                return 0.05F;
            case SWEDISH:
                return 0.5F;
            default:
                return 0.6F;
        }
    }

    public static boolean isMountainSnowPeaked(MountainType type){
        switch(type){
            case DESERT:
            case LUSH:
            case FROZEN:
                return false;
            default:
                return true;
        }
    }

    public static boolean isMountainCustomPeaked(MountainType type){
        switch(type){
            case DESERT:
            case FROZEN:
                return true;
            default:
                return false;
        }
    }

    public static BlockState getCustomMountainPeak(MountainType type){
        switch(type){
            case DESERT:
                return Blocks.SAND.getDefaultState();
            case FROZEN:
                return Blocks.BLUE_ICE.getDefaultState();
            default:
                return Blocks.AIR.getDefaultState();
        }
    }

    public static Biome.RainType getMountainRain(MountainType type){
        switch(type){
            case DESERT:
                return Biome.RainType.NONE;
            case FROZEN:
                return Biome.RainType.SNOW;
            default:
                return Biome.RainType.RAIN;
        }
    }

    public static Biome.RainType getForestRain(ForestType type){
        switch(type){
            case SNOWY:
                return Biome.RainType.SNOW;
            default:
                return Biome.RainType.RAIN;
        }
    }

    public static int getOceanColor(OceanType type){
        switch(type){
            case DEEP:
                return 0x324E72;
            case DEEP_FROZEN:
                return 0x3750089;
            default:
                return 0x04770A5;
        }
    }
}