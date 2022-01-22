package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialMetalTitanium extends MaterialBaseMetal {

    public MaterialMetalTitanium(){
        super("titanium");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0x878681;
    }
}
