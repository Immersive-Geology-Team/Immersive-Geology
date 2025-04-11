package com.igteam.immersivegeology.core.material.data.mineral;


import com.igteam.immersivegeology.client.helper.IGVeinTextureType;
import com.igteam.immersivegeology.common.world.features.helper.noise.IGGenerationType;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import java.util.Optional;

public class MaterialLignite extends MaterialMineral
{

	public MaterialLignite()
	{
		super();
		this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);
		this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);
		removeMaterialFlags(ItemCategoryFlags.values());
		removeMaterialFlags(BlockCategoryFlags.values());
		addFlags(ItemCategoryFlags.NORMAL_ORE, ItemCategoryFlags.INGOT, ItemCategoryFlags.GRIT);
		addFlags(BlockCategoryFlags.ORE_BLOCK, BlockCategoryFlags.STORAGE_BLOCK);

		addExistingFlag(ModFlags.TFC, BlockCategoryFlags.ORE_BLOCK);
		setBurntime(425);
		CONFIG = new MineralConfig(30, 50, 1, 0, 320, 3500, 0.9,true, Optional.of(BiomeTags.IS_OVERWORLD), IGGenerationType.BANDED);
		this.colorFunction = (flag,v) -> 0xff3b3f2e;
	}

	@Override
	public IGVeinTextureType getVeinTextureType()
	{
		return IGVeinTextureType.LAYERED;
	}

	@Override
	public void setupRecipeStages()
	{
		super.setupRecipeStages();
		IGMethodBuilder.crushing(this, IGStageDesignation.EXTRACTION).create(this, ItemCategoryFlags.NORMAL_ORE, 1, this, ItemCategoryFlags.GRIT, 1, 2400, 200);
		IGMethodBuilder.squeezing(this, IGStageDesignation.REFINEMENT).create(this, ItemCategoryFlags.GRIT, 4, this, ItemCategoryFlags.INGOT, 1, new FluidStack(Fluids.WATER, 250), 19200, 80);
	}

	@Override
	public float getNoiseProbability()
	{
		return 0.14013672f;
	}
}
