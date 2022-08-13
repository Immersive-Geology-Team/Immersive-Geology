package igteam.api.materials.data.metal.variants;

import igteam.api.materials.data.metal.MaterialBaseMetal;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.helper.PeriodicTableElement;
import net.minecraft.item.Rarity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalBronze extends MaterialBaseMetal {

    public MaterialMetalBronze() {
        super("bronze");
        initializeColorMap((p) -> 0xf5d57f);
    }

    @Override
    public Rarity getRarity() {
        return Rarity.UNCOMMON;
    }

    @Override
    public boolean hasCrystal() {
        return false;
    }

    @Override
    public boolean hasCompoundDust() {
        return false;
    }

    @Override
    public boolean hasMetalOxide() {
        return false;
    }


    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements() {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.COPPER),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.TIN)
        ));
    }

    @Override
    public ArrayList<MaterialInterface<? extends MaterialBaseMineral>> getSourceMinerals() {
        ArrayList<MaterialInterface<? extends MaterialBaseMineral>> lst = new ArrayList<>();


        return lst;
    }
}