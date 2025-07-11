/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.misc;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.types.MaterialMisc;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public class MaterialHighVoltageCable extends MaterialMisc
{
	public MaterialHighVoltageCable()
	{
		super();
		this.name = "high_voltage_cable";
		addFlags(BlockCategoryFlags.ENERGY_PIPE);
		removeMaterialFlags(MaterialFlags.IS_ORE_BEARING);
	}

	public Properties getProperties(IFlagType<?> flag)
	{
		return Properties.of().sound(SoundType.BAMBOO_WOOD).instrument(NoteBlockInstrument.BASEDRUM).strength(10, 20);
	}

	@Override
	public ResourceLocation getTextureLocation(IFlagType<?> flag)
	{
		return new ResourceLocation(IGLib.MODID, "block/static_block/concrete_reinforced");
	}
}
