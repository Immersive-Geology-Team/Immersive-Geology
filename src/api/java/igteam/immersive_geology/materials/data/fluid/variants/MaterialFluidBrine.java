package igteam.immersive_geology.materials.data.fluid.variants;

import igteam.immersive_geology.materials.data.fluid.MaterialBaseFluid;

public class MaterialFluidBrine extends MaterialBaseFluid {
    public MaterialFluidBrine() {
        super("brine");
    }

    @Override
    public int getColor() {
        return 0xBCA271;
    }
}
