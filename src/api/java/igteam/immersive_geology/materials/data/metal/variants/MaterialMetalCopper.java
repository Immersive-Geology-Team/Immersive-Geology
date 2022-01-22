package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialMetalCopper extends MaterialBaseMetal {
    public MaterialMetalCopper() {
        super("copper");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xe39919;
    }

    @Override
    public boolean isNative() {
        return true;
    }
}
