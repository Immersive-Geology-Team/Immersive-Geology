package igteam.api.materials.data.metal.variants;

import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.data.metal.MaterialBaseMetal;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalVanadium extends MaterialBaseMetal {

    public MaterialMetalVanadium() {
        super("vanadium");
        initializeColorMap((p) -> 0x8e1e1d);
    }
    @Override
    public boolean hasCrystal() {return false;}
    @Override
    public boolean hasMetalOxide(){return true;}
    @Override
    public boolean hasCompoundDust(){return true;}
    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.VANADIUM)
        ));
    }
}
