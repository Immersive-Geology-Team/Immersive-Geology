package igteam.immersive_geology.processing.helper;

import igteam.immersive_geology.processing.methods.IGCraftingMethod;
import igteam.immersive_geology.processing.methods.IGSeparatorMethod;

public interface IRecipeBuilder {
    static IGCraftingMethod crafting() { return new IGCraftingMethod();}
    static IGSeparatorMethod separating() { return new IGSeparatorMethod();}

}
