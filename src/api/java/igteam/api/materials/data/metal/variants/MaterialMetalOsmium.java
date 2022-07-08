package igteam.api.materials.data.metal.variants;

import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.data.metal.MaterialBaseMetal;
import igteam.api.materials.helper.CrystalFamily;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalOsmium extends MaterialBaseMetal {

    public MaterialMetalOsmium() {
        super("osmium");
        initializeColorMap((p) -> 0x8A9A9A);
    }

    @Override
    public boolean hasCrystal() {return false;}

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OSMIUM)
        ));
    }
}
