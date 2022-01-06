package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;

public class MaterialMetalChromium extends MaterialBaseMetal {
    public MaterialMetalChromium() {
        super("chromium");
    }

    @Override
    public int getColor() {
        return 0xc6c8c9;
    }
}
