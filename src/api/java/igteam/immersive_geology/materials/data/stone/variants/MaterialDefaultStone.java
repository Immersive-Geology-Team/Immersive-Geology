package igteam.immersive_geology.materials.data.stone.variants;

import igteam.immersive_geology.materials.data.stone.MaterialBaseStone;

public class MaterialDefaultStone extends MaterialBaseStone {
    public MaterialDefaultStone() {
        super("stone");
    }

    @Override
    public int getColor() {
        return 0x8F8F8F;
    }
}
