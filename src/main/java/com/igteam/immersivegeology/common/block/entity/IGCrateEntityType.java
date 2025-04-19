/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.entity;

import blusunrize.immersiveengineering.common.blocks.IEEntityBlock;
import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.block.energypipe.IGEnergyPipeEntity;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class IGCrateEntityType extends IEEntityBlock<IGCrateEntity> implements IGBlockType
{
	protected final Map<MaterialTexture, MaterialInterface<?>> materialMap = new HashMap<>();
	protected final BlockCategoryFlags category;

	public IGCrateEntityType(BlockCategoryFlags flag, MaterialInterface<?> material, RegistryObject<BlockEntityType<IGCrateEntity>> type)
	{
		super(type, Properties.of().sound(SoundType.NETHERITE_BLOCK).mapColor(MapColor.COLOR_LIGHT_GRAY).strength(20,2000), false);
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
}
