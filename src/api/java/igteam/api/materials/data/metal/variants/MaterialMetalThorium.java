package igteam.api.materials.data.metal.variants;

import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.data.metal.MaterialBaseMetal;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalThorium extends MaterialBaseMetal {

    public MaterialMetalThorium() {
        super("thorium");
        initializeColorMap((p) -> 0x45484b);
    }

    @Override
    public boolean hasCompoundDust() {return true;}

    @Override
    public boolean hasMetalOxide() {return true;}
    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.THORIUM)
        ));
    }
}
