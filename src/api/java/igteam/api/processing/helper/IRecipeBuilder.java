package igteam.api.processing.helper;

import igteam.api.processing.methods.*;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.methods.*;

public interface IRecipeBuilder {
    static IGCraftingMethod crafting(IGProcessingStage parentStage) { return new IGCraftingMethod(parentStage);}
    static IGSeparatorMethod separating(IGProcessingStage parentStage) { return new IGSeparatorMethod(parentStage);}
    static IGChemicalMethod chemical(IGProcessingStage parentStage) { return new IGChemicalMethod(parentStage);}

    static IGBloomeryMethod bloomery(IGProcessingStage parentStage) {return new IGBloomeryMethod(parentStage);}

    static IGRoastingMethod roast(IGProcessingStage parentStage) {return new IGRoastingMethod(parentStage);}

    static IGCalcinationMethod decompose(IGProcessingStage parentStage) {return new IGCalcinationMethod(parentStage);}

    static IGCrystallizationMethod crystalize(IGProcessingStage parentStage) {return new IGCrystallizationMethod(parentStage);}
    static IGBlastingMethod blasting(IGProcessingStage parentStage) { return new IGBlastingMethod(parentStage); }
    static IGCrushingMethod crushing(IGProcessingStage parentStage) {
        return new IGCrushingMethod(parentStage);
    }

    static IGRefineryMethod synthesis (IGProcessingStage parentStage) {return new IGRefineryMethod(parentStage);}
    static IGBasicSmeltingMethod basicSmelting(IGProcessingStage parentStage){ return new IGBasicSmeltingMethod(parentStage); }

    static IGArcSmeltingMethod arcSmelting(IGProcessingStage parentStage) { return new IGArcSmeltingMethod(parentStage);};

    static IGHydrojetMethod cutting(IGProcessingStage parentStage) { return new IGHydrojetMethod(parentStage); };
}
