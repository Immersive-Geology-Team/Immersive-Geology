/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.misc;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MiscEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMisc;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags.Items;



public class MaterialSilicon extends MaterialMisc
{
	public MaterialSilicon()
	{
		super();
		this.name = "silicon";
		removeMaterialFlags(MaterialFlags.IS_ORE_BEARING);
		addFlags(ItemCategoryFlags.METAL_OXIDE, ItemCategoryFlags.SLAG, ItemCategoryFlags.INGOT,
				ItemCategoryFlags.GRIT, ItemCategoryFlags.POWDERED_SLAG, MaterialFlags.HAS_SLURRY);
	}

	@Override
	public ResourceLocation getTextureLocation(IFlagType<?> flag)
	{
		if(flag.getValue() instanceof ItemCategoryFlags i)
		{
			switch(i)
			{
				case INGOT,GRIT,SLAG ->
				{
					return new ResourceLocation(IGLib.MODID, "item/colored/silicon/"+i.getName());
				}
				case POWDERED_SLAG ->
				{
					return new ResourceLocation(IGLib.MODID, "item/greyscale/rock/"+i.getName());
				}
				default ->
				{
					return new ResourceLocation(IGLib.MODID, "item/greyscale/metal/"+i.getName());
				}
			}
		}
		return  null;
	}

	@Override
	public void setupRecipeStages()
	{
		IGMethodBuilder.crushing(this, IGStageDesignation.EXTRACTION)
				.create("crush_quartz_to_oxide", new IngredientWithSize(Items.GEMS_QUARTZ, 1),
						getStack(ItemCategoryFlags.METAL_OXIDE, 1),
						6000, 100);
		IGMethodBuilder.arcSmelting(this, IGStageDesignation.SYNTHESIS)
				.create(ItemCategoryFlags.METAL_OXIDE,1, ItemCategoryFlags.SLAG, 1,
				0, new IngredientWithSize(IETags.coalCokeDust));

		IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION)
				.create(ItemCategoryFlags.SLAG, ItemCategoryFlags.POWDERED_SLAG);

		IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create("leech_silicon_slag_to_slurry",
				ItemStack.EMPTY, ChemicalEnum.HydrochloricAcid.getSlurryWith(MiscEnum.Silicon, IGLib.SLURRY_FROM_ACID_AMOUNT),
				new IngredientWithSize(getItemTag(ItemCategoryFlags.POWDERED_SLAG), 1),
				new FluidTagInput(ChemicalEnum.HydrochloricAcid.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_SLURRY_AMOUNT),
				null, null,
				200, 51200);

		IGMethodBuilder.chemical(this, IGStageDesignation.EXTRACTION).create("sediment_silicon_powder_from_slurry",
				getStack(ItemCategoryFlags.GRIT, 2),
				ChemicalEnum.HydrochloricAcid.getSlurryWith(MetalEnum.Magnesium, IGLib.SLURRY_FROM_ACID_AMOUNT),
				IngredientWithSize.of(MetalEnum.Magnesium.getStack(ItemCategoryFlags.GRIT, 1)),
				new FluidTagInput(ChemicalEnum.HydrochloricAcid.getSlurryTagWith(MiscEnum.Silicon), IGLib.SLURRY_FROM_ACID_AMOUNT),
				null, null, 200, 51200);

		IGMethodBuilder.chemical(this, IGStageDesignation.EXTRACTION).create("sediment_silicon_powder_from_slurry",
				getStack(ItemCategoryFlags.GRIT, 1),
				ChemicalEnum.HydrochloricAcid.getSlurryWith(MetalEnum.Zinc, IGLib.SLURRY_FROM_ACID_AMOUNT),
				IngredientWithSize.of(MetalEnum.Zinc.getStack(ItemCategoryFlags.GRIT, 1)),
				new FluidTagInput(ChemicalEnum.HydrochloricAcid.getSlurryTagWith(MiscEnum.Silicon), IGLib.SLURRY_FROM_ACID_AMOUNT),
				null, null, 200, 51200);

		IGMethodBuilder.arcSmelting(this, IGStageDesignation.SYNTHESIS)
				.create(ItemCategoryFlags.GRIT,1, ItemCategoryFlags.INGOT, 1, 0);
	}
}
