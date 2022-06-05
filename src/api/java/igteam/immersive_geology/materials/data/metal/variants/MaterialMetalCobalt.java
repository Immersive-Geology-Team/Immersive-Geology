package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.helper.PeriodicTableElement.ElementProportion;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalCobalt extends MaterialBaseMetal {

    public MaterialMetalCobalt() {
        super("cobalt");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0x1A79FF;
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

    @Override
    public LinkedHashSet<ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new ElementProportion(PeriodicTableElement.COBALT)
        ));
    }
}
