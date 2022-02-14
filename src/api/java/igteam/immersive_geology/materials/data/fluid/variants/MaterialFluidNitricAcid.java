package igteam.immersive_geology.materials.data.fluid.variants;

import igteam.immersive_geology.materials.data.fluid.MaterialBasFluid;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialFluidNitricAcid extends MaterialBasFluid {
    public MaterialFluidNitricAcid(){
        super("nitric_acid");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xe3ce77;
    }
}
