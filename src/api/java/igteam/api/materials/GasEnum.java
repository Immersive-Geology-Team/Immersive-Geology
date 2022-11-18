package igteam.api.materials;

import igteam.api.materials.data.gas.MaterialBaseGas;
import igteam.api.materials.data.gas.variants.MaterialSulphurDioxideGas;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.ItemFamily;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public enum GasEnum implements MaterialInterface<MaterialBaseGas> {
    SulphurDioxide(new MaterialSulphurDioxideGas());

    private final MaterialBaseGas material;

    GasEnum(MaterialBaseGas m){
        this.material = m;
    }
    @Override
    public MaterialBaseGas instance() {
        return material;
    }

    @Override
    public FeatureConfiguration getGenerationConfig() {
        return null;
    }

    @Override
    public boolean isFluidPortable(ItemFamily pattern){
        return false;
    }

}
