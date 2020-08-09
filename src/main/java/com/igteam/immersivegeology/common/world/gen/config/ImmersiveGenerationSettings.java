package com.igteam.immersivegeology.common.world.gen.config;

import net.minecraft.world.biome.provider.IBiomeProviderSettings;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.storage.WorldInfo;

//should only extend GenerationSettings not OverworldGenSettings
public class ImmersiveGenerationSettings extends GenerationSettings implements IBiomeProviderSettings
{
	public static int SEA_LEVEL = 96;

	public static boolean isFlatBedrock() //change this later
	{
		return true; // todo: config
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

	public void setWorldInfo(WorldInfo worldInfo)
	{
		this.worldInfo = worldInfo;
	}
}