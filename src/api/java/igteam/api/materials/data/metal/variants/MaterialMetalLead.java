package igteam.api.materials.data.metal.variants;

import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.data.metal.MaterialBaseMetal;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalLead extends MaterialBaseMetal {


    public MaterialMetalLead() {
        super("lead");
        initializeColorMap((p) -> 0x444f53);
    }

    @Override
    public boolean hasCrystal() {return false;}

    @Override
    public boolean hasExistingImplementation() {
        return true;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.LEAD)
        ));
    }
}
