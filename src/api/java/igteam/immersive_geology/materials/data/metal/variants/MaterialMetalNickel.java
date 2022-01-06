package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;

public class MaterialMetalNickel extends MaterialBaseMetal {
    public MaterialMetalNickel() {
        super("nickel");
    }

    @Override
    public int getColor() {
        return 0xb3c1b3;
    }

    @Override
    public boolean isNative() {
        return true;
    }
}