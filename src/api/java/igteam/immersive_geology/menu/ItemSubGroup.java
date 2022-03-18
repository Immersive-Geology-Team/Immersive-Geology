package igteam.immersive_geology.menu;

import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.StoneEnum;
import igteam.immersive_geology.materials.data.MaterialBase;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.ItemPattern;

public enum ItemSubGroup {
    natural(3, "Gold"),
    processed(0, "Aluminium"),
    machines(11, "Tungsten"),
    misc(2, "Stone");

    private final int patternOrdinal;
    private final String materialName;

    ItemSubGroup(int p, String m){
        this.patternOrdinal = p;
        this.materialName = m;
    }

    //We need to do things this way as we cannot introduce a circular reference, this method prevents it. (Order of Operation issue) ~Muddykat
    public MaterialInterface<? extends MaterialBase> getMaterial() {
        try {
            return MetalEnum.valueOf(materialName);
        } catch (Exception ignored){}

        try {
            return StoneEnum.valueOf(materialName);
        } catch (Exception ignored){}

        return MetalEnum.Aluminium;
    }

    //We use patternOrdinal to avoid a Circular reference, which would result in a Null pointer error,
    //So using an ordinal can avoid it.
    public ItemPattern getPattern() {
        return ItemPattern.values()[patternOrdinal];
    }


}
