package com.igteam.immersivegeology.common;

import com.igteam.immersivegeology.common.world.ImmersiveBiomeProvider;
import com.igteam.immersivegeology.common.world.biome.biomes.BadlandsBiome;
import com.igteam.immersivegeology.common.world.biome.biomes.CanyonsBiome;
import com.igteam.immersivegeology.common.world.biome.biomes.DesertBiome;
import com.igteam.immersivegeology.common.world.biome.biomes.HillsBiome;
import com.igteam.immersivegeology.common.world.biome.biomes.LakeBiome;
import com.igteam.immersivegeology.common.world.biome.biomes.LowlandsBiome;
import com.igteam.immersivegeology.common.world.biome.biomes.MountainsBiome;
import com.igteam.immersivegeology.common.world.biome.biomes.OasisBiome;
import com.igteam.immersivegeology.common.world.biome.biomes.OceanBiome;
import com.igteam.immersivegeology.common.world.biome.biomes.PlainsBiome;
import com.igteam.immersivegeology.common.world.biome.biomes.RiverBiome;
import com.igteam.immersivegeology.common.world.biome.biomes.ShoreBiome;
import com.igteam.immersivegeology.common.world.chunk.ChunkGeneratorImmersiveOverworld;
import com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.igteam.immersivegeology.ImmersiveGeology.MODID;




@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class WorldGenRegistryEvents {
	@SubscribeEvent
    public static void registerChunkGeneratorTypes(RegistryEvent.Register<ChunkGeneratorType<?, ?>> event)
    {
        event.getRegistry().registerAll(
            new ChunkGeneratorType<>(ChunkGeneratorImmersiveOverworld::new, false, ImmersiveGenerationSettings::new).setRegistryName("overworld")
        );
    }

    @SubscribeEvent
    public static void registerBiomeProviderTypes(RegistryEvent.Register<BiomeProviderType<?, ?>> event)
    {
        event.getRegistry().registerAll(
            new BiomeProviderType<>(ImmersiveBiomeProvider::new, ImmersiveGenerationSettings::new).setRegistryName("overworld")
        );
    }

    @SubscribeEvent
    public static void registerBiomes(RegistryEvent.Register<Biome> event)
    {
        event.getRegistry().registerAll(
            new OceanBiome(false).setRegistryName("ocean"),
            new OceanBiome(true).setRegistryName("deep_ocean"),
            new OceanBiome(true).setRegistryName("deep_ocean_ridge"),
            new OceanBiome(true).setRegistryName("ocean_edge"),
            
            new PlainsBiome(-4, 10).setRegistryName("plains"),
            new DesertBiome(-5, 20).setRegistryName("desert"),
            new LowlandsBiome().setRegistryName("lowlands"),
            new HillsBiome(16).setRegistryName("hills"),
            new CanyonsBiome(-5, 14).setRegistryName("low_canyons"),

            new HillsBiome(28).setRegistryName("rolling_hills"),
            new BadlandsBiome().setRegistryName("badlands"),
            new PlainsBiome(20, 30).setRegistryName("plateau"),
            new MountainsBiome(48, 28, false).setRegistryName("old_mountains"),

            new MountainsBiome(48, 56, false).setRegistryName("mountains"),
            new MountainsBiome(30, 64, true).setRegistryName("flooded_mountains"),
            new CanyonsBiome(-7, 26).setRegistryName("canyons"),

            new ShoreBiome(false).setRegistryName("shore"),
            new ShoreBiome(true).setRegistryName("stone_shore"),

            new MountainsBiome(36, 34, false).setRegistryName("mountains_edge"),
            new LakeBiome().setRegistryName("lake"),
            new OasisBiome().setRegistryName("oasis"),
            new RiverBiome().setRegistryName("river")
        );
    }

}
