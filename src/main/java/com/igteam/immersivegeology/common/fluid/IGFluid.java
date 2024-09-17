/*
 * BluSunrize
 * Copyright (c) 2024
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.fluid;

import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;

public abstract class IGFluid extends FlowingFluid implements IGBlockType
{
	protected final Map<MaterialTexture, MaterialInterface<?>> materialMap = new HashMap<>();
	protected final BlockCategoryFlags category;

	public IGFluid(BlockCategoryFlags flag, MaterialInterface<?> material)
	{
		this.materialMap.put(MaterialTexture.base, material);
		this.category = flag;
	}

	public IFlagType<?> getFlag() {
		return category;
	}

	public ItemSubGroup getGroup() {
		return category.getSubGroup();
	}

	@Override
	public int getColor(int index) {
		return materialMap.get(MaterialTexture.values()[index]).getColor(category);
	}

	@Override
	public MaterialInterface<?> getMaterial(MaterialTexture t) {
		return materialMap.get(t);
	}

	@Override
	public @NotNull FluidType getFluidType()
	{
		return new FluidType(getMaterial(MaterialTexture.base).getFluidProperties()){
			public double motionScale(Entity entity) {
				return entity.level().dimensionType().ultraWarm() ? 0.007 : 0.0023333333333333335;
			}

			public void setItemMovement(ItemEntity entity) {
				Vec3 vec3 = entity.getDeltaMovement();
				entity.setDeltaMovement(vec3.x * 0.949999988079071, vec3.y + (double)(vec3.y < 0.05999999865889549 ? 5.0E-4F : 0.0F), vec3.z * 0.949999988079071);
			}

			public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
				consumer.accept(getMaterial(MaterialTexture.base).getFluidExtendedProperties());
			}
		};
	}

	public Collection<MaterialInterface<?>> getMaterials() {
		return materialMap.values();
	}

	@Override
	public Block getBlock() {
		return IGRegistrationHolder.getBlock.apply(getFlag().getRegistryKey(getMaterial(MaterialTexture.base)) + "_block");
	}

	@Override
	public Map<MaterialTexture, MaterialInterface<?>> getMaterialMap() {
		return materialMap;
	}

	@Override
	public Fluid getFlowing()
	{
		return IGRegistrationHolder.getFluid.apply(this.getFlag().getRegistryKey(this.getMaterial(MaterialTexture.base)) + "_flowing");
	}

	@Override
	public Fluid getSource()
	{
		return IGRegistrationHolder.getFluid.apply(this.getFlag().getRegistryKey(this.getMaterial(MaterialTexture.base)));
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
		return IGRegistrationHolder.getItem.apply(ItemCategoryFlags.BUCKET.getRegistryKey(this.getMaterial(MaterialTexture.base), category));
	}

	public int getSlopeFindDistance(LevelReader pLevel) {
		return pLevel.dimensionType().ultraWarm() ? 4 : 2;
	}

	public int getDropOff(LevelReader pLevel) {
		return pLevel.dimensionType().ultraWarm() ? 1 : 2;
	}

	public BlockState createLegacyBlock(FluidState pState) {
		return IGRegistrationHolder.getBlock.apply(getFlag().getRegistryKey(getMaterial(MaterialTexture.base)) + "_block").defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(pState));
	}

	@Override
	protected boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockReader, BlockPos pos, Fluid fluidIn, Direction direction)
	{
		return direction==Direction.DOWN&&!isSame(fluidIn);
	}

	@Override
	public boolean isSame(@Nonnull Fluid fluidIn)
	{
		return fluidIn==IGRegistrationHolder.getFluid.apply(getFlag().getRegistryKey(getMaterial(MaterialTexture.base)))||fluidIn==IGRegistrationHolder.getFluid.apply(getFlag().getRegistryKey(getMaterial(MaterialTexture.base))+ "_flowing");
	}

	@Override
	public int getTickDelay(LevelReader level)
	{
		return level.dimensionType().ultraWarm() ? 10 : 30;
	}

	public static class Source extends IGFluid {
		public Source(MaterialInterface<?> material) {
			super(BlockCategoryFlags.FLUID, material);
		}

		public int getAmount(FluidState pState) {
			return 8;
		}

		public boolean isSource(FluidState pState) {
			return true;
		}
	}

	public static class Flowing extends IGFluid {
		public Flowing(MaterialInterface<?> material) {
			super(BlockCategoryFlags.FLUID, material);
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
