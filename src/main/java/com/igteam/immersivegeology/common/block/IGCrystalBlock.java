/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block;

import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.level.BlockEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IGCrystalBlock extends IGGenericBlock implements IGBlockType
{

	public static final IntegerProperty AGE = BlockStateProperties.AGE_2;
	protected final Map<MaterialTexture, MaterialInterface<?>> materialMap = new HashMap<>();
	protected final BlockCategoryFlags category;

	public IGCrystalBlock(BlockCategoryFlags flag, MaterialInterface<?> material)
	{
		super(flag, material, Properties.of().randomTicks().sound(SoundType.AMETHYST_CLUSTER).noCollission().strength(1.5f, 1.0f));
		this.registerDefaultState(this.defaultBlockState().setValue(AGE, 0));
		this.materialMap.put(MaterialTexture.base, material);
		this.category = flag;
	}

	public int getMaxAge()
	{
		return 2;
	}

	public BlockState getStateForAge(int pAge) {
		return (BlockState)this.defaultBlockState().setValue(this.getAgeProperty(), pAge);
	}

	@Override
	public @Nullable PushReaction getPistonPushReaction(BlockState state)
	{
		return PushReaction.DESTROY;
	}

	@Override
	public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom)
	{
		if (pLevel.isAreaLoaded(pPos, 1))
		{
			int i = this.getAge(pState);
			int stageIncrement = 1;
			if(pLevel.isRaining()&&pLevel.canSeeSky(pPos.above()))
			{
				if(i > 0) {
					stageIncrement = -1;
				}
				if(i == 0)
				{
					stageIncrement = 0;
				}
			}

			if(i < this.getMaxAge() || stageIncrement < 0)
			{
				float f = 1.0f;
				if(ForgeHooks.onCropsGrowPre(pLevel, pPos, pState, pRandom.nextInt((int)(25.0F/f)+1)==0))
				{
					pLevel.setBlock(pPos, this.getStateForAge(i+stageIncrement), 2);
					ForgeHooks.onCropsGrowPost(pLevel, pPos, pState);
				}
			}
		}
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean moved)
	{
		super.neighborChanged(state, level, pos, neighborBlock, neighborPos, moved);
		for(Direction direction : Direction.values())
		{
			BlockPos adjacentPos = pos.relative(direction);
			BlockState adjacentState = level.getBlockState(adjacentPos);
			if(direction.equals(Direction.DOWN)&&adjacentState.isAir())
			{
				level.destroyBlock(pos, false);
			}
		}
	}

	@Override
	public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction)
	{
		return false;
	}

	public int getAge(BlockState state)
	{
		return state.getValue(this.getAgeProperty());
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		builder.add(AGE);
	}

	protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos)
	{
		return state.getBlock() instanceof IGEvaporateMineralBlock;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
	{
		return this.mayPlaceOn(level.getBlockState(pos), level, pos.below());
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
	public Block getBlock() {
		return this;
	}

	@Override
	public Map<MaterialTexture, MaterialInterface<?>> getMaterialMap() {
		return materialMap;
	}

	public IntegerProperty getAgeProperty()
	{
		return AGE;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
	{
		if (state.getValue(getAgeProperty()) == getMaxAge()) {  // Check if AGE is 3 (fully grown)
			return super.getDrops(state, builder);  // Drops items as usual
		}
		return List.of();
	}

	public ItemLike getItemDrop()
	{
		return getMaterial(MaterialTexture.base).getItem(ItemCategoryFlags.CRYSTAL);
	}
}
