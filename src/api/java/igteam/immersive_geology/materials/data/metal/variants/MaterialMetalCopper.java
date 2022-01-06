package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;

public class MaterialMetalCopper extends MaterialBaseMetal {
    public MaterialMetalCopper() {
        super("copper");
    }

    @Override
    public int getColor() {
        return 0xe39919;
    }

    @Override
    public boolean isNative() {
        return true;
    }
}
