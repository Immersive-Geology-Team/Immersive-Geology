package com.igteam.immersivegeology.core.material.data.mineral;


import com.igteam.immersivegeology.client.helper.IGVeinTextureType;
import com.igteam.immersivegeology.common.block.helper.MineralWeathering;
import com.igteam.immersivegeology.common.world.features.helper.IGGenerationType;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialColorHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeChain;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.item.Items;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialLignite extends MaterialMineral
{

	public MaterialLignite()
	{
		super();
		this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);
		this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);
		removeMaterialFlags(ItemCategoryFlags.values());
		removeMaterialFlags(BlockCategoryFlags.values());
		addFlags(ItemCategoryFlags.POOR_ORE, ItemCategoryFlags.NORMAL_ORE, ItemCategoryFlags.RICH_ORE);
		addFlags(BlockCategoryFlags.ORE_BLOCK);

		addExistingFlag(ModFlags.TFC, BlockCategoryFlags.ORE_BLOCK);
		setBurntime(500);
		CONFIG = new MineralConfig(30, 50, 1, 40, 200, 2000, 0.9,false, Optional.of(BiomeTags.IS_OVERWORLD), IGGenerationType.BANDED);
		this.colorFunction = (flag,v) -> 0x3b3f2e;
	}

	@Override
	public IGVeinTextureType getVeinTextureType()
	{
		return IGVeinTextureType.LAYERED;
	}
}
