package igteam.immersive_geology.processing.helper;

import igteam.immersive_geology.materials.data.MaterialBase;
import igteam.immersive_geology.processing.IGProcessingStage;

import java.util.HashMap;
import java.util.Set;

public class StageProvider {

    private static HashMap<MaterialBase, Set<IGProcessingStage>> stageMap = new HashMap<>();

    public static void add(MaterialBase material, Set<IGProcessingStage> stage){
        stageMap.put(material, stage);
    }

    public static Set<IGProcessingStage> get(MaterialBase material) {
        return stageMap.get(material);
    }
}
