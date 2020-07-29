package com.igteam.immersivegeology.common.world.noise;

public class NoiseUtil
{
    private NoiseUtil() {}

    /**
     * Offsets a Fastnoise simplex noise value by a CDF percent amount.
     * Does not include robust integrity checking on params.
     */
    public static float simplexNoiseOffsetByPercent(float baseNoise, float percent) {
        float currCDFPercent = 0f;
        float basePercent = baseNoise == -1 ? 0 : noiseToCDF(baseNoise);
        float currNoise = baseNoise;
        while (currCDFPercent < percent && currNoise < 1f) {
            currCDFPercent = noiseToCDF(currNoise) - basePercent;
            currNoise += .01f;
        }
        return currNoise - .01f;
    }

    /**
     * Offsets a Fastnoise simplex noise value by a CDF percent amount in the negative direction.
     * Does not include robust integrity checking on params.
     */
    public static float simplexNoiseNegativeOffsetByPercent(float baseNoise, float percent) {
        float currCDFPercent = 0f;
        float currNoise = baseNoise;
        float basePercent = baseNoise == 1 ? 1 : noiseToCDF(baseNoise);

        while (currCDFPercent < percent && currNoise > -1f) {
            currCDFPercent = basePercent - noiseToCDF(currNoise);
            currNoise -= .01f;
        }
        return currNoise + .01f;
    }

    /**
     * Uses a polynomial approximation of a CDF (Cumulative Distribution Function) for FastNoise Simplex noise to
     * approximate the probability of generating a noise value less than or equal to x.
     * The max margin of error is around 2%.
     * @return Percent chance as a decimal. Since the function is an approximation, it is possible that this value
     *         will be less than 0 or greater than 1.0 for some values of x.
     */
    public static float noiseToCDF(float x) {
        return (-0.435999f * x * x * x) + (.000303f * x * x) + (.916298f * x) + .499721f;
    }
	
	public static float lerp(float start, float end, float t)
	{
		return start*(1-t)+end*t;
	}

	public static int fastFloor(float f)
	{
		return f < 0?(int)f-1: (int)f;
	}

	public static int fastRound(float f)
	{
		return f >= 0?(int)(f+0.5f): (int)(f-0.5f);
	}

	public static int hash(long seed, int x, int y)
	{
		seed ^= 1619*x;
		seed ^= 31337*y;
		seed = seed*seed*seed*60493;
		seed = (seed >> 13)^seed;
		return (int)seed;
	}

	public static int hash(long seed, int x, int y, int z)
	{
		seed ^= 1619*x;
		seed ^= 31337*y;
		seed ^= 6971*z;
		seed = seed*seed*seed*60493;
		seed = (seed >> 13)^seed;
		return (int)seed;
	}
}