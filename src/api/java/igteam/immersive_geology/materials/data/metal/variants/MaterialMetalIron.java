package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialMetalIron extends MaterialBaseMetal {
    public MaterialMetalIron() {
        super("iron");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xd8dada;
    }

    @Override
    public boolean isNative() {
        return true;
    }
}
