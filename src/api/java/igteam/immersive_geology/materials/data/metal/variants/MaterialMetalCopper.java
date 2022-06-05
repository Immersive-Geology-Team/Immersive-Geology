package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.helper.PeriodicTableElement.ElementProportion;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalCopper extends MaterialBaseMetal {

    public MaterialMetalCopper() {
        super("copper");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xe39919;
    }

    @Override
    public boolean isNative() {
        return true;
    }

    @Override
    protected boolean hasMetalOxide() {
        return true;
    }
    @Override
    public boolean hasExistingImplementation() {
        return true;
    }

    @Override
    public LinkedHashSet<ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new ElementProportion(PeriodicTableElement.COPPER)
        ));
    }
}
