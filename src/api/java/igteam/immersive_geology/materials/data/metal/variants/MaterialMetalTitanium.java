package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalTitanium extends MaterialBaseMetal {

    public MaterialMetalTitanium() {
        super("titanium");
        initializeColorMap((p) -> 0x878681);
    }
    @Override
    public boolean hasCrystal() {return false;}

    @Override
    public boolean hasMetalOxide() {return true;}

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.TITANIUM)
        ));
    }

}
