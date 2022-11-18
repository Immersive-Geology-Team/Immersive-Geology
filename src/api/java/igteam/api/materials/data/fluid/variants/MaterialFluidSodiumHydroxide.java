package igteam.api.materials.data.fluid.variants;

import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.data.fluid.MaterialBaseFluid;
import igteam.api.materials.pattern.MaterialPattern;

import java.util.LinkedHashSet;

public class MaterialFluidSodiumHydroxide extends MaterialBaseFluid {

    public MaterialFluidSodiumHydroxide(){
        super("sodium_hydroxide");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xe3ce77;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements() {
        return null;
    }
}
