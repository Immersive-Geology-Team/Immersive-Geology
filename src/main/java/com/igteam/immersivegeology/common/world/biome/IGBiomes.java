package com.igteam.immersivegeology.common.world.biome;

import static com.igteam.immersivegeology.ImmersiveGeology.MODID;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import com.igteam.immersivegeology.common.world.help.Helpers;

import net.minecraft.util.LazyLoadBase;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(value = MODID)
public class IGBiomes {
	// Aquatic biomes
    public static final IGBiome OCEAN = Helpers.getNull(); // Ocean biome found near continents.
    public static final IGBiome OCEAN_EDGE = Helpers.getNull(); 
    public static final IGBiome DEEP_OCEAN = Helpers.getNull();
    public static final IGBiome DEEP_OCEAN_VOLCANIC = Helpers.getNull();
	
    //low height biomes
    public static final IGBiome PLAINS = Helpers.getNull(); 
    public static final IGBiome DESERT = Helpers.getNull();
    public static final IGBiome HILLS = Helpers.getNull(); 
    public static final IGBiome LOWLANDS = Helpers.getNull(); 
    public static final IGBiome LOW_CANYONS = Helpers.getNull(); 
    
    public static final IGBiome ROLLING_HILLS = Helpers.getNull(); 
    public static final IGBiome BADLANDS = Helpers.getNull(); 
    public static final IGBiome PLATEAU = Helpers.getNull(); 
    public static final IGBiome OLD_MOUNTAINS = Helpers.getNull(); 
    
    public static final IGBiome MOUNTAINS = Helpers.getNull(); 
    public static final IGBiome FLOODED_MOUNTAINS = Helpers.getNull(); 
    public static final IGBiome CANYONS = Helpers.getNull(); 
   
    public static final IGBiome SHORE = Helpers.getNull(); 
    public static final IGBiome STONE_SHORE = Helpers.getNull(); 
    
    public static final IGBiome MOUNTAINS_EDGE = Helpers.getNull(); 
    public static final IGBiome LAKE = Helpers.getNull(); 
    public static final IGBiome OASIS = Helpers.getNull(); 
    public static final IGBiome RIVER = Helpers.getNull(); 
    
    private static final LazyLoadBase<Set<IGBiome>> BIOMES = new LazyLoadBase<>(() -> {
        Set<IGBiome> values = new HashSet<>();
        for (IGBiome biome : Arrays.asList(OCEAN, OCEAN_EDGE, DEEP_OCEAN, DEEP_OCEAN_VOLCANIC, PLAINS, DESERT, HILLS, LOWLANDS, LOW_CANYONS, ROLLING_HILLS, BADLANDS, PLATEAU, OLD_MOUNTAINS, MOUNTAINS, FLOODED_MOUNTAINS, CANYONS, SHORE, STONE_SHORE, MOUNTAINS_EDGE, LAKE, OASIS, RIVER))
        {
            if (biome == null)
            {
                throw new IllegalStateException("Unable to locate biome! Did an object holder not copy correctly, or was a registry entry changed?");
            }
            values.add(biome);
        }
        return values;
    });
    
    @Nonnull
    public static Set<IGBiome> getBiomes()
    {
        return BIOMES.getValue();
    }
}
