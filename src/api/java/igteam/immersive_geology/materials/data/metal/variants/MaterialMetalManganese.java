package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import net.minecraft.item.Rarity;

public class MaterialMetalManganese extends MaterialBaseMetal {
    public MaterialMetalManganese() {
        super("manganese");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xaaa9ad;
    }

    @Override
    public Rarity getRarity()
    {
        return Rarity.UNCOMMON;
    }

}
