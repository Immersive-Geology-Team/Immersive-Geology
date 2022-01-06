package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;

public class MaterialMetalSilver extends MaterialBaseMetal {

    public MaterialMetalSilver() {
        super("silver");
    }

    @Override
    public int getColor() {
        return 0xe7e7f7;
    }

    @Override
    public boolean isNative() {
        return true;
    }
}
