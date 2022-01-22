package igteam.immersive_geology.materials.data.stone.variants;

import igteam.immersive_geology.materials.data.stone.MaterialBaseStone;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.materials.pattern.MiscPattern;

public class MaterialDefaultStone extends MaterialBaseStone {
    public MaterialDefaultStone() {
        super("stone");
    }

    @Override
    public int getColor(MaterialPattern p) {

        if(p instanceof BlockPattern b){
            return switch(b){
                case ore, block -> 0xFFFFFF;
                default -> 0x8F8F8F;
            };
        }

        if(p instanceof ItemPattern i){
            return switch(i){
                case block_item -> 0xFFFFFF;
                default -> 0x8F8F8F;
            };
        }

        if(p instanceof MiscPattern m){

        }

        return 0xFFFFFF;
    }
}
