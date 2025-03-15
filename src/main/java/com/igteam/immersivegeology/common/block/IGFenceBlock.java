/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class IGFenceBlock extends FenceBlock implements IGBlockType
{
	protected final Map<MaterialTexture, MaterialInterface<?>> materialMap = new HashMap<>();
	protected final BlockCategoryFlags category;

	public IGFenceBlock(BlockCategoryFlags flag, MaterialInterface<?> material) {
		this(flag, material, material.instance().getProperties());
	}

	public IGFenceBlock(BlockCategoryFlags flag, MaterialInterface<?> material, Properties props) {
		super(props);
		this.materialMap.put(MaterialTexture.base, material);
		this.category = flag;
	}

	@Override
	public Block getBlock()
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
