package com.igteam.immersivegeology.common.world.layer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.LongFunction;
import java.util.function.Supplier;

import com.igteam.immersivegeology.common.world.biome.IGBiomes;
import com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings;
import com.igteam.immersivegeology.common.world.layer.layers.AddIslandLayer;
import com.igteam.immersivegeology.common.world.layer.layers.AddLakeLayer;
import com.igteam.immersivegeology.common.world.layer.layers.AddOasisLayer;
import com.igteam.immersivegeology.common.world.layer.layers.BiomeLayer;
import com.igteam.immersivegeology.common.world.layer.layers.EdgeBiomeLayer;
import com.igteam.immersivegeology.common.world.layer.layers.ElevationLayer;
import com.igteam.immersivegeology.common.world.layer.layers.IslandLayer;
import com.igteam.immersivegeology.common.world.layer.layers.MixRiverLayer;
import com.igteam.immersivegeology.common.world.layer.layers.OceanLayer;
import com.igteam.immersivegeology.common.world.layer.layers.RandomLayer;
import com.igteam.immersivegeology.common.world.layer.layers.RemoveOceanLayer;
import com.igteam.immersivegeology.common.world.layer.layers.RiverLayer;
import com.igteam.immersivegeology.common.world.layer.layers.ShoreLayer;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.SmoothLayer;
import net.minecraft.world.gen.layer.VoroniZoomLayer;
import net.minecraft.world.gen.layer.ZoomLayer;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;


/*
 * Author: alcatrazEscapee, TerraFirmaCraft
 * Modified for use in Immersive Geology by Muddykat
 */
public class IGLayerUtil {
	
	/* Biomes */
	public static final int OCEAN = getId(IGBiomes.OCEAN);
	public static final int DEEP_OCEAN = getId(IGBiomes.DEEP_OCEAN);
	public static final int OCEAN_EDGE = getId(IGBiomes.OCEAN_EDGE);
	public static final int DEEP_OCEAN_VOLCANIC = getId(IGBiomes.DEEP_OCEAN_VOLCANIC);
	public static final int PLAINS = getId(IGBiomes.PLAINS);
	public static final int DESERT = getId(IGBiomes.DESERT);
	public static final int OASIS = getId(IGBiomes.OASIS);
	public static final int HILLS = getId(IGBiomes.HILLS);
	public static final int LOWLANDS = getId(IGBiomes.LOWLANDS);
	public static final int LOW_CANYONS = getId(IGBiomes.LOW_CANYONS);
	public static final int ROLLING_HILLS = getId(IGBiomes.ROLLING_HILLS);
	public static final int BADLANDS = getId(IGBiomes.BADLANDS);
	public static final int PLATEAU = getId(IGBiomes.PLATEAU);
	public static final int OLD_MOUNTAINS = getId(IGBiomes.OLD_MOUNTAINS);
	public static final int MOUNTAINS = getId(IGBiomes.MOUNTAINS);
	public static final int FLOODED_MOUNTAINS = getId(IGBiomes.FLOODED_MOUNTAINS);
	public static final int LUSH_MOUNTAINS = getId(IGBiomes.LUSH_MOUNTAINS);
	public static final int CANYONS = getId(IGBiomes.CANYONS);
	public static final int SHORE = getId(IGBiomes.SHORE);
	public static final int STONE_SHORE = getId(IGBiomes.STONE_SHORE);
	public static final int MOUNTAINS_EDGE = getId(IGBiomes.MOUNTAINS_EDGE);
	public static final int LAKE = getId(IGBiomes.LAKE);
	public static final int RIVER = getId(IGBiomes.RIVER);

