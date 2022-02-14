package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBasMetal;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialMetalTin extends MaterialBasMetal {

    public MaterialMetalTin(){
        super("tin");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xd3d4d5;
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.TETRAGONAL;
    }
}
