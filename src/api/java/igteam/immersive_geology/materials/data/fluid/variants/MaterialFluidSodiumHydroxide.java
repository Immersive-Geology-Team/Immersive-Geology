package igteam.immersive_geology.materials.data.fluid.variants;

import igteam.immersive_geology.materials.data.fluid.MaterialBaseFluid;

public class MaterialFluidSodiumHydroxide extends MaterialBaseFluid {

    public MaterialFluidSodiumHydroxide(){
        super("sodium_hydroxide");
    }

    @Override
    public int getColor() {
        return 0xe3ce77;
    }
}
