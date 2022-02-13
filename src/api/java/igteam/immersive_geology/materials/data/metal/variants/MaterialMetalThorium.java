package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialMetalThorium extends MaterialBaseMetal {
    public MaterialMetalThorium(){
        super("thorium");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0x45484b;
    }
}
