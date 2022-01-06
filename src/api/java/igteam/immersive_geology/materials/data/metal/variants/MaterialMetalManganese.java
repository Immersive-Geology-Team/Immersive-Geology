package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import net.minecraft.world.item.Rarity;

public class MaterialMetalManganese extends MaterialBaseMetal {
    public MaterialMetalManganese() {
        super("manganese");
    }

    @Override
    public int getColor() {
        return 0xaaa9ad;
    }

    @Override
    public Rarity getRarity()
    {
        return Rarity.UNCOMMON;
    }
}
