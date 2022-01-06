package igteam.immersive_geology.materials.data.fluid.variants;

import igteam.immersive_geology.materials.data.fluid.MaterialBaseFluid;

public class MaterialFluidNitricAcid extends MaterialBaseFluid {
    public MaterialFluidNitricAcid(){
        super("nitric_acid");
    }

    @Override
    public int getColor() {
        return 0xe3ce77;
    }
}
