package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;

public class MaterialMetalThorium extends MaterialBaseMetal {
    public MaterialMetalThorium(){
        super("thorium");
    }

    @Override
    public int getColor() {
        return 0x45484b;
    }
}
