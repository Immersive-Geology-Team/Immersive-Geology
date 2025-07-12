/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.entity.vent;

import blusunrize.immersiveengineering.common.blocks.IEEntityBlock;
import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static blusunrize.immersiveengineering.common.register.IEBlocks.METAL_PROPERTIES_NO_OCCLUSION;

public class IGHydroVent extends IEEntityBlock<IGHydroVentEntity> implements IGBlockType, SimpleWaterloggedBlock
{
	protected final Map<MaterialTexture, MaterialInterface<?>> materialMap = new HashMap<>();
	protected final BlockCategoryFlags category;

	public IGHydroVent(BlockCategoryFlags flag, MaterialInterface<?> material, RegistryObject<BlockEntityType<IGHydroVentEntity>> TYPE)
	{
		super(TYPE, METAL_PROPERTIES_NO_OCCLUSION.get());
		this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
		this.materialMap.put(MaterialTexture.base, material);
		this.category = flag;
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		super.onRemove(state, world, pos, newState, isMoving);
		BlockEntity entity = world.getBlockEntity(pos);
		if(entity instanceof IGHydroVentEntity vent)
		{
			vent.invalidateCaps();
		}
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
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder)
	{
		super.createBlockStateDefinition(pBuilder);
		pBuilder.add(BlockStateProperties.WATERLOGGED);
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

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
	{
		return Shapes.or(Shapes.box(0.0625,0,0.0625,0.9375,0.5625,0.9375),
				Shapes.box(0.125,0.5625,0.125,0.875,1,0.875));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
	{
		return Shapes.or(Shapes.box(0.0625,0,0.0625,0.9375,0.5625,0.9375),
				Shapes.box(0.125,0.5625,0.125,0.875,1,0.875));
	}
}
