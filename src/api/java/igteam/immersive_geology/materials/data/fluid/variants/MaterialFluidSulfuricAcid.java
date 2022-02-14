package igteam.immersive_geology.materials.data.fluid.variants;

import igteam.immersive_geology.materials.data.fluid.MaterialBasFluid;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialFluidSulfuricAcid extends MaterialBasFluid {
    public MaterialFluidSulfuricAcid() {
        super("sulfuric_acid");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0;
    }
}
