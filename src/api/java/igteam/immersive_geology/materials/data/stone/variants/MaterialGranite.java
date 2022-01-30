package igteam.immersive_geology.materials.data.stone.variants;

import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.materials.data.stone.MaterialBaseStone;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.materials.pattern.MiscPattern;
import net.minecraft.resources.ResourceLocation;

public class MaterialGranite extends MaterialBaseStone {
    public MaterialGranite() {
        super("granite");
    }

    @Override
    public int getColor(MaterialPattern p) {

        if(p instanceof BlockPattern b){
            return switch(b){
                case ore, block -> 0xFFFFFF;
                default -> 0x484A49;
            };
        }

        if(p instanceof ItemPattern i){
            return switch(i){
                case block_item -> 0xFFFFFF;
                default -> 0x484A49;
            };
        }

        if(p instanceof MiscPattern m){

        }

        return 0xFFFFFF;
    }

    @Override
    public ResourceLocation getTextureLocation(MaterialPattern pattern) {
        if(pattern instanceof BlockPattern b){
            return switch(b) {
                case block, ore -> new ResourceLocation("minecraft", "block/granite");
                default -> new ResourceLocation("minecraft", "block/granite");
            };
        }

        if(pattern instanceof ItemPattern i){
            return switch(i) {
                case ore_chunk, stone_chunk -> new ResourceLocation(IGApi.MODID, "item/greyscale/rock/rock_chunk");
                case ore_bit, stone_bit -> new ResourceLocation(IGApi.MODID, "item/greyscale/rock/rock_bit");
                case dirty_crushed_ore -> new ResourceLocation(IGApi.MODID, "item/greyscale/rock/crushed_stone");
                default -> new ResourceLocation("minecraft", "block/granite");
            };
        }
        return null;
    }

    @Override
    public boolean hasExistingImplementation() {
        return true;
    }
}
