package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBasMetal;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialMetalTungsten extends MaterialBasMetal {

    public MaterialMetalTungsten(){
        super("tungsten");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0x767980;
    }

}
