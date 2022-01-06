package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;

public class MaterialMetalLead extends MaterialBaseMetal {
    public MaterialMetalLead() {
        super("lead");
    }

    @Override
    public int getColor() {
        return 0x444f53;
    }
}
