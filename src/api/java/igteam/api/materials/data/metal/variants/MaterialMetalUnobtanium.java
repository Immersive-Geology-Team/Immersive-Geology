package igteam.api.materials.data.metal.variants;

import igteam.api.materials.data.metal.MaterialBaseMetal;
import igteam.api.materials.helper.PeriodicTableElement;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalUnobtanium extends MaterialBaseMetal {

    public MaterialMetalUnobtanium() {
        super("unobtanium");
        initializeColorMap((p) -> 0x444D6A);
    }


    @Override
    public boolean hasMetalOxide () {return true;}

    @Override
    public boolean hasCompoundDust () {return true;}
    @Override
    public boolean hasCrystal() {return true;}

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.UNOBTANIUM)
        ));
    }
}
