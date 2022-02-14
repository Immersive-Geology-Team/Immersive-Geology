package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBasMetal;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialMetalVanadium extends MaterialBasMetal {

    public MaterialMetalVanadium(){
        super("vanadium");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xaaa9ad;
    }
}
