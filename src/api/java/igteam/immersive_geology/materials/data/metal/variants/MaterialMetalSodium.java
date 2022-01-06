package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;

public class MaterialMetalSodium extends MaterialBaseMetal {
    public MaterialMetalSodium(){
        super("sodium");
    }

    @Override
    public int getColor() {
        return 0xd0d5db;
    }
}
