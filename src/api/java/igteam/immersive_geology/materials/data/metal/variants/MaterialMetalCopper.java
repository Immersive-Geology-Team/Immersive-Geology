package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBasMetal;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialMetalCopper extends MaterialBasMetal {
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

    @Override
    public boolean hasExistingImplementation() {
        return true;
    }
}
