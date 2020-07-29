package com.igteam.immersivegeology.common.world.gen.carver.builder;

import com.igteam.immersivegeology.common.world.gen.carver.CarverSettings;
import com.igteam.immersivegeology.common.world.gen.carver.CaveCarver;
import com.igteam.immersivegeology.common.world.gen.carver.util.CaveType;
import com.igteam.immersivegeology.common.world.noise.FastNoise;

import net.minecraft.block.BlockState;

public class CaveCarverBuilder {
	private CarverSettings settings;
	private int surfaceCutoff;
	private int bottomY;
	private int topY;
	private boolean enableYAdjust;
	private float yAdjustF1;
	private float yAdjustF2;
 
	public CaveCarverBuilder(long seed) {
		settings = new CarverSettings(seed);
	}

	public CaveCarver build() {
		return new CaveCarver(this);
	} 

	/**
	 * Helps build a CaveCarver from a ConfigHolder based on its CaveType
	 * 
	 * @param caveType the CaveType of this CaveCarver
	 * @param config   the config
	 */
	public CaveCarverBuilder ofTypeFromConfig(CaveType caveType) {
		this.settings.setLiquidAltitude(10);
		this.settings.setReplaceFloatingGravel(true);
		this.settings.getNoiseSettings().setFractalType(FastNoise.FractalType.RigidMulti);
		switch (caveType) { 
		case CUBIC:
			this.settings.setFastNoise(true);
			this.settings.setNoiseThreshold(.95f);
			this.settings.getNoiseSettings().setNoiseType(FastNoise.NoiseType.valueOf("CubicFractal"));
			this.settings.getNoiseSettings().setOctaves(1);
			this.settings.getNoiseSettings().setGain(0.3f);
			this.settings.getNoiseSettings().setFrequency(0.03f);
			this.settings.setNumGens(2);
			this.settings.setXzCompression(1.6f);
			this.settings.setyCompression(5f);
			this.settings.setPriority(10);
			this.surfaceCutoff = 15;
			this.bottomY = 1;
			this.topY = 80;
			this.enableYAdjust = true;
			this.yAdjustF1 = .9f;
			this.yAdjustF2 = .9f;
			break;
		case SIMPLEX:
			this.settings.setFastNoise(false);
			this.settings.setNoiseThreshold(.82f);
			this.settings.getNoiseSettings()
					.setNoiseType(FastNoise.NoiseType.valueOf("SimplexFractal"));
			this.settings.getNoiseSettings().setOctaves(1);
			this.settings.getNoiseSettings().setGain(.3f);
			this.settings.getNoiseSettings().setFrequency(.025f);
			this.settings.setNumGens(2);
			this.settings.setXzCompression(0.9f);
			this.settings.setyCompression(2.2f);
			this.settings.setPriority(5);
			this.surfaceCutoff = 15;
			this.bottomY = 1;
			this.topY = 80;
			this.enableYAdjust = true;
			this.yAdjustF1 = .95f;
			this.yAdjustF2 = .5f;
			break;
		}
		return this;
	}

	/*
	 * ================================== Builder Setters
	 * ==================================
	 */
	/**
	 * @param noiseType The type of noise this carver will use
	 */
	public CaveCarverBuilder noiseType(FastNoise.NoiseType noiseType) {
		settings.getNoiseSettings().setNoiseType(noiseType);
		return this;
	}

	/**
	 * @param fractalOctaves Number of fractal octaves to use in ridged multifractal
	 *                       noise generation
	 */
	public CaveCarverBuilder fractalOctaves(int fractalOctaves) {
		settings.getNoiseSettings().setOctaves(fractalOctaves);
		return this;
	}

	/**
	 * @param fractalGain Amount of gain to use in ridged multifractal noise
	 *                    generation
	 */
	public CaveCarverBuilder fractalGain(float fractalGain) {
		settings.getNoiseSettings().setGain(fractalGain);
		return this;
	}

	/**
	 * @param fractalFreq Frequency to use in ridged multifractal noise generation
	 */
	public CaveCarverBuilder fractalFrequency(float fractalFreq) {
		settings.getNoiseSettings().setFrequency(fractalFreq);
		return this;
	}

	/**
	 * @param numGens Number of noise values to calculate for a given block
	 */
	public CaveCarverBuilder numberOfGenerators(int numGens) {
		settings.setNumGens(numGens);
		return this;
	}

	/**
	 * @param yCompression Vertical cave gen compression. Use 1.0 for default
	 *                     generation
	 */
	public CaveCarverBuilder verticalCompression(float yCompression) {
		settings.setyCompression(yCompression);
		return this;
	}

	/**
	 * @param xzCompression Horizontal cave gen compression. Use 1.0 for default
	 *                      generation
	 */
	public CaveCarverBuilder horizontalCompression(float xzCompression) {
		settings.setXzCompression(xzCompression);
		return this;
	}

	/**
	 * @param surfaceCutoff Cave surface cutoff depth
	 */
	public CaveCarverBuilder surfaceCutoff(int surfaceCutoff) {
		this.surfaceCutoff = surfaceCutoff;
		return this;
	}

	/**
	 * @param bottomY Cave bottom y-coordinate
	 */
	public CaveCarverBuilder bottomY(int bottomY) {
		this.bottomY = bottomY;
		return this;
	}

	/**
	 * @param topY Cave top y-coordinate
	 */
	public CaveCarverBuilder topY(int topY) {
		this.topY = topY;
		return this;
	}

	/**
	 * @param yAdjustF1 Adjustment value for the block immediately above. Must be
	 *                  between 0 and 1.0
	 */
	public CaveCarverBuilder verticalAdjuster1(float yAdjustF1) {
		this.yAdjustF1 = yAdjustF1;
		return this;
	}

	/**
	 * @param yAdjustF2 Adjustment value for the block two blocks above. Must be
	 *                  between 0 and 1.0
	 */
	public CaveCarverBuilder verticalAdjuster2(float yAdjustF2) {
		this.yAdjustF2 = yAdjustF2;
		return this;
	}

	/**
	 * @param enableYAdjust Whether or not to adjust/increase the height of caves.
	 */
	public CaveCarverBuilder enableVerticalAdjustment(boolean enableYAdjust) {
		this.enableYAdjust = enableYAdjust;
		return this;
	}

	/**
	 * @param noiseThreshold Noise threshold to determine whether or not a given
	 *                       block will be dug out
	 */
	public CaveCarverBuilder noiseThreshold(float noiseThreshold) {
		settings.setNoiseThreshold(noiseThreshold);
		return this;
	}

	/**
	 * @param liquidAltitude altitude at and below which air is replaced with liquid
	 */
	public CaveCarverBuilder liquidAltitude(int liquidAltitude) {
		settings.setLiquidAltitude(liquidAltitude);
		return this;
	}

	/*
	 * ================================== Builder Getters
	 * ==================================
	 */

	public CarverSettings getSettings() {
		return settings;
	}

	public int getSurfaceCutoff() {
		return surfaceCutoff;
	}

	public int getBottomY() {
		return bottomY;
	}

	public int getTopY() {
		return topY;
	}

	public boolean isEnableYAdjust() {
		return enableYAdjust;
	}

	public float getyAdjustF1() {
		return yAdjustF1;
	}

	public float getyAdjustF2() {
		return yAdjustF2;
	}
}
