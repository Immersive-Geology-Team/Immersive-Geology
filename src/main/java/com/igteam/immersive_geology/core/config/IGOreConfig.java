package com.igteam.immersive_geology.core.config;

public class IGOreConfig {
    public final int veinSize;
    public final int minY;
    public final int maxY;
    public final int veinsPerChunk;

    public IGOreConfig(int veinSize, int minY, int maxY, int veinsPerChunk)
    {
        this.veinSize = veinSize;
        this.minY = minY;
        this.maxY = maxY;
        this.veinsPerChunk = veinsPerChunk;
    }
}
