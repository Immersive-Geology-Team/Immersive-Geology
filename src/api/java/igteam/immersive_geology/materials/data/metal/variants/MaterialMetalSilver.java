package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBasMetal;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialMetalSilver extends MaterialBasMetal {

    public MaterialMetalSilver() {
        super("silver");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xe7e7f7;
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
