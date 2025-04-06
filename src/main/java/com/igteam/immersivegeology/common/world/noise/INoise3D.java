/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world.noise;

import net.minecraft.core.BlockPos;

@FunctionalInterface
public interface INoise3D
{
	float noise(float x, float y, float z);
	default float noise(BlockPos pos)
	{
		return noise(pos.getX(), pos.getY(), pos.getZ());
	}

	default INoise3D octaves(int octaves)
	{
		return octaves(octaves, 0.5f);
	}
	default INoise3D min(INoise3D other) {
		return (x, y, z) -> Math.min(INoise3D.this.noise(x, y, z), other.noise(x, y, z));
	}


	default INoise3D subtractAndMin(INoise3D other) {
		return (x, y, z) -> {
			float original = INoise3D.this.noise(x, y, z);
			float subtracted = original - other.noise(x, y, z);
			return Math.min(original, subtracted);
		};
	}

	default INoise3D octaves(int octaves, float persistence)
	{
		final float[] frequency = new float[octaves];
		final float[] amplitude = new float[octaves];
		for(int i = 0; i < octaves; i++)
		{
			frequency[i] = 1<<i;
			amplitude[i] = (float)Math.pow(persistence, octaves-i);
		}
		return (x, y, z) -> {
			float value = 0;
			for(int i = 0; i < octaves; i++)
			{
				value += INoise3D.this.noise(x/frequency[i], y/frequency[i], z/frequency[i])*amplitude[i];
			}
			return value;
		};
	}

	default INoise3D ridged()
	{
		return (x, y, z) -> {
			float value = INoise3D.this.noise(x, y, z);
			value = value < 0?-value: value;
			return 1f-2f*value;
		};
	}

	default INoise3D terraces(int levels)
	{
		return (x, y, z) -> {
			float value = 0.5f*INoise3D.this.noise(x, y, z)+0.5f;
			float rounded = (int)(value*levels); // In range [0, levels)
			return (rounded*2f)/levels-1f;
		};
	}

	default INoise3D spread(float scaleFactor)
	{
		return (x, y, z) -> INoise3D.this.noise(x*scaleFactor, y*scaleFactor, z*scaleFactor);
	}

	default INoise3D warped(INoise3D warpX, INoise3D warpY, INoise3D warpZ)
	{
		return (x, y, z) -> {
			float x0 = x+warpX.noise(x, y, z);
			float y0 = y+warpY.noise(x, y, z);
			float z0 = z+warpZ.noise(x, y, z);
			return INoise3D.this.noise(x0, y0, z0);
		};
	}

	default INoise3D flattened(float min, float max)
	{
		return (x, y, z) -> {
			float noise = INoise3D.this.noise(x, y, z);
			return noise > max?max: noise < min?min: noise;
		};
	}

	default INoise3D scale(float factor) {
		return (x, y, z) -> INoise3D.this.noise(x, y, z) * factor;
	}

	default INoise3D gap(INoise3D other) {
		return (x, y, z) -> other.noise(x,y,z) > 0 ? -1 : INoise3D.this.noise(x, y, z) - other.noise(x,y,z);
	}

	default INoise3D bias(float offset) {
		return (x, y, z) -> INoise3D.this.noise(x, y, z) + offset;
	}
	default INoise3D add(INoise3D other)
	{
		return (x, y, z) -> INoise3D.this.noise(x, y, z)+other.noise(x, y, z);
	}
	default INoise3D abs() {
		return (x, y, z) -> Math.abs(INoise3D.this.noise(x, y, z));
	}
	default INoise3D invert() {
		return (x, y, z) -> -INoise3D.this.noise(x, y, z);
	}
	default INoise3D power(float exponent) {
		return (x, y, z) -> (float) Math.pow(INoise3D.this.noise(x, y, z), exponent);
	}
	default INoise3D multiply(INoise3D other) {
		return (x, y, z) -> INoise3D.this.noise(x, y, z) * other.noise(x, y, z);
	}
	default INoise3D blend(INoise3D other, float alpha) {
		return (x, y, z) -> {
			float thisNoise = INoise3D.this.noise(x, y, z);
			float otherNoise = other.noise(x, y, z);
			return thisNoise * (1 - alpha) + otherNoise * alpha;
		};
	}
	default INoise3D sub(INoise3D other)
	{
		return (x, y, z) -> INoise3D.this.noise(x, y, z)-other.noise(x, y, z);
	}
	default INoise3D sinWarp(float frequency, float amplitude) {
		return (x, y, z) -> {
			float noise = INoise3D.this.noise(x, y, z);
			return (float) Math.sin(noise * frequency) * amplitude;
		};
	}
	default INoise3D mirror(float planeX, float planeY, float planeZ) {
		return (x, y, z) -> {
			float mirroredX = 2 * planeX - x;
			float mirroredY = 2 * planeY - y;
			float mirroredZ = 2 * planeZ - z;
			return INoise3D.this.noise(mirroredX, mirroredY, mirroredZ);
		};
	}

}
