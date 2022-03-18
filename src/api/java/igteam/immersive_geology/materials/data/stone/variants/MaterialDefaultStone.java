package igteam.immersive_geology.materials.data.stone.variants;

import igteam.immersive_geology.materials.data.stone.MaterialBaseStone;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.materials.pattern.MiscPattern;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialDefaultStone extends MaterialBaseStone {

    public MaterialDefaultStone() {
        super("stone");
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements() {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SILICON),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 2),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.ALUMINIUM),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 3)
        ));
    }

    @Override
    public int getColor(MaterialPattern p) {

        if(p instanceof BlockPattern){
            BlockPattern b = (BlockPattern) p;
            switch(b){
                case ore: case block: return 0xFFFFFF;
                default: return 0x8F8F8F;
            }
        }

        if(p instanceof ItemPattern){
            ItemPattern i = (ItemPattern) p;
            switch(i){
                case block_item: return 0xFFFFFF;
                default: return 0x8F8F8F;
            }
        }

        if(p instanceof MiscPattern){

        }

        return 0xFFFFFF;
    }

    @Override
    protected boolean hasDefaultBlock() {
        return false;
    }

    @Override
    public boolean hasExistingImplementation() {
        return true;
    }
}
