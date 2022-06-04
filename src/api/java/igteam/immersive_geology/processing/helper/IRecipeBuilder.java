package igteam.immersive_geology.processing.helper;

import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.methods.IGBloomeryMethod;
import igteam.immersive_geology.processing.methods.IGChemicalMethod;
import igteam.immersive_geology.processing.methods.IGCraftingMethod;
import igteam.immersive_geology.processing.methods.IGSeparatorMethod;

public interface IRecipeBuilder {
    static IGCraftingMethod crafting(IGProcessingStage parentStage) { return new IGCraftingMethod(parentStage);}
    static IGSeparatorMethod separating(IGProcessingStage parentStage) { return new IGSeparatorMethod(parentStage);}
    static IGChemicalMethod chemical(IGProcessingStage parentStage) { return new IGChemicalMethod(parentStage);}

    static IGBloomeryMethod bloomery(IGProcessingStage parentStage) {return new IGBloomeryMethod(parentStage);}

}
