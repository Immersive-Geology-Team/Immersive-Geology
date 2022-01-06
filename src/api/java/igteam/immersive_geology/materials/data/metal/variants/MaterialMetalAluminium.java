package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import net.minecraft.world.item.Rarity;

public class MaterialMetalAluminium extends MaterialBaseMetal {
    public MaterialMetalAluminium() {
        super("aluminium");
    }

    @Override
    public int getColor() {
        return 0xd0d5db;
    }

    @Override
    public Rarity getRarity() {
        return Rarity.UNCOMMON;
    }
}
