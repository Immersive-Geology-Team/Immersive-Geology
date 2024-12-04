/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.item;

import com.igteam.immersivegeology.common.block.IGOreBlock.OreRichness;
import com.igteam.immersivegeology.core.material.data.types.MaterialNativeMetal;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class IGGenericOreItem extends IGGenericItem
{
	private final OreRichness oreRichness;
	public IGGenericOreItem(ItemCategoryFlags flag, MaterialInterface<?> material)
	{
		super(flag, material);
		this.oreRichness = flag.equals(ItemCategoryFlags.RICH_ORE) ? OreRichness.RICH : (flag.equals(ItemCategoryFlags.NORMAL_ORE) ? OreRichness.NORMAL : OreRichness.POOR);
	}

	@Override
	public int getColor(int index) {
		return super.getColor(index);
	}

	@Override
	public @NotNull Component getName(ItemStack stack) {
		String grade = getFlag().equals(ItemCategoryFlags.NORMAL_ORE) ? "normal" : (getFlag().equals(ItemCategoryFlags.RICH_ORE) ? "rich" : "poor");
		MutableComponent normalName = Component.translatable("material.immersivegeology.ore." + grade).append(Component.translatable("formatting.space"));
		for(MaterialTexture t : MaterialTexture.values()){
			if (materialMap.containsKey(t)) {
				MaterialInterface<?> base = materialMap.get(t);

				if(List.of(ItemCategoryFlags.CRUSHED_ORE, ItemCategoryFlags.POOR_ORE, ItemCategoryFlags.NORMAL_ORE, ItemCategoryFlags.RICH_ORE, ItemCategoryFlags.DIRTY_CRUSHED_ORE).contains(getFlag()) && base.instance() instanceof MaterialNativeMetal)
				{
					normalName.append(Component.translatable("material.immersivegeology.native")).append(Component.translatable("formatting.space"));
				}
				normalName.append(Component.translatable("material.immersivegeology." + base.getName()));
			}
		}
		return normalName;
	}

	public OreRichness getOreRichness()
	{
		return this.oreRichness;
	}
}
