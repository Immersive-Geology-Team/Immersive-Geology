package igteam.api.materials.data.metal.variants;

import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.data.metal.MaterialBaseMetal;
import igteam.api.materials.helper.CrystalFamily;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalCobalt extends MaterialBaseMetal {

    public MaterialMetalCobalt() {
        super("cobalt");
        initializeColorMap((p) -> 0x1A79FF);
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.COBALT)
        ));
    }
}
