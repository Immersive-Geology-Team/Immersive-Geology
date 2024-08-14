/*
 * BluSunrize
 * Copyright (c) 2024
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.stone.compat.tfc;

import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class MaterialTFCRawStone extends MaterialStone
{
	public MaterialTFCRawStone() {
		super();
		addFlags(MaterialFlags.EXISTING_IMPLEMENTATION);
		addFlags(ModFlags.TFC);
	}

	@Override
	protected Function<IFlagType<?>, Integer> materialColorFunction() {
		return ((p) -> (p == BlockCategoryFlags.ORE_BLOCK ? 0xffffff : 0x888c8d));
	}

	@Override
	public ResourceLocation getTextureLocation(IFlagType<?> flag) {
		return new ResourceLocation("tfc", "block/rock/raw/"+getName());
	}
}

