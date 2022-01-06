package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;

public class MaterialMetalTin extends MaterialBaseMetal {

    public MaterialMetalTin(){
        super("tin");
    }

    @Override
    public int getColor() {
        return 0xd3d4d5;
    }
}
