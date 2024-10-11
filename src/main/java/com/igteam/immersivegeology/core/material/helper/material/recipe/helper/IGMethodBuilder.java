/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper.material.recipe.helper;

import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.methods.*;

public class IGMethodBuilder
{
	public static IGCraftingMethod crafting(IGRecipeStage parentStage) { return new IGCraftingMethod(parentStage);}
	public static IGSeparatorMethod separating(MaterialHelper parentMaterial, IGStageDesignation stage) { return new IGSeparatorMethod(parentMaterial, stage);}

	public static IGBloomeryMethod bloomery(IGRecipeStage parentStage) {return new IGBloomeryMethod(parentStage);}

	public static IGRoastingMethod roast(MaterialHelper parentMaterial, IGStageDesignation stage) {return new IGRoastingMethod(parentMaterial, stage);}
	public static IGChemicalMethod chemical(MaterialHelper parentMaterial, IGStageDesignation stage) { return new IGChemicalMethod(parentMaterial, stage);}

	public static IGCalcinationMethod decompose(IGRecipeStage parentStage) {return new IGCalcinationMethod(parentStage);}

	public static IGCrystallizationMethod crystalize(MaterialHelper parentMaterial, IGStageDesignation stage) {return new IGCrystallizationMethod(parentMaterial, stage);}
	public static IGBlastingMethod blasting(MaterialHelper parentMaterial, IGStageDesignation stage) { return new IGBlastingMethod(parentMaterial, stage); }
	public static IGCrushingMethod crushing(MaterialHelper parentMaterial, IGStageDesignation stage) {
		return new IGCrushingMethod(parentMaterial, stage);
	}

	public static IGRefineryMethod synthesis (IGRecipeStage parentStage) {return new IGRefineryMethod(parentStage);}
	public static IGBasicSmeltingMethod basicSmelting(IGRecipeStage parentStage){ return new IGBasicSmeltingMethod(parentStage); }

	public static IGArcSmeltingMethod arcSmelting(IGRecipeStage parentStage) { return new IGArcSmeltingMethod(parentStage);};

	public static IGHydrojetMethod cutting(IGRecipeStage parentStage) { return new IGHydrojetMethod(parentStage); };
}
