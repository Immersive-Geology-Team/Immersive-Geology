package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;

public class MaterialMetalPlatinum extends MaterialBaseMetal {

    public MaterialMetalPlatinum() {
        super("platinum");
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
