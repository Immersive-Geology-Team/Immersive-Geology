package com.igteam.immersivegeology.common.world.gen.config;

import net.minecraft.world.biome.provider.IBiomeProviderSettings;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.storage.WorldInfo;

public class ImmersiveGenerationSettings extends GenerationSettings implements IBiomeProviderSettings
{
    public boolean isFlatBedrock()
    {
        return true; // todo: config
    }

    private WorldInfo worldInfo;

    public int getIslandFrequency()
    {
        return 6; // todo: config
    }

    public int getBiomeZoomLevel()
    {
        return 4; // todo: config
    }

    public WorldInfo getWorldInfo()
    {
        return worldInfo;
    }

    public void setWorldInfo(WorldInfo worldInfo)
    {
        this.worldInfo = worldInfo;
    }
}