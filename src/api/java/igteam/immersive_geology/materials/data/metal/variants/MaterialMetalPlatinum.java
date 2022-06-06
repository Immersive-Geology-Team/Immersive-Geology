package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalPlatinum extends MaterialBaseMetal {


    public MaterialMetalPlatinum() {
        super("platinum");
    }

    @Override
    public int getColor(MaterialPattern p) {

        if ((p == ItemPattern.ingot) || (p == ItemPattern.nugget) || (p == ItemPattern.plate)
                || (p == ItemPattern.block_item) || (p == BlockPattern.storage))
            return 0xFFFFFF;
        return p == ItemPattern.metal_oxide ? 0x65698C : 0xe7e7f7;
    }

    @Override
    public boolean isNative() {
        return true;
    }

    @Override
    public ResourceLocation getTextureLocation(MaterialPattern pattern) {
        if (pattern instanceof BlockPattern)
        {
            BlockPattern i = (BlockPattern) pattern;
            switch (i)
            {
                case storage: return new ResourceLocation(IGApi.MODID, "block/colored/platinum/storage");

                //case sheetmetal:return new ResourceLocation(IGApi.MODID, "block/colored/platinum/sheetmetal");
            }
        }
        if (pattern instanceof ItemPattern)
        {
            ItemPattern i = (ItemPattern) pattern;
            switch (i)
            {
                case ingot:
                    return new ResourceLocation(IGApi.MODID, "item/colored/platinum/ingot");
                case nugget:
                    return new ResourceLocation(IGApi.MODID, "item/colored/platinum/nugget");
                case plate:
                    return new ResourceLocation(IGApi.MODID, "item/colored/platinum/plate");
            }
        }
        return super.getTextureLocation(pattern);
    }

    @Override
    protected boolean hasMetalOxide() {
        return true;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.PLATINUM)
        ));
    }
}
