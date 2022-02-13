package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialMetalVanadium extends MaterialBaseMetal {

    public MaterialMetalVanadium(){
        super("vanadium");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xaaa9ad;
    }
}
