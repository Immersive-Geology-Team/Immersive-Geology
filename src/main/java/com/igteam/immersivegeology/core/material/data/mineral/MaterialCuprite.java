package com.igteam.immersivegeology.core.material.data.mineral;


import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.CrusherRecipeBuilder;
import com.igteam.immersivegeology.common.block.helper.MineralWeathering;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.builder.BloomeryRecipeBuilder;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialColorHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import com.igteam.immersivegeology.core.material.helper.material.recipe.methods.IGBloomeryMethod;
import net.minecraft.tags.BiomeTags;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialCuprite extends MaterialMineral
{

	public MaterialCuprite()
	{
		super();
		this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);
		this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_EXTRUSIVE);
		this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);
		CONFIG = new MineralConfig(40, 50, 1, 0, 200, 1000, 0.5,false, Optional.of(BiomeTags.IS_OVERWORLD));
	}

	public boolean willTarnishOverTime()
	{
		return true;
	}

	Function<Integer, Integer> coloredWeathering = MaterialColorHelper.setupWeatheredColors(
			List.of(MaterialColorHelper.weatheredColor(MineralWeathering.PRISTINE, 0x9E2A2F),
					MaterialColorHelper.weatheredColor(MineralWeathering.TARNISHED, 0x3D8C7D)));

	@Override
	protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction()
	{
		return ((p, i) -> coloredWeathering.apply(i));
	}

	@Override
	public void setupRecipeStages()
	{
		super.setupRecipeStages();
		IGMethodBuilder.bloomery(this, IGStageDesignation.REFINEMENT).create(ItemCategoryFlags.CRUSHED_ORE, 2,  ItemCategoryFlags.INGOT, 1, 400);
	}

	@Override
	public LinkedHashSet<MaterialInterface<?>> getSourceMaterials()
	{
		return new LinkedHashSet<>(Set.of(MetalEnum.Copper));
	}
}
