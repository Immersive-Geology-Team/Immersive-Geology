package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalIron extends MaterialBaseMetal {

    public MaterialMetalIron() {
        super("iron");
        initializeColorMap((p) -> 0xd8dada);
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
    protected boolean hasCompoundDust() {
        return true;
    }

    @Override
    public boolean hasExistingImplementation() {
        return true;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements() {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.IRON)
        ));
    }
}
