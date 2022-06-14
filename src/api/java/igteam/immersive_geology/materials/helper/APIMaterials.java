package igteam.immersive_geology.materials.helper;

import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.MineralEnum;
import igteam.immersive_geology.materials.MiscEnum;
import igteam.immersive_geology.materials.StoneEnum;
import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class APIMaterials {
    public static StoneEnum[] stoneValues() {
        return StoneEnum.values();
    }

    public static HashSet<MaterialInterface<?>> all(){
        HashSet<MaterialInterface<?>> all = new HashSet<>();
        all.addAll(Arrays.asList(StoneEnum.values()));
        all.addAll(Arrays.asList(MetalEnum.values()));
        all.addAll(Arrays.asList(MineralEnum.values()));
        all.addAll(Arrays.asList(MiscEnum.values()));
        return all;
    }

    public static HashSet<MaterialInterface<?>> generatedMaterials(){
        HashSet<MaterialInterface<?>> all = new HashSet<>();
        List<MaterialInterface<?>> metals = new ArrayList<>();
        for (MaterialInterface<?> metal : MetalEnum.values()) {
            if(metal.get() instanceof MaterialBaseMetal){
                MaterialBaseMetal m = (MaterialBaseMetal) metal.get();
                if(m.isNative()){
                    metals.add(metal);
                }
            }
        }

        all.addAll(metals);
        all.addAll(Arrays.asList(MineralEnum.values()));
        return all;
    }

}
