package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalSodium extends MaterialBaseMetal {

    public MaterialMetalSodium() {
        super("sodium");
        initializeColorMap((p) -> 0xd0d5db);
    }

    @Override
    public boolean hasMetalOxide() {
        return true;
    }

    @Override
    public boolean hasCompoundDust() {
        return true;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements() {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SODIUM)
        ));
    }
}
