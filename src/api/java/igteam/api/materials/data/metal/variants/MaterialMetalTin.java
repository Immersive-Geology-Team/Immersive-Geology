package igteam.api.materials.data.metal.variants;

import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.data.metal.MaterialBaseMetal;
import igteam.api.materials.helper.CrystalFamily;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalTin extends MaterialBaseMetal {

    public MaterialMetalTin() {
        super("tin");
        initializeColorMap((p) -> 0xd3d4d5);
    }

    @Override
    public boolean hasCrystal() {return false;}

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.TETRAGONAL;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.TIN)
        ));
    }
}
