package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBasMetal;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialMetalZirconium extends MaterialBasMetal {

    public MaterialMetalZirconium() {
        super("zirconium");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xeaeded;
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }
}
