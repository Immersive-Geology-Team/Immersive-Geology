package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalTungsten extends MaterialBaseMetal {

    public MaterialMetalTungsten() {
        super("tungsten");
        initializeColorMap((p) -> 0x444D6A);
    }

    @Override
    public boolean hasCrystal() {return false;}

    @Override
    public boolean hasCompoundDust() {return  true;}

    @Override
    public boolean hasMetalOxide() {return  true;}
    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.TUNGSTEN)
        ));
    }

}
