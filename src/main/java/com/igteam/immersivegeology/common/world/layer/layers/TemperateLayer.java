package com.igteam.immersivegeology.common.world.layer.layers;

import com.igteam.immersivegeology.api.util.IGMathHelper;
import com.igteam.immersivegeology.common.world.layer.IGLayerUtil;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IBishopTransformer;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.igteam.immersivegeology.common.world.layer.IGLayerUtil.*;

public enum TemperateLayer implements ICastleTransformer, IBishopTransformer
{
	CASTLE, BISHOP;

	public static final int[] FROZEN_BIOMES = new int[]{ARCTIC_DESERT, GLACIER, FROZEN_MOUNTAINS};
	public static final int[] COLD_BIOMES = new int[]{OLD_MOUNTAINS, MOUNTAINS, LOWLANDS};
	public static final int[] TEMPERATE_BIOMES = new int[]{PLAINS, ROLLING_HILLS, HILLS, FLOODED_MOUNTAINS, PLATEAU};
	public static final int[] WARM_BIOMES = new int[]{LOW_CANYONS, BADLANDS, LUSH_MOUNTAINS};
	public static final int[] HOT_BIOMES = new int[]{CANYONS, DESERT, BADLANDS, MOUNTAIN_DUNES};

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

	
	public static boolean isBiomeTemperate(int value)
	{
		return TEMPERATE.contains(value);
	}
	
	public static boolean isBiomeCold(int value)
	{
		return COLD.contains(value);
	}
	
	public static boolean isBiomeWarm(int value)
	{
		return WARM.contains(value);
	}
	
	public static boolean isBiomeHot(int value)
	{
		return HOT.contains(value);
	}
	
	public static boolean isBiomeFrozen(int value)
	{
		return FROZEN.contains(value);
	}
	
	private boolean canPlaceNearTemperateBiomes(int value)
	{
		List<Integer> tempCompat = Stream.of(TEMPERATE, WARM, COLD).flatMap(x -> x.stream())
				.collect(Collectors.toList());
		return tempCompat.contains(value);
	}

	private boolean canPlaceNearFrozenBiomes(int value)
	{
		List<Integer> tempCompat = Stream.of(FROZEN, COLD).flatMap(x -> x.stream()).collect(Collectors.toList());
		return tempCompat.contains(value);
	}
	
	private boolean canPlaceNearColdBiomes(int value)
	{
		List<Integer> tempCompat = Stream.of(FROZEN, COLD, TEMPERATE).flatMap(x -> x.stream()).collect(Collectors.toList());
		return tempCompat.contains(value);
	}
	
	private boolean canPlaceNearWarmBiomes(int value)
	{
		List<Integer> tempCompat = Stream.of(WARM, HOT, TEMPERATE).flatMap(x -> x.stream()).collect(Collectors.toList());
		return tempCompat.contains(value); 
	}

	private boolean canPlaceNearHotBiomes(int value)
	{
		List<Integer> tempCompat = Stream.of(HOT, WARM).flatMap(x -> x.stream()).collect(Collectors.toList());
		return tempCompat.contains(value);
	}

	private boolean isBiomeHigh(int value)
	{
		return high.contains(value);
	}

	private boolean isBiomeMid(int value)
	{
		return mid.contains(value);
	}

	private boolean isBiomeLow(int value)
	{
		return low.contains(value);
	}

	private boolean canPlaceBiomeNearHigh(int value)
	{
		List<Integer> highMid = Stream.of(high, mid).flatMap(x -> x.stream()).collect(Collectors.toList());
		return highMid.contains(value);
	}

	private boolean canPlaceBiomeNearLow(int value)
	{
		List<Integer> lowMid = Stream.of(low, mid).flatMap(x -> x.stream()).collect(Collectors.toList());
		return lowMid.contains(value);
	}

	private boolean canPlaceBiomeNearMid(int value)
	{
		List<Integer> lowMid = Stream.of(low, high).flatMap(x -> x.stream()).collect(Collectors.toList());
		return lowMid.contains(value);
	}

