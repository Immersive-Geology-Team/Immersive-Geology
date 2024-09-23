/*
 * BluSunrize
 * Copyright (c) 2024
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.helper;

import com.igteam.immersivegeology.client.helper.IGRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;

public class TFCCollapseRecipeBuilder extends IGRecipeBuilder<TFCCollapseRecipeBuilder>
{
	protected TFCCollapseRecipeBuilder()
	{
		super(TFCDatagenCompat.invokeCollapseRecipe());
	}

	public static TFCCollapseRecipeBuilder builder(Ingredient result)
	{
		return (TFCCollapseRecipeBuilder) new TFCCollapseRecipeBuilder().addWriter((jsonObject) -> {
			jsonObject.addProperty("ingredient", result.toJson().getAsJsonObject().get("item").getAsString());
		});
	}

	public TFCCollapseRecipeBuilder copyInput(boolean b)
	{
		return (TFCCollapseRecipeBuilder) this.addWriter((jsonObject) -> {
			jsonObject.addProperty("copy_input", b);
		});
	}
}
