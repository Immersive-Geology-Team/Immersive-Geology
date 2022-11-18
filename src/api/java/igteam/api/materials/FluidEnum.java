package igteam.api.materials;

import igteam.api.materials.data.fluid.MaterialBaseFluid;
import igteam.api.materials.data.fluid.variants.*;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.ItemFamily;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public enum FluidEnum implements MaterialInterface<MaterialBaseFluid> {
    Brine(new MaterialFluidBrine()),
    SulfuricAcid(new MaterialFluidSulfuricAcid()),
    HydrochloricAcid(new MaterialFluidHydrochloricAcid()),
    HydrofluoricAcid(new MaterialFluidHydrofluoricAcid()),
    NitricAcid(new MaterialFluidNitricAcid()),
    SodiumHydroxide(new MaterialFluidSodiumHydroxide());

    private final MaterialBaseFluid material;

    FluidEnum(MaterialBaseFluid m){
        this.material = m;
    }

    @Override
    public MaterialBaseFluid instance() {
        return material;
    }

    @Override
    public FeatureConfiguration getGenerationConfig() {
        return null;
    }

    @Override
    public boolean isFluidPortable(ItemFamily pattern){
        return material.isFluidPortable(pattern);
    }


}
