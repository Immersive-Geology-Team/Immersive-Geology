package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBasMetal;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import net.minecraft.item.Rarity;

public class MaterialMetalAluminium extends MaterialBasMetal {
    public MaterialMetalAluminium() {
        super("aluminium");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xd0d5db;
    }

    @Override
    public Rarity getRarity() {
        return Rarity.UNCOMMON;
    }
}
