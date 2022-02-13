package igteam.immersive_geology.materials.data.fluid.variants;

import igteam.immersive_geology.materials.data.fluid.MaterialBaseFluid;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialFluidBrine extends MaterialBaseFluid {
    public MaterialFluidBrine() {
        super("brine");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xBCA271;
    }
}
