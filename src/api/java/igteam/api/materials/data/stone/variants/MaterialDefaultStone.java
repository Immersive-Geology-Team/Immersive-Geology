package igteam.api.materials.data.stone.variants;

import igteam.api.materials.data.stone.MaterialBaseStone;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.helper.MaterialSourceWorld;
import igteam.api.materials.helper.PeriodicTableElement;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialDefaultStone extends MaterialBaseStone {

    public MaterialDefaultStone() {
        super("stone");
        initializeColorMap((p) -> 0x8F8F8F);
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements() {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SILICON),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 2),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.ALUMINIUM),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 3)
        ));
    }

    @Override
    protected boolean hasDefaultBlock() {
        return false;
    }

    @Override
    public boolean hasExistingImplementation() {
        return true;
    }

    @Override
    public boolean generateOreFor(MaterialInterface m) {
        return getDimension().equals(MaterialSourceWorld.overworld);
    }
}
