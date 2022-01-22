package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialMetalTin extends MaterialBaseMetal {

    public MaterialMetalTin(){
        super("tin");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xd3d4d5;
    }
}
