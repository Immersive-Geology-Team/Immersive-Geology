package igteam.immersive_geology.menu;

import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.MineralEnum;
import igteam.immersive_geology.materials.MiscEnum;
import igteam.immersive_geology.materials.StoneEnum;
import igteam.immersive_geology.materials.data.MaterialBase;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;

public enum ItemSubGroup {
    natural(4, "Monazite"),
    processed(11, "Chromium"),
    decoration(1, "Titanium", true),
    misc(2, "Stone");

    private final int patternOrdinal;
    private final String materialName;

    private final boolean useBlockPattern;

    ItemSubGroup(int p, String m, boolean useBlockPattern){
        this.patternOrdinal = p;
        this.materialName = m;
        this.useBlockPattern = useBlockPattern;
    }

    ItemSubGroup(int p, String m){
        this.patternOrdinal = p;
        this.materialName = m;
        this.useBlockPattern = false;
    }

    //We need to do things this way as we cannot introduce a circular reference, this method prevents it. (Order of Operation issue) ~Muddykat
    public MaterialInterface<? extends MaterialBase> getMaterial() {
        try {
            return MetalEnum.valueOf(materialName);
        } catch (Exception ignored){}

        try {
            return MineralEnum.valueOf(materialName);
        } catch (Exception ignored){}

        try {
            return MiscEnum.valueOf(materialName);
        } catch (Exception ignored){}

        try {
            return StoneEnum.valueOf(materialName);
        } catch (Exception ignored){}

        return MetalEnum.Aluminum;
    }

    //We use patternOrdinal to avoid a Circular reference, which would result in a Null pointer error,
    //So using an ordinal can avoid it.
    public MaterialPattern getPattern() {
        return useBlockPattern ? BlockPattern.values()[patternOrdinal] : ItemPattern.values()[patternOrdinal];
    }
}
