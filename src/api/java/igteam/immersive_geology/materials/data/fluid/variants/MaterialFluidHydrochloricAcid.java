package igteam.immersive_geology.materials.data.fluid.variants;

import igteam.immersive_geology.materials.data.fluid.MaterialBaseFluid;

public class MaterialFluidHydrochloricAcid extends MaterialBaseFluid {

    public MaterialFluidHydrochloricAcid(){
        super("hydrochloric_acid");
    }

    @Override
    public int getColor() {
        return 0xFFFFFF;
    }
}
