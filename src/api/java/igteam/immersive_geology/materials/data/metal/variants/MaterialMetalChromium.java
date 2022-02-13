package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public class MaterialMetalChromium extends MaterialBaseMetal {
    public MaterialMetalChromium() {
        super("chromium");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xc6c8c9;
    }
}
