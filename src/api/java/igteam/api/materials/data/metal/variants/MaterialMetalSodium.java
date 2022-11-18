package igteam.api.materials.data.metal.variants;

import igteam.api.materials.MineralEnum;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.data.metal.MaterialBaseMetal;

import java.util.ArrayList;
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

    @Override
    public ArrayList<MaterialInterface<? extends MaterialBaseMineral>> getSourceMinerals() {
        ArrayList<MaterialInterface<? extends MaterialBaseMineral>> lst = new ArrayList<>();
        lst.add(MineralEnum.RockSalt);
        lst.add(MineralEnum.Cryolite);

        return lst;
    }
}
