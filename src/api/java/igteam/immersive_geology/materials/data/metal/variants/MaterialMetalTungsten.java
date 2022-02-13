package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialMetalTungsten extends MaterialBaseMetal {

    public MaterialMetalTungsten(){
        super("tungsten");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0x767980;
    }

}
