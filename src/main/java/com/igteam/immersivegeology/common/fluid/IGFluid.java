/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.fluid;

import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraftforge.fluids.FluidStack;
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
		return 0xff000000 | materialMap.get(MaterialTexture.values()[index]).getColor(category);
	}


	@Override
	public MaterialInterface<?> getMaterial(MaterialTexture t) {
		return materialMap.get(t);
	}

	@Override
	public @NotNull FluidType getFluidType()
	{
		return new FluidType(getMaterial(MaterialTexture.base).getFluidProperties()){
			@Override
			public Component getDescription()
			{
				List<String> materialList = new ArrayList<>();
				String molten = "";
				if(materialMap.get(MaterialTexture.base) instanceof MaterialMetal){
					materialList.add(I18n.get("material.immersivegeology.fluid_type.molten"));
					molten = "_molten";
				}

				for(MaterialTexture t : MaterialTexture.values()){
					if (materialMap.containsKey(t)) {
						materialList.add(I18n.get("material.immersivegeology." + materialMap.get(t).getName()));
					}
				}
				if(category.equals(BlockCategoryFlags.SLURRY)) materialList.add(I18n.get("material.immersivegeology.fluid_type.slurry"));

				return Component.translatable("fluid.immersivegeology." + category.getName().toLowerCase() + molten, materialList.toArray());
			}

			@Override
			public Component getDescription(FluidStack stack)
			{
				List<String> materialList = new ArrayList<>();
				String type = "fluid";
				MaterialInterface<?> baseMaterial = getMaterial(MaterialTexture.base);
				MaterialInterface<?> overlayMaterial = getMaterial(MaterialTexture.overlay);

 				if(baseMaterial instanceof MetalEnum)
				{
					type = "fluid_molten";
				}

				if(baseMaterial instanceof ChemicalEnum && overlayMaterial != null)
				{
					type = "slurry";
					materialList.add(I18n.get("material.immersivegeology." + overlayMaterial.getName()));
					materialList.add(I18n.get("component.immersivegeology." + baseMaterial.getName()));
				} else {
					materialList.add(I18n.get("material.immersivegeology." + baseMaterial.getName()));
				}

				return Component.translatable("fluid.immersivegeology." + type, materialList.toArray());
			}

			public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
				consumer.accept(getFluidExtendedProperties(category));
			}
		};
	}

	public IClientFluidTypeExtensions getFluidExtendedProperties(BlockCategoryFlags flag)
	{
		IGFluid fluid = this;
		MaterialInterface<?> base = fluid.getMaterial(MaterialTexture.base);
		MaterialInterface<?> overlay = fluid.getMaterial(MaterialTexture.overlay);
		return new IClientFluidTypeExtensions()
		{
			@Override
			public int getTintColor()
			{
				return 0xFF000000 | (overlay != null ? overlay.getColor(flag) : base.getColor(flag));
			}

			@Override
			public ResourceLocation getStillTexture()
			{
				return base.hasFlag(MaterialFlags.IS_MOLTEN_METAL) ? new ResourceLocation(IGLib.MODID, "block/fluid/molten_still") : new ResourceLocation(IGLib.MODID, "block/fluid/default_still");
			}

			@Override
			public ResourceLocation getFlowingTexture()
			{
				return base.hasFlag(MaterialFlags.IS_MOLTEN_METAL) ? new ResourceLocation(IGLib.MODID, "block/fluid/molten_flow") : new ResourceLocation(IGLib.MODID, "block/fluid/default_flowing");
			}
		};
	}

	public Collection<MaterialInterface<?>> getMaterials() {
		return materialMap.values();
	}

	@Override
	public Block getBlock() {
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
	public Fluid getSource()
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
		String key = materialMap.size() > 1 ? ItemCategoryFlags.BUCKET.getRegistryKey(getMaterial(MaterialTexture.base), getMaterial(MaterialTexture.overlay)) : ItemCategoryFlags.BUCKET.getRegistryKey(getMaterial(MaterialTexture.base), category);
		return IGRegistrationHolder.getItem.apply(key);
	}

	public int getSlopeFindDistance(LevelReader pLevel) {
		return pLevel.dimensionType().ultraWarm() ? 4 : 2;
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
		return level.dimensionType().ultraWarm() ? 10 : 30;
	}

	public static class Source extends IGFluid {
		public Source(MaterialInterface<?> material, @Nullable MaterialInterface<?> extra, BlockCategoryFlags flag) {
			super(flag, material);
			if(extra != null) this.materialMap.put(MaterialTexture.overlay, extra);
		}

		public int getAmount(FluidState pState) {
			return 8;
		}

		public boolean isSource(FluidState pState) {
			return true;
		}
	}

	public static class Flowing extends IGFluid {
		public Flowing(MaterialInterface<?> material, @Nullable MaterialInterface<?> extra, BlockCategoryFlags flag) {
			super(flag, material);
			if(extra != null) this.materialMap.put(MaterialTexture.overlay, extra);
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
