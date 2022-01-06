package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;

public class MaterialMetalZinc extends MaterialBaseMetal {

    public MaterialMetalZinc(){
        super("zinc");
    }

    @Override
    public int getColor() {
        return 0xd0d5db;
    }
}
