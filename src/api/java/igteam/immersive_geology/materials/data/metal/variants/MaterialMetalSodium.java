package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialMetalSodium extends MaterialBaseMetal {
    public MaterialMetalSodium(){
        super("sodium");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xd0d5db;
    }
}
