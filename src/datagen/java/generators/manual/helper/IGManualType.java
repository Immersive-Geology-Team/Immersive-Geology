package generators.manual.helper;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import igteam.api.IGApi;
import igteam.immersive_geology.ImmersiveGeology;

public enum IGManualType {
    item_display(ImmersiveEngineering.MODID),
    crafting(ImmersiveEngineering.MODID),
    table(ImmersiveEngineering.MODID);

    private final String modid;

    private IGManualType(String modid){
        this.modid = modid;
    }

    public String get() {
        return name();
    }
}
