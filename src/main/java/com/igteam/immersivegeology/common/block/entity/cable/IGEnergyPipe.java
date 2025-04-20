/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.entity.cable;

import blusunrize.immersiveengineering.common.blocks.IEEntityBlock;
import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static blusunrize.immersiveengineering.common.register.IEBlocks.METAL_PROPERTIES_NO_OCCLUSION;

public class IGEnergyPipe extends IEEntityBlock<IGEnergyPipeEntity> implements IGBlockType
{
	protected final Map<MaterialTexture, MaterialInterface<?>> materialMap = new HashMap<>();
	protected final BlockCategoryFlags category;

	public IGEnergyPipe(BlockCategoryFlags flag, MaterialInterface<?> material)
	{
		super(IGRegistrationHolder.ENERGY_PIPE, METAL_PROPERTIES_NO_OCCLUSION.get().dynamicShape());
		this.materialMap.put(MaterialTexture.base, material);
		this.category = flag;
	}

	@Override
	public Block getIGBlock()
	{
		return this;
	}

	public @NotNull Collection<MaterialInterface<?>> getMaterials() {
		return materialMap.values();
	}

	@Override
	public MaterialInterface<?> getMaterial(MaterialTexture t) {
		return materialMap.get(t);
	}

	@Override
	public IFlagType<?> getFlag()
	{
		return category;
	}

	@Override
	public ItemSubGroup getGroup()
	{
		return category.getSubGroup();
	}

	@Override
	public Map<MaterialTexture, MaterialInterface<?>> getMaterialMap()
	{
		return materialMap;
	}

	@Override
	public int getColor(int index, BlockState state) {
		return materialMap.get(MaterialTexture.values()[index > 0 ? 1 : 0]).getColor(category, 0);
	}

	public int getTransferLimit()
	{
		return 655360;
	}
}
