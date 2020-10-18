package com.igteam.immersivegeology.common.world.gen.config;

import com.igteam.immersivegeology.ImmersiveGeology;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.provider.IBiomeProviderSettings;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.NetherGenSettings;
import net.minecraft.world.storage.WorldInfo;

public class ImmersiveNetherGenSettings extends GenerationSettings implements IBiomeProviderSettings {
    public static int SEA_LEVEL = 70;

    public static boolean isFlatBedrock() //change this later
    {
        return true; // todo: config
    }

    public int getBedrockFloorHeight() {
        return 0;
    }

    public int getBedrockRoofHeight() {
        return 127;
    }

    private WorldInfo worldInfo;

    public int getIslandFrequency()
    {
        return 3; // todo: config //Low island amount, more landmass
    }

    public int getBiomeZoomLevel()
    {
        return 4; // todo: config
    }

    public WorldInfo getWorldInfo()
    {
        return worldInfo;
    }

    @Override
    public BlockState getDefaultFluid() {
        return Blocks.LAVA.getDefaultState();
    }



    public ImmersiveNetherGenSettings setWorldInfo(WorldInfo worldInfo)
    {
        this.worldInfo = worldInfo;
        return this;
    }
}
