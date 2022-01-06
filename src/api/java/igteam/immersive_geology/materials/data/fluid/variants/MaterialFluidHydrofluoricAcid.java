package igteam.immersive_geology.materials.data.fluid.variants;

import igteam.immersive_geology.materials.data.fluid.MaterialBaseFluid;

public class MaterialFluidHydrofluoricAcid extends MaterialBaseFluid {

    public MaterialFluidHydrofluoricAcid(){
        super("hydrofluoric_acid");
    }

    @Override
    public int getColor() {
        return 0xFFFFFF;
    }
}
