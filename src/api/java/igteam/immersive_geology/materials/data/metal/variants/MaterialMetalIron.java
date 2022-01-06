package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;

public class MaterialMetalIron extends MaterialBaseMetal {
    public MaterialMetalIron() {
        super("iron");
    }

    @Override
    public int getColor() {
        return 0xd8dada;
    }

    @Override
    public boolean isNative() {
        return true;
    }
}