	public int apply(INoiseRandom context, int top, int right, int bottom, int left, int center)
	{
		List<Integer> lowAndFrozen = IGMathHelper.intersection(FROZEN, low);
		List<Integer> midAndFROZEN = IGMathHelper.intersection(FROZEN, mid);
		List<Integer> highAndFROZEN = IGMathHelper.intersection(FROZEN, high);
		
		List<Integer> lowAndCold = IGMathHelper.intersection(COLD, low);
		List<Integer> midAndCold = IGMathHelper.intersection(COLD, mid);
		List<Integer> highAndCold = IGMathHelper.intersection(COLD, high);
		
		List<Integer> lowAndWarm = IGMathHelper.intersection(WARM, low);
		List<Integer> midAndWarm = IGMathHelper.intersection(WARM, mid);
		List<Integer> highAndWarm = IGMathHelper.intersection(WARM, high);

		List<Integer> lowAndTemperate = IGMathHelper.intersection(TEMPERATE, low);
		List<Integer> midAndTemperate = IGMathHelper.intersection(TEMPERATE, mid);
		List<Integer> highAndTemperate = IGMathHelper.intersection(TEMPERATE, high);
		
		List<Integer> lowAndHot = IGMathHelper.intersection(HOT, low);
		List<Integer> midAndHot = IGMathHelper.intersection(HOT, mid);
		List<Integer> highAndHot = IGMathHelper.intersection(HOT, high);
		
		List<Integer> lowMidCold = IGMathHelper.union(lowAndCold, midAndCold);
		List<Integer> lowMidWarm = IGMathHelper.union(lowAndWarm, midAndWarm);
	
		List<Integer> lowAndFrozenHot = IGMathHelper.intersection(lowAndHot, lowAndFrozen);
		
		List<Integer> lowMidColdWarm = IGMathHelper.union(lowMidCold, lowMidWarm);
	
		
		if(!IGLayerUtil.isOcean(center))
		{
			if(isBiomeLow(center)) {
				if(isBiomeFrozen(center)) {
					if(canPlaceNearFrozenBiomes(top) && canPlaceNearFrozenBiomes(bottom) && canPlaceNearFrozenBiomes(left) && canPlaceNearFrozenBiomes(right)) {
						return center;
 					} else {
 						return lowAndCold.get(context.random(lowAndCold.size()));
 					}
				}
				if(isBiomeCold(center)) {
					if(canPlaceNearColdBiomes(top) && canPlaceNearColdBiomes(bottom) && canPlaceNearColdBiomes(left) && canPlaceNearColdBiomes(right)) {
						return center;
 					} else {
 						return lowAndTemperate.get(context.random(lowAndTemperate.size()));
 					}
				}	
				if(isBiomeTemperate(center)) {
					if(canPlaceNearTemperateBiomes(top) && canPlaceNearTemperateBiomes(bottom) &&
					   canPlaceNearTemperateBiomes(left) && canPlaceNearTemperateBiomes(right)) {
						return center;
					} else {
						//As Temperate is a Transformation Biome we need a detailed Check
						int hotBiomes = 0;
						int frozenBiomes = 0;
						//check for Hot Biomes
						if(isBiomeHot(top)) {
							hotBiomes++;
						}
						if(isBiomeHot(bottom)) {
							hotBiomes++;
						}
						if(isBiomeHot(left)) {
							hotBiomes++;
						}
						if(isBiomeHot(right)) {
							hotBiomes++;
						}
						//check for frozen Biomes
						if(isBiomeFrozen(top)) {
							frozenBiomes++;
						}
						if(isBiomeFrozen(bottom)) {
							frozenBiomes++;
						}
						if(isBiomeFrozen(left)) {
							frozenBiomes++;
						}
						if(isBiomeFrozen(right)) {
							frozenBiomes++;
						}
						
						int compareBiomes = hotBiomes - frozenBiomes;
						
						if(compareBiomes == 0) {
							return lowAndTemperate.get(context.random(lowAndTemperate.size()));
						}
						
						if(compareBiomes < 0) {
							return lowAndCold.get(context.random(lowAndCold.size()));
						}
						
						if(compareBiomes > 0) {
							return lowAndWarm.get(context.random(lowAndWarm.size()));
						}		
					}
				}
				if(isBiomeWarm(center)) {
					if(canPlaceNearWarmBiomes(top) && canPlaceNearWarmBiomes(bottom) &&
					   canPlaceNearWarmBiomes(left) && canPlaceNearWarmBiomes(right)) {
						return center;
					} else {
						return lowAndTemperate.get(context.random(lowAndTemperate.size()));
					}
				}
				if(isBiomeHot(center)) {
					if(canPlaceNearHotBiomes(top) && canPlaceNearHotBiomes(bottom) &&
					   canPlaceNearHotBiomes(left) && canPlaceNearHotBiomes(right)) {
						return center;
					} else {
						return lowAndWarm.get(context.random(lowAndWarm.size()));
					}
				}
			}
			
			if(isBiomeMid(center)) {
				if(isBiomeFrozen(center)) {
					if(canPlaceNearFrozenBiomes(top) && canPlaceNearFrozenBiomes(bottom) && canPlaceNearFrozenBiomes(left) && canPlaceNearFrozenBiomes(right)) {
						return center;
 					} else {
 						return midAndCold.get(context.random(midAndCold.size()));
 					}
				}
				if(isBiomeCold(center)) {
					if(canPlaceNearColdBiomes(top) && canPlaceNearColdBiomes(bottom) && canPlaceNearColdBiomes(left) && canPlaceNearColdBiomes(right)) {
						return center;
 					} else {
 						return midAndTemperate.get(context.random(midAndTemperate.size()));
 					}
				}	
				if(isBiomeTemperate(center)) {
					if(canPlaceNearTemperateBiomes(top) && canPlaceNearTemperateBiomes(bottom) &&
					   canPlaceNearTemperateBiomes(left) && canPlaceNearTemperateBiomes(right)) {
						return center;
					} else {
						//As Temperate is a Transformation Biome we need a detailed Check
						int hotBiomes = 0;
						int frozenBiomes = 0;
						//check for Hot Biomes
						if(isBiomeHot(top)) {
							hotBiomes++;
						}
						if(isBiomeHot(bottom)) {
							hotBiomes++;
						}
						if(isBiomeHot(left)) {
							hotBiomes++;
						}
						if(isBiomeHot(right)) {
							hotBiomes++;
						}
						//check for frozen Biomes
						if(isBiomeFrozen(top)) {
							frozenBiomes++;
						}
						if(isBiomeFrozen(bottom)) {
							frozenBiomes++;
						}
						if(isBiomeFrozen(left)) {
							frozenBiomes++;
						}
						if(isBiomeFrozen(right)) {
							frozenBiomes++;
						}
						
						int compareBiomes = hotBiomes - frozenBiomes;
						
						if(compareBiomes == 0) {
							return midAndTemperate.get(context.random(midAndTemperate.size()));
						}
						
						if(compareBiomes < 0) {
							return midAndCold.get(context.random(midAndCold.size()));
						}
						
						if(compareBiomes > 0) {
							return midAndWarm.get(context.random(midAndWarm.size()));
						}		
					}
				}
				if(isBiomeWarm(center)) {
					if(canPlaceNearWarmBiomes(top) && canPlaceNearWarmBiomes(bottom) &&
					   canPlaceNearWarmBiomes(left) && canPlaceNearWarmBiomes(right)) {
						return center;
					} else {
						return midAndTemperate.get(context.random(midAndTemperate.size()));
					}
				}
				if(isBiomeHot(center)) {
					if(canPlaceNearHotBiomes(top) && canPlaceNearHotBiomes(bottom) &&
					   canPlaceNearHotBiomes(left) && canPlaceNearHotBiomes(right)) {
						return center;
					} else {
						return midAndWarm.get(context.random(midAndWarm.size()));
					}
				}
			}
			
			if(isBiomeHigh(center)) {
				if(isBiomeFrozen(center)) {
					if(canPlaceNearFrozenBiomes(top) && canPlaceNearFrozenBiomes(bottom) && canPlaceNearFrozenBiomes(left) && canPlaceNearFrozenBiomes(right)) {
						return center;
 					} else {
 						return highAndCold.get(context.random(highAndCold.size()));
 					}
				}
				if(isBiomeCold(center)) {
					if(canPlaceNearColdBiomes(top) && canPlaceNearColdBiomes(bottom) && canPlaceNearColdBiomes(left) && canPlaceNearColdBiomes(right)) {
						return center;
 					} else {
 						return highAndTemperate.get(context.random(highAndTemperate.size()));
 					}
				}	
				if(isBiomeTemperate(center)) {
					if(canPlaceNearTemperateBiomes(top) && canPlaceNearTemperateBiomes(bottom) &&
					   canPlaceNearTemperateBiomes(left) && canPlaceNearTemperateBiomes(right)) {
						return center;
					} else {
						//As Temperate is a Transformation Biome we need a detailed Check
						int hotBiomes = 0;
						int frozenBiomes = 0;
						//check for Hot Biomes
						if(isBiomeHot(top)) {
							hotBiomes++;
						}
						if(isBiomeHot(bottom)) {
							hotBiomes++;
						}
						if(isBiomeHot(left)) {
							hotBiomes++;
						}
						if(isBiomeHot(right)) {
							hotBiomes++;
						}
						//check for frozen Biomes
						if(isBiomeFrozen(top)) {
							frozenBiomes++;
						}
						if(isBiomeFrozen(bottom)) {
							frozenBiomes++;
						}
						if(isBiomeFrozen(left)) {
							frozenBiomes++;
						}
						if(isBiomeFrozen(right)) {
							frozenBiomes++;
						}
						
						int compareBiomes = hotBiomes - frozenBiomes;
						
						if(compareBiomes == 0) {
							return highAndTemperate.get(context.random(highAndTemperate.size()));
						}
						
						if(compareBiomes < 0) {
							return highAndCold.get(context.random(highAndCold.size()));
						}
						
						if(compareBiomes > 0) {
							return highAndWarm.get(context.random(highAndWarm.size()));
						}		
					}
				}
				if(isBiomeWarm(center)) {
					if(canPlaceNearWarmBiomes(top) && canPlaceNearWarmBiomes(bottom) &&
					   canPlaceNearWarmBiomes(left) && canPlaceNearWarmBiomes(right)) {
						return center;
					} else {
						return highAndTemperate.get(context.random(highAndTemperate.size()));
					}
				}
				if(isBiomeHot(center)) {
					if(canPlaceNearHotBiomes(top) && canPlaceNearHotBiomes(bottom) &&
					   canPlaceNearHotBiomes(left) && canPlaceNearHotBiomes(right)) {
						return center;
					} else {
						return highAndWarm.get(context.random(highAndWarm.size()));
					}
				}
			}
		}
		return center;
	}
	
	@Override
	public int func_215728_a(IExtendedNoiseRandom<?> context, IArea area, int x, int z) 
	{
		return this == CASTLE ? ICastleTransformer.super.func_215728_a(context, area, x, z) : IBishopTransformer.super.func_215728_a(context,  area, x, z);
	}
}