package com.igteam.immersivegeology.common.world.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

public class BiomeFactory {
	   private final LazyArea lazyArea;

	    public static IGBiome getBiome(int id)
	    {
	    	Biome biome = ((ForgeRegistry<Biome>) ForgeRegistries.BIOMES).getValue(id);
	    	
	    	return (IGBiome) biome;
	    	
	    }

	    public IGBiome[] getBiomes(int startX, int startZ, int xSize, int zSize)
	    {
	    	IGBiome[] biomes = new IGBiome[xSize * zSize];

	        for (int x = 0; x < xSize; ++x)
	        {
	            for (int z = 0; z < zSize; ++z)
	            {
	                int value = lazyArea.getValue(startX + x, startZ + z);
	                biomes[x + z * xSize] = getBiome(value);
	            }
	        }
	        return biomes;
	    }

	    public IGBiome getBiome(int x, int z)
	    {
	        return getBiome(lazyArea.getValue(x, z));
	    }

	    public BiomeFactory(IAreaFactory<LazyArea> lazyAreaFactoryIn)
	    {
	        this.lazyArea = lazyAreaFactoryIn.make();
	    }
}
