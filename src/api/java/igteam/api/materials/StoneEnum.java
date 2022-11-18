package igteam.api.materials;

import igteam.api.materials.data.stone.MaterialBaseStone;
import igteam.api.materials.data.stone.variants.*;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.ItemFamily;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public enum StoneEnum implements MaterialInterface<MaterialBaseStone> {
    Stone(new MaterialDefaultStone()),
    Granite(new MaterialGranite()),
    Netherrack(new MaterialNetherrack()),
    Blackstone(new MaterialBlackstone()),
    End_stone(new MaterialEndstone()); //Needs underscore to generate correctly

    private final MaterialBaseStone material;

    StoneEnum(MaterialBaseStone m){
        this.material = m;
    }

    @Override
    public MaterialBaseStone instance() {
        return material;
    }

    @Override
    public FeatureConfiguration getGenerationConfig() {
        return null;
    }

    @Override
    public boolean isFluidPortable(ItemFamily bucket) {
        return false;
    }
}
