/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.fluid;

import blusunrize.immersiveengineering.common.register.IEFluids;
import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class IGFluid extends FlowingFluid implements IGBlockType
{
	protected final Map<MaterialTexture, MaterialInterface<?>> materialMap = new HashMap<>();
	protected final BlockCategoryFlags category;
	protected final ItemCategoryFlags bucket_type;
	protected final Supplier<FluidType> type;
	private FluidType cached;
	public static final DispenseItemBehavior BUCKET_DISPENSE_BEHAVIOR = new DefaultDispenseItemBehavior() {
		private final DefaultDispenseItemBehavior defaultBehavior = new DefaultDispenseItemBehavior();

		public ItemStack execute(BlockSource source, ItemStack stack) {
			BucketItem bucketitem = (BucketItem)stack.getItem();
			BlockPos blockpos = source.getPos().relative((Direction)source.getBlockState().getValue(DispenserBlock.FACING));
			Level world = source.getLevel();
			if (bucketitem.emptyContents((Player)null, world, blockpos, (BlockHitResult)null)) {
				bucketitem.checkExtraContent((Player)null, world, stack, blockpos);
				return new ItemStack(Items.BUCKET);
			} else {
				return this.defaultBehavior.dispense(source, stack);
			}
		}
	};

	public IGFluid(BlockCategoryFlags flag, ItemCategoryFlags bucket_type, MaterialInterface<?> material, MaterialInterface<?> overlay)
	{
		this.materialMap.put(MaterialTexture.base, material);
		if(overlay != null)
		{
			this.materialMap.put(MaterialTexture.overlay, overlay);
		}
		this.category = flag;
		this.bucket_type = bucket_type;
		this.type = () -> new IGFluidType(this, material, overlay, category);
	}

	public IFlagType<?> getFlag() {
		return category;
	}

	public ItemSubGroup getGroup() {
		return category.getSubGroup();
	}

	@Override
	public int getColor(int index, BlockState state) {
		return materialMap.get(MaterialTexture.values()[index]).getColor(category, 0);
	}

	@Override
	public MaterialInterface<?> getMaterial(MaterialTexture t) {
		return materialMap.get(t);
	}

	// So I'm sure there are better ways to do this.
	// Originally I was kinda just 'creating' it every time, which caused small issues
	// This just caches the first attempt at getting it, this works, but I think a more elegant method is available.
	@Override
	public @NotNull FluidType getFluidType()
	{
		if(cached == null) cached = this.type.get();
		return cached;
	}


	public @NotNull Collection<MaterialInterface<?>> getMaterials() {
		return materialMap.values();
	}

	@Override
	public Block getIGBlock() {
		IFlagType<?> flag = getFlag();
		String key = materialMap.size() > 1 ? flag.getRegistryKey(getMaterial(MaterialTexture.base), getMaterial(MaterialTexture.overlay)) : flag.getRegistryKey(getMaterial(MaterialTexture.base));
		return IGRegistrationHolder.getBlock.apply(key + "_block");
	}

	@Override
	public Map<MaterialTexture, MaterialInterface<?>> getMaterialMap() {
		return materialMap;
	}

	@Override
	public Fluid getFlowing()
	{
		IFlagType<?> flag = getFlag();
		String key = materialMap.size() > 1 ? flag.getRegistryKey(getMaterial(MaterialTexture.base), getMaterial(MaterialTexture.overlay)) : flag.getRegistryKey(getMaterial(MaterialTexture.base));
		return IGRegistrationHolder.getFluid.apply(key + "_flowing");
	}

	@Override
	public @NotNull Fluid getSource()
	{
		IFlagType<?> flag = getFlag();
		String key = materialMap.size() > 1 ? flag.getRegistryKey(getMaterial(MaterialTexture.base), getMaterial(MaterialTexture.overlay)) : flag.getRegistryKey(getMaterial(MaterialTexture.base));
		return IGRegistrationHolder.getFluid.apply(key);
	}

	@Override
	protected void animateTick(Level pLevel, BlockPos pPos, FluidState pState, RandomSource pRandom)
	{
		super.animateTick(pLevel, pPos, pState, pRandom);
	}

	@Override
	protected void randomTick(Level pLevel, BlockPos pPos, FluidState pState, RandomSource pRandom)
	{
		super.randomTick(pLevel, pPos, pState, pRandom);
	}

	@Nullable
	@Override
	protected ParticleOptions getDripParticle()
	{
		return this.getMaterial(MaterialTexture.base).hasFlag(MaterialFlags.IS_MOLTEN_METAL) ? ParticleTypes.DRIPPING_LAVA : ParticleTypes.DRIPPING_WATER;
	}

	private void fizz(LevelAccessor pLevel, BlockPos pPos) {
		pLevel.levelEvent(1501, pPos, 0);
	}

	@Override
	protected void beforeDestroyingBlock(LevelAccessor level, BlockPos blockPos, BlockState blockState)
	{
		this.fizz(level, blockPos);
	}

	@Override
	public boolean canConvertToSource(FluidState state, Level level, BlockPos pos)
	{
		return false;
	}

	@Override
	protected boolean canConvertToSource(Level level)
	{
		return false;
	}

	@Override
	protected boolean isRandomlyTicking()
	{
		return true;
	}

	@Override
	protected float getExplosionResistance()
	{
		return 100.0F;
	}

	@Override
	public Optional<SoundEvent> getPickupSound()
	{
		return this.getMaterial(MaterialTexture.base).hasFlag(MaterialFlags.IS_MOLTEN_METAL) ? Optional.of(SoundEvents.BUCKET_EMPTY_LAVA) : Optional.of(SoundEvents.BUCKET_EMPTY);
	}

	@Override
	public Item getBucket()
	{
		String key = materialMap.size() > 1 ? bucket_type.getRegistryKey(getMaterial(MaterialTexture.base), getMaterial(MaterialTexture.overlay)) : bucket_type.getRegistryKey(getMaterial(MaterialTexture.base), category);
		return IGRegistrationHolder.getItem.apply(key);
	}

	public int getSlopeFindDistance(LevelReader pLevel) {
		MaterialInterface<?> base = getMaterial(MaterialTexture.base);
		int slopeFind = base.hasFlag(MaterialFlags.IS_MOLTEN_METAL) ? 4 : 8;
		if(pLevel.dimensionType().ultraWarm()) slopeFind = slopeFind / 2;
		return slopeFind;
	}

	public int getDropOff(LevelReader pLevel) {
		return pLevel.dimensionType().ultraWarm() ? 1 : 2;
	}

	public @NotNull BlockState createLegacyBlock(@NotNull FluidState pState) {
		IFlagType<?> flag = getFlag();
		String key = materialMap.size() > 1 ? flag.getRegistryKey(getMaterial(MaterialTexture.base), getMaterial(MaterialTexture.overlay)) : flag.getRegistryKey(getMaterial(MaterialTexture.base));
		return IGRegistrationHolder.getBlock.apply(key + "_block").defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(pState));
	}

	@Override
	protected boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockReader, BlockPos pos, Fluid fluidIn, Direction direction)
	{
		return direction==Direction.DOWN&&!isSame(fluidIn);
	}

	@Override
	public boolean isSame(@Nonnull Fluid fluidIn)
	{
		IFlagType<?> flag = getFlag();
		String key = materialMap.size() > 1 ? flag.getRegistryKey(getMaterial(MaterialTexture.base), getMaterial(MaterialTexture.overlay)) : flag.getRegistryKey(getMaterial(MaterialTexture.base));
		return fluidIn==IGRegistrationHolder.getFluid.apply(key)||fluidIn==IGRegistrationHolder.getFluid.apply(key + "_flowing");
	}

	@Override
	public int getTickDelay(LevelReader level)
	{
		MaterialInterface<?> base = getMaterial(MaterialTexture.base);
		int delay = base.hasFlag(MaterialFlags.IS_MOLTEN_METAL) ? 10 : 5;
		if(level.dimensionType().ultraWarm()) delay = delay /2;
		return delay;
	}

	@Override
	public void tick(Level level, BlockPos pos, FluidState state)
	{
		super.tick(level, pos, state);
		getMaterial(MaterialTexture.base).fluidTick(level, pos, state);

	}

	@Override
	protected void spreadTo(LevelAccessor level, BlockPos pos, BlockState state, Direction direction, FluidState fluidState)
	{
		if(getMaterial(MaterialTexture.base).fluidSpreadEvent(level, pos, state, direction, fluidState)) return;
		super.spreadTo(level, pos, state, direction, fluidState);
	}

	public static class Source extends IGFluid {
		public Source(MaterialInterface<?> material, @Nullable MaterialInterface<?> extra, BlockCategoryFlags flag, ItemCategoryFlags bucket_type) {
			super(flag, bucket_type, material, extra);
		}

		public int getAmount(FluidState pState) {
			return 8;
		}

		public boolean isSource(FluidState pState) {
			return true;
		}
	}

	public static class Flowing extends IGFluid {
		public Flowing(MaterialInterface<?> material, @Nullable MaterialInterface<?> extra, BlockCategoryFlags flag, ItemCategoryFlags bucket_type) {
			super(flag, bucket_type, material, extra);

		}

		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> pBuilder) {
			super.createFluidStateDefinition(pBuilder);
			pBuilder.add(LEVEL);
		}

		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}

		public boolean isSource(@NotNull FluidState state) {
			return false;
		}
	}
}
