package igteam.api.materials.data.metal.variants;

import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.data.metal.MaterialBaseMetal;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalNickel extends MaterialBaseMetal {

    public MaterialMetalNickel() {
        super("nickel");
        initializeColorMap((p) -> 0x7FFFD4);
    }

    @Override
    public boolean isNative() {
        return true;
    }


    @Override
    public boolean hasMetalOxide () {return true;}
    @Override
    public boolean hasExistingImplementation() {
        return true;
    }

    @Override
    public boolean hasCrystal() {return true;}

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.NICKEL)
        ));
    }
}