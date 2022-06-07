package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.helper.PeriodicTableElement.ElementProportion;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.function.Function;

public class MaterialMetalChromium extends MaterialBaseMetal {

    public MaterialMetalChromium() {
        super("chromium");
        initializeColorMap((p) -> 0xD7B4F3);
    }

    @Override
    public LinkedHashSet<ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new ElementProportion(PeriodicTableElement.CHROMIUM)
        ));
    }
}
