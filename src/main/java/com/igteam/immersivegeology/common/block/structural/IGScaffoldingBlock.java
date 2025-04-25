/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.structural;

import blusunrize.immersiveengineering.api.tool.conveyor.ConveyorHandler;
import blusunrize.immersiveengineering.api.tool.conveyor.IConveyorBelt;
import blusunrize.immersiveengineering.api.tool.conveyor.IConveyorType;
import blusunrize.immersiveengineering.common.blocks.generic.ScaffoldingBlock;
import blusunrize.immersiveengineering.common.blocks.metal.ConveyorBeltBlockEntity;
import blusunrize.immersiveengineering.common.blocks.metal.ConveyorBlock;
import blusunrize.immersiveengineering.common.blocks.metal.FluidPipeBlockEntity;
import blusunrize.immersiveengineering.common.blocks.metal.MetalScaffoldingType;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import blusunrize.immersiveengineering.common.register.IEBlocks.MetalDevices;
import blusunrize.immersiveengineering.common.util.loot.ConveyorCoverLootFunction;
import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.block.entity.cable.IGEnergyPipeEntity;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static blusunrize.immersiveengineering.common.register.IEBlocks.METAL_PROPERTIES_NO_OCCLUSION;

public class IGScaffoldingBlock extends ScaffoldingBlock implements IGBlockType
{
	protected final Map<MaterialTexture, MaterialInterface<?>> materialMap = new HashMap<>();
	protected final BlockCategoryFlags category;
	protected final MetalScaffoldingType type;
	public IGScaffoldingBlock(MetalScaffoldingType type, MaterialInterface<?> material)
	{
		super(METAL_PROPERTIES_NO_OCCLUSION.get().explosionResistance(1200));
		this.materialMap.put(MaterialTexture.base, material);
		this.category = BlockCategoryFlags.SCAFFOLDING;
		this.type = type;

		FluidPipeBlockEntity.validPipeCovers.add((input) -> {
			return input == this;
		});
		FluidPipeBlockEntity.climbablePipeCovers.add((input) -> {
			return input == this;
		});
		IGEnergyPipeEntity.validPipeCovers.add((input) -> {
			return input == this;
		});
		IGEnergyPipeEntity.climbablePipeCovers.add((input) -> {
			return input == this;
		});
	}

	public IFlagType<?> getFlag() {
		return category;
	}

	public ItemSubGroup getGroup() {
		return category.getSubGroup();
	}

	@Override
	public int getColor(int index, BlockState state) {
		// By default, we don't need any additional information; the secondaryColors are used for mineral oxidation
		// or other state based color changes
		return materialMap.get(MaterialTexture.values()[index > 0 ? 1 : 0]).getColor(category, 0);
	}

	public @NotNull Collection<MaterialInterface<?>> getMaterials() {
		return materialMap.values();
	}

	@Override
	public MaterialInterface<?> getMaterial(MaterialTexture t) {
		return materialMap.get(t);
	}
	@Override
	public Block getIGBlock() {
		return this;
	}

	public MetalScaffoldingType getScaffoldingType()
	{
		return type;
	}

	@Override
	public Map<MaterialTexture, MaterialInterface<?>> getMaterialMap() {
		return materialMap;
	}
}
