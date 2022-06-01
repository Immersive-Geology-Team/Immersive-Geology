package igteam.immersive_geology.materials.data.fluid.variants;

import igteam.immersive_geology.materials.data.fluid.MaterialBaseFluid;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

import java.util.LinkedHashSet;

public class MaterialFluidHydrofluoricAcid extends MaterialBaseFluid {

    public MaterialFluidHydrofluoricAcid(){
        super("hydrofluoric_acid");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xFFFFFF;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements() {
        return null;
    }
}