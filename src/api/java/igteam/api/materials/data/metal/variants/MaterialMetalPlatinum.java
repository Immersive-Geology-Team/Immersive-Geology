package igteam.api.materials.data.metal.variants;

import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.data.metal.MaterialBaseMetal;
import igteam.api.materials.pattern.ItemPattern;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalPlatinum extends MaterialBaseMetal {


    public MaterialMetalPlatinum() {
        super("platinum");
        initializeColorMap((p) -> (p == ItemPattern.metal_oxide ? 0x65698C : 0xe7e7f7));
    }

    @Override
    public boolean hasCrystal() {return false;}

    @Override
    public boolean isNative() {
        return true;
    }

    @Override
    protected boolean hasMetalOxide() {
        return false;
    }

    @Override
    protected boolean hasCompoundDust() {
        return true;
    }

    @Override
    public boolean hasExistingImplementation() {
        return false;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.PLATINUM)
        ));
    }
}
