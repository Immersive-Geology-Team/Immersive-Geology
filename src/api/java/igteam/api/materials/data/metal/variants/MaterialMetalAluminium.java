package igteam.api.materials.data.metal.variants;

import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.data.metal.MaterialBaseMetal;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalAluminium extends MaterialBaseMetal {

    public MaterialMetalAluminium() {
        super("aluminum"); //compat rename
        initializeColorMap((p) -> 0xd0d5db);
    }

    @Override
    public boolean hasCrystal() {return false;}

    @Override
    public boolean hasExistingImplementation() {
        return true;
    }

    @Override
   public boolean hasCompoundDust () {return  true;}

    @Override
    public boolean hasMetalOxide () {return  true;}
    @Override
    public Rarity getRarity() {
        return Rarity.UNCOMMON;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.ALUMINIUM)
        ));
    }
}
