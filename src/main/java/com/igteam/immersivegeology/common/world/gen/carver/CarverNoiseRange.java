package com.igteam.immersivegeology.common.world.gen.carver;

import com.igteam.immersivegeology.common.world.noise.NoiseCube;
import com.igteam.immersivegeology.common.world.noise.NoiseUtil;

public class CarverNoiseRange {
    // Bounds of a range of noise values
    private float bottom, top;

    // Thresholds marking the boundaries for the bottom and top smoothing ranges.
    // Currently only used for caverns.
    private float smoothBottomCutoff, smoothTopCutoff;

    // The carver associated with this range of noises.
    private ICarver carver;

    // The NoiseCube associated with this range of noises.
    private NoiseCube noiseCube;

    // The degree of smoothing on cavern edges. For a given SMOOTH_PERCENT x, both the
    // bottom and top ends of the noise range are each smoothed by (x * 100) percent.
    private static final float SMOOTH_PERCENT = .3f;

    public CarverNoiseRange(float bottom, float top, ICarver carver) {
        this.bottom = bottom;
        this.top = top;
        float smoothRangePercent = getPercentLength() * SMOOTH_PERCENT;
        this.smoothBottomCutoff = NoiseUtil.simplexNoiseOffsetByPercent(bottom, smoothRangePercent);
        this.smoothTopCutoff = NoiseUtil.simplexNoiseNegativeOffsetByPercent(top, smoothRangePercent);
        this.carver = carver;
        this.noiseCube = null;
    }

    public boolean contains(float noiseValue) {
        return bottom <= noiseValue && noiseValue < top;
    }

    public float getSmoothAmp(float noiseValue) {
        if (bottom <= noiseValue && noiseValue <= smoothBottomCutoff) {
            return (noiseValue - bottom) / (smoothBottomCutoff - bottom);
        }
        else if (smoothTopCutoff <= noiseValue && noiseValue < top) {
            return (noiseValue - top) / (smoothTopCutoff - top);
        }
        return 1;
    }

    public float getPercentLength() {
        return (top == 1 ? 1 : NoiseUtil.noiseToCDF(top)) - (bottom == -1 ? 0 : NoiseUtil.noiseToCDF(bottom));
    }

    public ICarver getCarver() {
        return carver;
    } 

    public NoiseCube getNoiseCube() {
        return noiseCube;
    }

    public void setNoiseCube(NoiseCube noiseCube) {
        this.noiseCube = noiseCube;
    }

    @Override
    public String toString() {
        return String.format("[%2.2f, %2.2f] (%2.4f%%) -- smooth cutoffs: [%2.2f, %2.2f]", bottom, top, getPercentLength(), smoothBottomCutoff, smoothTopCutoff);
    }
}
