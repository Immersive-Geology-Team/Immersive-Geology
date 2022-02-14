package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBasMetal;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialMetalChromium extends MaterialBasMetal {
    public MaterialMetalChromium() {
        super("chromium");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xc6c8c9;
    }
}
