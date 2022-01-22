package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialMetalNickel extends MaterialBaseMetal {
    public MaterialMetalNickel() {
        super("nickel");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xb3c1b3;
    }

    @Override
    public boolean isNative() {
        return true;
    }
}