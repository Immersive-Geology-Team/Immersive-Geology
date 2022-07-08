package igteam.api.materials.data.metal.variants;

import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.data.metal.MaterialBaseMetal;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalSodium extends MaterialBaseMetal {

    public MaterialMetalSodium() {
        super("sodium");
        initializeColorMap((p) -> 0xd0d5db);
    }

    @Override
    public boolean hasCrystal() {return false;}

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