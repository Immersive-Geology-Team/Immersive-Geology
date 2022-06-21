package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

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
