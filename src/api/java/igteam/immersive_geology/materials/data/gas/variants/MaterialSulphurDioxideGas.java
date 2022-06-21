package igteam.immersive_geology.materials.data.gas.variants;

import igteam.immersive_geology.materials.data.gas.MaterialBaseGas;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.pattern.ItemPattern;

import java.util.LinkedHashSet;

public class MaterialSulphurDioxideGas extends MaterialBaseGas {
    public MaterialSulphurDioxideGas() {
        super("sulphur_dioxide");
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements() {
        return null;
    }

    @Override
    public boolean isFluidPortable(ItemPattern bucket) {
        return false;
    }
}
