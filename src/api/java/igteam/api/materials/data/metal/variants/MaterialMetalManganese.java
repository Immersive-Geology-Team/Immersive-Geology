package igteam.api.materials.data.metal.variants;

import igteam.api.materials.MineralEnum;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.data.metal.MaterialBaseMetal;
import net.minecraft.item.Rarity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalManganese extends MaterialBaseMetal {

    public MaterialMetalManganese() {
        super("manganese");
        initializeColorMap((p) -> 0xaaa9ad);
    }


    @Override
    public Rarity getRarity()
    {
        return Rarity.UNCOMMON;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.MANGANESE)
        ));
    }

    @Override
    public ArrayList<MaterialInterface<? extends MaterialBaseMineral>> getSourceMinerals() {
        ArrayList<MaterialInterface<? extends MaterialBaseMineral>> lst = new ArrayList<>();
        lst.add(MineralEnum.Pyrolusite);
        lst.add(MineralEnum.Hubnerite);

        return lst;
    }
}
