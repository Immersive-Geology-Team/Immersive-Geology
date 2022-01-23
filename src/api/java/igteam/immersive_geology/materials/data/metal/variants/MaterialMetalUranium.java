package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialMetalUranium extends MaterialBaseMetal {

    public MaterialMetalUranium() {
        super("uranium");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0x759068;
    }

    @Override
    public boolean hasExistingImplementation() {
        return true;
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.ORTHORHOMBIC;
    }

}
