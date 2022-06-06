package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.helper.PeriodicTableElement.ElementProportion;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalChromium extends MaterialBaseMetal {

    public MaterialMetalChromium() {
        super("chromium");
    }

    @Override
    public int getColor(MaterialPattern p) {
        if ((p == ItemPattern.ingot) || (p == ItemPattern.nugget) || (p == ItemPattern.plate)
                || (p == ItemPattern.block_item) || (p == BlockPattern.storage))
            return 0xFFFFFF;
        return 0xD7B4F3;
    }

    @Override
    public ResourceLocation getTextureLocation(MaterialPattern pattern) {
        if (pattern instanceof BlockPattern)
        {
            BlockPattern i = (BlockPattern) pattern;
            switch (i)
            {
                case storage: return new ResourceLocation(IGApi.MODID, "block/colored/chromium/storage");

                //case sheetmetal:return new ResourceLocation(IGApi.MODID, "block/colored/chromium/sheetmetal");
            }
        }
        if (pattern instanceof ItemPattern)
        {
            ItemPattern i = (ItemPattern) pattern;
            switch (i)
            {
                case ingot:
                    return new ResourceLocation(IGApi.MODID, "item/colored/chromium/ingot");
                case nugget:
                    return new ResourceLocation(IGApi.MODID, "item/colored/chromium/nugget");
                case plate:
                    return new ResourceLocation(IGApi.MODID, "item/colored/chromium/plate");
            }
        }
        return super.getTextureLocation(pattern);
    }

    @Override
    public LinkedHashSet<ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new ElementProportion(PeriodicTableElement.CHROMIUM)
        ));
    }
}
