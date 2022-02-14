package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBasMetal;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialMetalTitanium extends MaterialBasMetal {

    public MaterialMetalTitanium(){
        super("titanium");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0x878681;
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

}
