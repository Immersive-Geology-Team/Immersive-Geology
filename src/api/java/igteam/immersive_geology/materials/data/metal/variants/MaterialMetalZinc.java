package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialMetalZinc extends MaterialBaseMetal {

    public MaterialMetalZinc(){
        super("zinc");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xd0d5db;
    }
}
