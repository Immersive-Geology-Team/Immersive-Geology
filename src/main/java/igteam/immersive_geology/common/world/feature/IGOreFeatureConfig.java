package igteam.immersive_geology.common.world.feature;

import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.helper.MaterialSourceWorld;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import static net.minecraft.world.gen.feature.OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD;

public class IGOreFeatureConfig extends OreFeatureConfig {

    final int minSize, maxSize;

    final MaterialSourceWorld sourceDimension;
    final MaterialInterface<?> oreType;
    public IGOreFeatureConfig(MaterialSourceWorld dimension, MaterialInterface<?> oreType, int sizeMin, int sizeMax) {
        super(BASE_STONE_OVERWORLD, Blocks.STONE.getDefaultState(), sizeMax);
        this.minSize = sizeMin;
        this.maxSize = sizeMax;
        this.oreType = oreType;
        this.sourceDimension = dimension;
    }
}
