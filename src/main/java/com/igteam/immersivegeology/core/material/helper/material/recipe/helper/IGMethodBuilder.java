package com.igteam.immersivegeology.core.material.helper.material.recipe.helper;

import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeMethod;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;

public final class IGMethodBuilder
{
	private IGMethodBuilder()
	{
	}

	private static IGRecipeMethod method(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		return new IGRecipeMethod(parentMaterial, stage);
	}

	public static IGRecipeMethod crafting(Object parentStage)
	{
		return new IGRecipeMethod(null, null);
	}

	public static IGRecipeMethod separating(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		return method(parentMaterial, stage);
	}

	public static IGRecipeMethod bloomery(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		return method(parentMaterial, stage);
	}

	public static IGRecipeMethod roast(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		return method(parentMaterial, stage);
	}

	public static IGRecipeMethod chemical(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		return method(parentMaterial, stage);
	}

	public static IGRecipeMethod decompose(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		return method(parentMaterial, stage);
	}

	public static IGRecipeMethod mixing(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		return method(parentMaterial, stage);
	}

	public static IGRecipeMethod crystallize(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		return method(parentMaterial, stage);
	}

	public static IGRecipeMethod blasting(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		return method(parentMaterial, stage);
	}

	public static IGRecipeMethod crushing(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		return method(parentMaterial, stage);
	}

	public static IGRecipeMethod squeezing(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		return method(parentMaterial, stage);
	}

	public static IGRecipeMethod pulverization(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		return method(parentMaterial, stage);
	}

	public static IGRecipeMethod centrifuge(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		return method(parentMaterial, stage);
	}

	public static IGRecipeMethod synthesis(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		return method(parentMaterial, stage);
	}

	public static IGRecipeMethod basicSmelting(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		return method(parentMaterial, stage);
	}

	public static IGRecipeMethod arcSmelting(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		return method(parentMaterial, stage);
	}

	public static IGRecipeMethod cutting(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		return method(parentMaterial, stage);
	}

	public static IGRecipeMethod pelletize(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		return method(parentMaterial, stage);
	}

	public static IGRecipeMethod trommel(MaterialHelper parentMaterial, IGStageDesignation stage)
	{
		return method(parentMaterial, stage);
	}
}