	public static List<IAreaFactory<LazyArea>> createOverworldBiomeLayer(long seed,
			ImmersiveGenerationSettings settings) {
		LongFunction<LazyAreaLayerContext> contextFactory = seedModifier -> new LazyAreaLayerContext(25, seed,
				seedModifier);

		IAreaFactory<LazyArea> mainLayer, riverLayer;
		int layerCount = 0;

		// Ocean / Continents

		mainLayer = new IslandLayer(settings.getIslandFrequency()).apply(contextFactory.apply(1000L));

		mainLayer = ZoomLayer.FUZZY.apply(contextFactory.apply(1001L), mainLayer);

		mainLayer = AddIslandLayer.NORMAL.apply(contextFactory.apply(1002L), mainLayer);

		mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1003L), mainLayer);

		mainLayer = AddIslandLayer.NORMAL.apply(contextFactory.apply(1004L), mainLayer);

		for (int i = 0; i < 2; i++) {
			mainLayer = AddIslandLayer.HEAVY.apply(contextFactory.apply(1005L + 2 * i), mainLayer);

			mainLayer = SmoothLayer.INSTANCE.apply(contextFactory.apply(1006L + 2 * i), mainLayer);

		}

		// Oceans and Continents => Elevation Mapping
		layerCount = 0;

		mainLayer = ElevationLayer.INSTANCE.apply(contextFactory.apply(1009L), mainLayer);

		mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1010L), mainLayer);

		// Elevation Mapping => Rivers
		layerCount = 0;

		riverLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1011L), mainLayer);

		for (int i = 0; i < 6; i++) {
			riverLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1012L + i), riverLayer);
		}

		riverLayer = RiverLayer.INSTANCE.apply(contextFactory.apply(1018L), riverLayer);

		riverLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1019L), riverLayer);

		// Elevation Mapping => Biomes

		layerCount = 0;

		mainLayer = BiomeLayer.INSTANCE.apply(contextFactory.apply(1011L), mainLayer);

		mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1012L), mainLayer);

		mainLayer = AddIslandLayer.NORMAL.apply(contextFactory.apply(1013L), mainLayer);

		mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1014L), mainLayer);

		mainLayer = RemoveOceanLayer.INSTANCE.apply(contextFactory.apply(1015L), mainLayer);

		mainLayer = OceanLayer.INSTANCE.apply(contextFactory.apply(1016L), mainLayer);

		mainLayer = EdgeBiomeLayer.INSTANCE.apply(contextFactory.apply(1017L), mainLayer);

		mainLayer = AddLakeLayer.INSTANCE.apply(contextFactory.apply(1018L), mainLayer);

		mainLayer = AddOasisLayer.INSTANCE.apply(contextFactory.apply(1018L), mainLayer);
		
		for (int i = 0; i < settings.getBiomeZoomLevel(); i++) {
			mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1019L), mainLayer);
		}

		mainLayer = ShoreLayer.INSTANCE.apply(contextFactory.apply(1023L), mainLayer);

		for (int i = 0; i < 2; i++) {
			mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1024L), mainLayer);
		}

		mainLayer = SmoothLayer.INSTANCE.apply(contextFactory.apply(1025L), mainLayer);

		mainLayer = MixRiverLayer.INSTANCE.apply(contextFactory.apply(1026L), mainLayer, riverLayer);

		IAreaFactory<LazyArea> areaFactoryActual = VoroniZoomLayer.INSTANCE.apply(contextFactory.apply(1029L),
				mainLayer);

		return Arrays.asList(mainLayer, areaFactoryActual);
	}

	public static IAreaFactory<LazyArea> createOverworldRockLayers(long seed, ImmersiveGenerationSettings settings) {
		LongFunction<LazyAreaLayerContext> contextFactory = seedModifier -> new LazyAreaLayerContext(25, seed,
				seedModifier);

		IAreaFactory<LazyArea> seedLayer, biomeLayer;
		List<IAreaFactory<LazyArea>> completedLayers = new ArrayList<>(3);
		int layerCount = 0;


		// Seed Areas
		seedLayer = RandomLayer.INSTANCE.apply(contextFactory.apply(1000L));

		for (int i = 0; i < 3; i++) {
			seedLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1001L), seedLayer);

			seedLayer = SmoothLayer.INSTANCE.apply(contextFactory.apply(1001L), seedLayer);
		}

		for (int i = 0; i < 5; i++) {
			seedLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1001L), seedLayer);
		}

		return seedLayer;
	}

	public static <A extends IArea, C extends IExtendedNoiseRandom<A>> IAreaFactory<A> repeat(
			IAreaTransformer1 transformer, int count, IAreaFactory<A> originalLayer, Supplier<C> contextSupplier) {
		IAreaFactory<A> newFactory = originalLayer;
		for (int i = 0; i < count; ++i) {
			newFactory = transformer.apply(contextSupplier.get(), newFactory);
		}
		return newFactory;
	}

	public static <A extends IArea, C extends IExtendedNoiseRandom<A>> IAreaFactory<A> repeat(
			IAreaTransformer1 transformer, int count, IAreaFactory<A> originalLayer, LongFunction<C> contextFactory,
			long seed) {
		IAreaFactory<A> newFactory = originalLayer;
		for (int i = 0; i < count; ++i) {
			newFactory = transformer.apply(contextFactory.apply(seed + i), newFactory);
		}
		return newFactory;
	}

	public static boolean isShoreCompatible(int value) {
		return value != LOWLANDS && value != LOW_CANYONS && value != CANYONS;
	}

	public static boolean isLakeCompatible(int value) {
		return isLow(value) || value == CANYONS || value == ROLLING_HILLS;
	}
	
	public static boolean isOasisCompatible(int value) {
		return value == DESERT;
	}

	public static boolean isOcean(int value) {
		return value == OCEAN || value == DEEP_OCEAN || value == DEEP_OCEAN_VOLCANIC || value == OCEAN_EDGE;
	}

	public static boolean isShallowOcean(int value) {
		return value == OCEAN && value != DEEP_OCEAN;
	}

	public static boolean isMountains(int value) {
		return value == MOUNTAINS || value == FLOODED_MOUNTAINS || value == MOUNTAINS_EDGE || value == OLD_MOUNTAINS || value == LUSH_MOUNTAINS;
	}

	public static boolean isLow(int value) {
		return value == PLAINS || value == HILLS || value == LOW_CANYONS || value == LOWLANDS;
	}

	private static int getId(Biome biome) {
		// todo: once finished with testing, inline this method
		return ((ForgeRegistry<Biome>) ForgeRegistries.BIOMES).getID(biome);
	}

	public static boolean isDeepOcean(int value) {
		// TODO Auto-generated method stub
		return value == DEEP_OCEAN || value == DEEP_OCEAN_VOLCANIC;
	}
}
