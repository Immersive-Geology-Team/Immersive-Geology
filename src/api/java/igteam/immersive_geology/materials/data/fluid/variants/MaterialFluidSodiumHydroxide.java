package igteam.immersive_geology.materials.data.fluid.variants;

import igteam.immersive_geology.materials.data.fluid.MaterialBasFluid;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialFluidSodiumHydroxide extends MaterialBasFluid {

    public MaterialFluidSodiumHydroxide(){
        super("sodium_hydroxide");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xe3ce77;
    }
}
