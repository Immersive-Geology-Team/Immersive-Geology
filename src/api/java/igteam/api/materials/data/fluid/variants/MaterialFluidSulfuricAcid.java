package igteam.api.materials.data.fluid.variants;

import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.data.fluid.MaterialBaseFluid;
import igteam.api.materials.pattern.MaterialPattern;

import java.util.LinkedHashSet;

public class MaterialFluidSulfuricAcid extends MaterialBaseFluid {
    public MaterialFluidSulfuricAcid() {
        super("sulfuric_acid");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements() {
        return null;
    }
}
