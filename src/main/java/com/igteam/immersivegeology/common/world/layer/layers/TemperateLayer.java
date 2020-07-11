package com.igteam.immersivegeology.common.world.layer.layers;

import static com.igteam.immersivegeology.common.world.layer.IGLayerUtil.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.igteam.immersivegeology.api.util.IGMathHelper;
import com.igteam.immersivegeology.common.world.layer.IGLayerUtil;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum TemperateLayer implements ICastleTransformer {
	NORMAL(5), HEAVY(12);

	private final int oceanWeight;

	TemperateLayer(int oceanWeight) {
		this.oceanWeight = oceanWeight;
	}

	public static final int[] FROZEN_BIOMES = new int[] { ARCTIC_DESERT };
	public static final int[] COLD_BIOMES = new int[] {LAKE, OLD_MOUNTAINS, MOUNTAINS, MOUNTAINS_EDGE};
	public static final int[] TEMPERATE_BIOMES = new int[] {LAKE, PLAINS, HILLS, FLOODED_MOUNTAINS, PLATEAU, LOWLANDS, LUSH_MOUNTAINS }; 
	public static final int[] WARM_BIOMES = new int[] {LOW_CANYONS, BADLANDS};
	public static final int[] HOT_BIOMES = new int[] { CANYONS, DESERT, BADLANDS};

	public static List<Integer> FROZEN = new ArrayList<Integer>(
			Arrays.stream(TemperateLayer.FROZEN_BIOMES).boxed().collect(Collectors.toList()));
	public static List<Integer> COLD = new ArrayList<Integer>(
			Arrays.stream(TemperateLayer.COLD_BIOMES).boxed().collect(Collectors.toList()));
	public static List<Integer> TEMPERATE = new ArrayList<Integer>(
			Arrays.stream(TemperateLayer.TEMPERATE_BIOMES).boxed().collect(Collectors.toList()));
	public static List<Integer> WARM = new ArrayList<Integer>(
			Arrays.stream(TemperateLayer.WARM_BIOMES).boxed().collect(Collectors.toList()));
	public static List<Integer> HOT = new ArrayList<Integer>(
			Arrays.stream(TemperateLayer.HOT_BIOMES).boxed().collect(Collectors.toList()));

	public static List<Integer> mid = new ArrayList<Integer>(
			Arrays.stream(BiomeLayer.MID_BIOMES).boxed().collect(Collectors.toList()));
	public static List<Integer> low = new ArrayList<Integer>(
			Arrays.stream(BiomeLayer.LOW_BIOMES).boxed().collect(Collectors.toList()));
	public static List<Integer> high = new ArrayList<Integer>(
			Arrays.stream(BiomeLayer.HIGH_BIOMES).boxed().collect(Collectors.toList()));

	private boolean canPlaceNearTemperateBiomes(int value) {
		List<Integer> tempCompat = Stream.of(TEMPERATE, WARM, COLD).flatMap(x -> x.stream())
				.collect(Collectors.toList());
		return tempCompat.contains(value);
	}

	private boolean canPlaceNearFrozenBiomes(int value) {
		List<Integer> tempCompat = Stream.of(FROZEN, COLD).flatMap(x -> x.stream()).collect(Collectors.toList());
		return tempCompat.contains(value);
	}

	private boolean canPlaceNearHotBiomes(int value) {
		List<Integer> tempCompat = Stream.of(HOT, WARM).flatMap(x -> x.stream()).collect(Collectors.toList());
		return tempCompat.contains(value);
	}

	private boolean isBiomeHigh(int value) {
		return high.contains(value);
	}

	private boolean isBiomeMid(int value) {
		return mid.contains(value);
	}

	private boolean isBiomeLow(int value) {
		return low.contains(value);
	}

	private boolean canPlaceBiomeNearHigh(int value) {
		List<Integer> highMid = Stream.of(high, mid).flatMap(x -> x.stream()).collect(Collectors.toList());
		return highMid.contains(value);
	}

	private boolean canPlaceBiomeNearLow(int value) {
		List<Integer> lowMid = Stream.of(low, mid).flatMap(x -> x.stream()).collect(Collectors.toList());
		return lowMid.contains(value);
	}
	
	private boolean canPlaceBiomeNearMid(int value) {
		List<Integer> lowMid = Stream.of(low, high).flatMap(x -> x.stream()).collect(Collectors.toList());
		return lowMid.contains(value);
	}

	public int apply(INoiseRandom context, int top, int right, int bottom, int left, int center) {
		if (!IGLayerUtil.isOcean(center)) {
			if (canPlaceNearTemperateBiomes(top) && canPlaceNearTemperateBiomes(bottom)
					&& canPlaceNearTemperateBiomes(right) && canPlaceNearTemperateBiomes(left)) {

				if (isBiomeLow(center)) {
					if (canPlaceBiomeNearLow(top) && canPlaceBiomeNearLow(top) && canPlaceBiomeNearLow(bottom)
							&& canPlaceBiomeNearLow(left)) {
						return center;
					} else {
						List<Integer> ColdAndWarm = IGMathHelper.union(COLD, WARM);
						List<Integer> temperateCompat = IGMathHelper.union(ColdAndWarm, TEMPERATE);
						List<Integer> lowTemperateCompat = IGMathHelper.intersection(temperateCompat, low);

						center = lowTemperateCompat.get(context.random(lowTemperateCompat.size()));
						return center;
					}
				}

				if (isBiomeMid(center)) {
					if (canPlaceBiomeNearMid(top) && canPlaceBiomeNearMid(top) && canPlaceBiomeNearMid(bottom)
							&& canPlaceBiomeNearMid(left)) {
						
						return center;
					
					} else {
						List<Integer> ColdAndWarm = IGMathHelper.union(COLD, WARM);
						List<Integer> temperateCompat = IGMathHelper.union(ColdAndWarm, TEMPERATE);
						List<Integer> midTemperateCompat = IGMathHelper.intersection(temperateCompat, mid);

						center = midTemperateCompat.get(context.random(midTemperateCompat.size()));
						return center;
					}
				}

				if (isBiomeHigh(center)) {
					if (canPlaceBiomeNearHigh(top) && canPlaceBiomeNearHigh(top) && canPlaceBiomeNearHigh(bottom)
							&& canPlaceBiomeNearHigh(left)) {
						return center;
					} else {
						List<Integer> ColdAndWarm = IGMathHelper.union(COLD, WARM);
						List<Integer> temperateCompat = IGMathHelper.union(ColdAndWarm, TEMPERATE);
						List<Integer> highTemperateCompat = IGMathHelper.intersection(temperateCompat, high);

						center = highTemperateCompat.get(context.random(highTemperateCompat.size()));
						return center;
					}
				}

				return center;

			} else if (canPlaceNearFrozenBiomes(top) && canPlaceNearFrozenBiomes(bottom)
					&& canPlaceNearFrozenBiomes(right) && canPlaceNearFrozenBiomes(left)) {

				if (isBiomeLow(center)) {
					if (canPlaceBiomeNearLow(top) && canPlaceBiomeNearLow(top) && canPlaceBiomeNearLow(bottom)
							&& canPlaceBiomeNearLow(left)) {
						return center;
					} else {
						List<Integer> ColdAndFrozen = IGMathHelper.union(COLD, FROZEN);
						List<Integer> lowFrozenCompat = IGMathHelper.intersection(ColdAndFrozen, low);

						center = lowFrozenCompat.get(context.random(lowFrozenCompat.size()));
						return center;
					}
				}

				if (isBiomeMid(center)) {
					return center;
				}

				if (isBiomeHigh(center)) {
					if (canPlaceBiomeNearHigh(top) && canPlaceBiomeNearHigh(top) && canPlaceBiomeNearHigh(bottom)
							&& canPlaceBiomeNearHigh(left)) {
						return center;
					} else {
						List<Integer> ColdAndFrozen = IGMathHelper.union(COLD, FROZEN);
						List<Integer> highFrozenCompat = IGMathHelper.intersection(ColdAndFrozen, high);

						center = highFrozenCompat.get(context.random(highFrozenCompat.size()));
						return center;
					}
				}

				return center;

			} else if (canPlaceNearHotBiomes(top) && canPlaceNearHotBiomes(bottom) && canPlaceNearHotBiomes(right)
					&& canPlaceNearHotBiomes(left)) {
				if (isBiomeLow(center)) {
					if (canPlaceBiomeNearLow(top) && canPlaceBiomeNearLow(top) && canPlaceBiomeNearLow(bottom)
							&& canPlaceBiomeNearLow(left)) {
						return center;
					} else {
						List<Integer> HotAndWarm = IGMathHelper.union(HOT, WARM);
						List<Integer> lowHotCompat = IGMathHelper.intersection(HotAndWarm, low);

						center = lowHotCompat.get(context.random(lowHotCompat.size()));
						return center;
					}
				}

				if (isBiomeMid(center)) {
					return center;
				}

				if (isBiomeHigh(center)) {
					if (canPlaceBiomeNearHigh(top) && canPlaceBiomeNearHigh(top) && canPlaceBiomeNearHigh(bottom)
							&& canPlaceBiomeNearHigh(left)) {
						return center;
					} else {
						List<Integer> HotAndWarm = IGMathHelper.union(HOT, WARM);
						List<Integer> highHotCompat = IGMathHelper.intersection(HotAndWarm, high);

						center = highHotCompat.get(context.random(highHotCompat.size()));
						return center;
					}
				}

				return center;
			} else {
				return center;
			}
		} else {
			return center;
		}
	}
}