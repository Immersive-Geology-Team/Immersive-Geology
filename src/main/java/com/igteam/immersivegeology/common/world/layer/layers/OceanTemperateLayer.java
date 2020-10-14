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

public enum OceanTemperateLayer implements ICastleTransformer, IBishopTransformer
{
	CASTLE, BISHOP;

	public static final int[] FROZEN_BIOMES = new int[]{FROZEN_DEEP_OCEAN};
	public static final int[] COLD_BIOMES = new int[]{COLD_OCEAN};
	public static final int[] TEMPERATE_BIOMES = new int[]{OCEAN, DEEP_OCEAN};
	public static final int[] WARM_BIOMES = new int[]{WARM_OCEAN};
	public static final int[] HOT_BIOMES = new int[]{DEEP_OCEAN_VOLCANIC};

	public static List<Integer> FROZEN = new ArrayList<Integer>(
			Arrays.stream(OceanTemperateLayer.FROZEN_BIOMES).boxed().collect(Collectors.toList()));
	public static List<Integer> COLD = new ArrayList<Integer>(
			Arrays.stream(OceanTemperateLayer.COLD_BIOMES).boxed().collect(Collectors.toList()));
	public static List<Integer> TEMPERATE = new ArrayList<Integer>(
			Arrays.stream(OceanTemperateLayer.TEMPERATE_BIOMES).boxed().collect(Collectors.toList()));
	public static List<Integer> WARM = new ArrayList<Integer>(
			Arrays.stream(OceanTemperateLayer.WARM_BIOMES).boxed().collect(Collectors.toList()));
	public static List<Integer> HOT = new ArrayList<Integer>(
			Arrays.stream(OceanTemperateLayer.HOT_BIOMES).boxed().collect(Collectors.toList()));

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

	public int apply(INoiseRandom context, int top, int right, int bottom, int left, int center)
	{
		if(IGLayerUtil.isOcean(center))
		{
			if(isBiomeFrozen(center)) {
				if(canPlaceNearFrozenBiomes(top) && canPlaceNearFrozenBiomes(bottom) && canPlaceNearFrozenBiomes(left) && canPlaceNearFrozenBiomes(right)) {
					return center;
				} else {
					return COLD.get(context.random(COLD.size()));
				}
			}

			if(isBiomeCold(center)) {
				if(canPlaceNearColdBiomes(top) && canPlaceNearColdBiomes(bottom) && canPlaceNearColdBiomes(left) && canPlaceNearColdBiomes(right)) {
					return center;
				} else {
					return TEMPERATE.get(context.random(TEMPERATE.size()));
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
						return TEMPERATE.get(context.random(TEMPERATE.size()));
					}

					if(compareBiomes < 0) {
						return COLD.get(context.random(COLD.size()));
					}

					if(compareBiomes > 0) {
						return WARM.get(context.random(WARM.size()));
					}
				}
			}
			if(isBiomeWarm(center)) {
				if(canPlaceNearWarmBiomes(top) && canPlaceNearWarmBiomes(bottom) &&
				   canPlaceNearWarmBiomes(left) && canPlaceNearWarmBiomes(right)) {
					return center;
				} else {
					return TEMPERATE.get(context.random(TEMPERATE.size()));
				}
			}
			if(isBiomeHot(center)) {
				if(canPlaceNearHotBiomes(top) && canPlaceNearHotBiomes(bottom) &&
				   canPlaceNearHotBiomes(left) && canPlaceNearHotBiomes(right)) {
					return center;
				} else {
					return WARM.get(context.random(WARM.size()));
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