package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBasMetal;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialMetalCobalt extends MaterialBasMetal {
    public MaterialMetalCobalt() {
        super("cobalt");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0x0047AB;
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }
}
