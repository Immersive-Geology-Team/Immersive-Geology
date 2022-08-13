package igteam.api.materials.data.metal.variants;

import igteam.api.materials.MineralEnum;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.data.metal.MaterialBaseMetal;
import igteam.api.materials.helper.CrystalFamily;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalZinc extends MaterialBaseMetal {

    public MaterialMetalZinc() {
        super("zinc");
        initializeColorMap((p) -> 0xd0d5db);
    }

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
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.ZINC)
        ));
    }

    @Override
    public ArrayList<MaterialInterface<? extends MaterialBaseMineral>> getSourceMinerals() {
        ArrayList<MaterialInterface<? extends MaterialBaseMineral>> lst = new ArrayList<>();
        lst.add(MineralEnum.Smithsonite);
        lst.add(MineralEnum.Sphalerite);

        return lst;
    }
}
