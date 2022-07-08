package igteam.api.materials.data.metal.variants;

import igteam.api.materials.data.metal.MaterialBaseMetal;
import igteam.api.materials.helper.CrystalFamily;
import igteam.api.materials.helper.PeriodicTableElement;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalNeodymium extends MaterialBaseMetal {

    public MaterialMetalNeodymium() {
        super("neodymium");
        initializeColorMap((p) -> 0xAB9CA3);
    }

    @Override
    public boolean isNative() {
        return false;
    }

    @Override
    public boolean hasCompoundDust() {return true;}

    @Override
    public boolean hasMetalOxide() {return true;}

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.NEODYMIUM)
        ));
    }
}
