package igteam.api.materials.data.fluid.variants;

import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.data.fluid.MaterialBaseFluid;
import igteam.api.materials.pattern.MaterialPattern;

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
