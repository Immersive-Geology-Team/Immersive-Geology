package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBasMetal;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialMetalZinc extends MaterialBasMetal {

    public MaterialMetalZinc(){
        super("zinc");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xd0d5db;
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }
}
