package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;

public class MaterialMetalOsmium extends MaterialBaseMetal {
    public MaterialMetalOsmium() {
        super("osmium");
    }

    @Override
    public int getColor() {
        return 0x8A9A9A;
    }

    @Override
    public boolean isNative() {
        return true;
    }
}
