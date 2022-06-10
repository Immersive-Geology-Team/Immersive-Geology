package igteam.immersive_geology.processing.helper;

import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.methods.*;

public interface IRecipeBuilder {
    static IGCraftingMethod crafting(IGProcessingStage parentStage) { return new IGCraftingMethod(parentStage);}
    static IGSeparatorMethod separating(IGProcessingStage parentStage) { return new IGSeparatorMethod(parentStage);}
    static IGChemicalMethod chemical(IGProcessingStage parentStage) { return new IGChemicalMethod(parentStage);}

    static IGBloomeryMethod bloomery(IGProcessingStage parentStage) {return new IGBloomeryMethod(parentStage);}

    static IGRoastingMethod roast(IGProcessingStage parentStage) {return new IGRoastingMethod(parentStage);}

    static IGCalcinationMethod decompose(IGProcessingStage parentStage) {return new IGCalcinationMethod(parentStage);}

    static IGCrystallizationMethod crystalize(IGProcessingStage parentStage) {return new IGCrystallizationMethod(parentStage);}

    static IGBlastingMethod blasting(IGProcessingStage parentStage) {
        return new IGBlastingMethod(parentStage);
    }

    static IGCrushingMethod crushing(IGProcessingStage parentStage) {
        return new IGCrushingMethod(parentStage);
    }
}
