package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalLead extends MaterialBaseMetal {

    @Override
    public String getName() {
        return "lead";
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0x444f53;
    }

    @Override
    public ResourceLocation getTextureLocation(MaterialPattern pattern) {
        return pattern == BlockPattern.storage ?
                new ResourceLocation("minecraft", "block/iron_block") : super.getTextureLocation(pattern);
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.LEAD)
        ));
    }
}
