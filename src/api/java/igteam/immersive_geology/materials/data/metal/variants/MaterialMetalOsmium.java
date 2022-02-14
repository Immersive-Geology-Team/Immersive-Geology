package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBasMetal;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialMetalOsmium extends MaterialBasMetal {
    public MaterialMetalOsmium() {
        super("osmium");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0x8A9A9A;
    }

    @Override
    public boolean isNative() {
        return true;
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }
}
