package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialMetalZirconium extends MaterialBaseMetal {

    public MaterialMetalZirconium() {
        super("zirconium");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xeaeded;
    }
}
