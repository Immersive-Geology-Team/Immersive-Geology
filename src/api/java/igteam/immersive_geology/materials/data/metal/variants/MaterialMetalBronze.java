package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalBronze extends MaterialBaseMetal {

    public MaterialMetalBronze() {
        super("bronze");
        initializeColorMap((p) -> (hasColoredTexture(p)) ? 0xFFFFFF : 0xf5d57f);

    }

    @Override
    public Rarity getRarity() {
        return Rarity.UNCOMMON;
    }

    @Override
    public boolean hasCrystal() {
        return false;
    }

    @Override
    public boolean hasCompoundDust() {
        return false;
    }

    @Override
    public boolean hasMetalOxide() {
        return false;
    }


    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements() {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.COPPER),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.TIN)
        ));
    }
}