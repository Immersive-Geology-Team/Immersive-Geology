package igteam.immersive_geology.materials.data.stone.variants;

import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.materials.data.stone.MaterialBaseStone;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.materials.pattern.MiscPattern;
import net.minecraft.util.ResourceLocation;

public class MaterialGranite extends MaterialBaseStone {
    public MaterialGranite() {
        super("granite");
    }

    @Override
    public int getColor(MaterialPattern p) {

        if(p instanceof BlockPattern){
            BlockPattern b = (BlockPattern) p;
            switch(b){
                case ore: case block: return 0xFFFFFF;
                default: return 0x484A49;
            }
        }

        if(p instanceof ItemPattern){
            ItemPattern i = (ItemPattern) p;
            switch(i){
                case block_item: return 0xFFFFFF;
                default: return 0x484A49;
            }
        }

        if(p instanceof MiscPattern){

        }

        return 0xFFFFFF;
    }

    @Override
    public ResourceLocation getTextureLocation(MaterialPattern pattern) {
        if(pattern instanceof BlockPattern){
            BlockPattern b = (BlockPattern) pattern;
            switch(b) {
                case block: case ore: return new ResourceLocation("minecraft", "block/granite");
                default: return new ResourceLocation("minecraft", "block/granite");
            }
        }

        if(pattern instanceof ItemPattern){
            ItemPattern i = (ItemPattern) pattern;
            switch(i) {
                case ore_chunk: case stone_chunk: return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/rock_chunk");
                case ore_bit: case stone_bit: return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/rock_bit");
                case dirty_crushed_ore: return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/crushed_stone");
                default: return new ResourceLocation("minecraft", "block/granite");
            }
        }
        return null;
    }

    @Override
    public boolean hasExistingImplementation() {
        return true;
    }
}
