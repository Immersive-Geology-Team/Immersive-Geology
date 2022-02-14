package igteam.immersive_geology.materials.data.fluid.variants;

import igteam.immersive_geology.materials.data.fluid.MaterialBasFluid;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialFluidBrine extends MaterialBasFluid {
    public MaterialFluidBrine() {
        super("brine");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xBCA271;
    }
}
