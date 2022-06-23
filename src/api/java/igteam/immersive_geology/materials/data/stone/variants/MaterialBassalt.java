package igteam.immersive_geology.materials.data.stone.variants;

import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.materials.data.stone.MaterialBaseStone;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.helper.MaterialSourceWorld;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.LinkedHashSet;

public class MaterialBassalt extends MaterialBaseStone {
    public MaterialBassalt() {
        super("basalt");

        initializeColorMap((p) -> (0x35302C));
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements() {
        return new LinkedHashSet<>(Collections.singletonList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SILICON)
        ));
    }

    @Override
    public ResourceLocation getTextureLocation(MaterialPattern pattern) {
        if(pattern instanceof BlockPattern){
            BlockPattern b = (BlockPattern) pattern;
            switch(b) {
                case block: case ore: return new ResourceLocation("minecraft", "block/blackstone");
                default: return new ResourceLocation("minecraft", "block/blackstone");
            }
        }

        if(pattern instanceof ItemPattern){
            ItemPattern i = (ItemPattern) pattern;
            switch(i) {
                case ore_chunk: case stone_chunk: return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/rock_chunk");
                case ore_bit: case stone_bit: return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/rock_bit");
                case dirty_crushed_ore: return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/crushed_stone");
                default: return new ResourceLocation("minecraft", "block/netherrack");
            }
        }
        return null;
    }

    @Override
    protected boolean hasDefaultBlock() {
        return false;
    }

    @Override
    public boolean hasExistingImplementation() {
        return true;
    }

    @Override
    public boolean generateOreFor(MaterialInterface m) {
        return getDimension().equals(MaterialSourceWorld.nether);
    }
}
