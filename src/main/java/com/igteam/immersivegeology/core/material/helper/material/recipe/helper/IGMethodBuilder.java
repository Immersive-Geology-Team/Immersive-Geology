/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.helper;

import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.methods.*;

public class IGMethodBuilder
{
	static IGCraftingMethod crafting(IGRecipeStage parentStage) { return new IGCraftingMethod(parentStage);}
	static IGSeparatorMethod separating(IGRecipeStage parentStage) { return new IGSeparatorMethod(parentStage);}
	static IGChemicalMethod chemical(IGRecipeStage parentStage) { return new IGChemicalMethod(parentStage);}

	static IGBloomeryMethod bloomery(IGRecipeStage parentStage) {return new IGBloomeryMethod(parentStage);}

	static IGRoastingMethod roast(IGRecipeStage parentStage) {return new IGRoastingMethod(parentStage);}

	static IGCalcinationMethod decompose(IGRecipeStage parentStage) {return new IGCalcinationMethod(parentStage);}

	static IGCrystallizationMethod crystalize(IGRecipeStage parentStage) {return new IGCrystallizationMethod(parentStage);}
	static IGBlastingMethod blasting(IGRecipeStage parentStage) { return new IGBlastingMethod(parentStage); }
	static IGCrushingMethod crushing(IGRecipeStage parentStage) {
		return new IGCrushingMethod(parentStage);
	}

	static IGRefineryMethod synthesis (IGRecipeStage parentStage) {return new IGRefineryMethod(parentStage);}
	static IGBasicSmeltingMethod basicSmelting(IGRecipeStage parentStage){ return new IGBasicSmeltingMethod(parentStage); }

	static IGArcSmeltingMethod arcSmelting(IGRecipeStage parentStage) { return new IGArcSmeltingMethod(parentStage);};

	static IGHydrojetMethod cutting(IGRecipeStage parentStage) { return new IGHydrojetMethod(parentStage); };
}
