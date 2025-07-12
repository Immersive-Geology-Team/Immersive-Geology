/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.entity.vent;

import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.common.blocks.IEBaseBlockEntity;
import blusunrize.immersiveengineering.common.blocks.ticking.IEClientTickableBE;
import blusunrize.immersiveengineering.common.blocks.ticking.IEServerTickableBE;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import blusunrize.immersiveengineering.common.util.ResettableCapability;
import blusunrize.immersiveengineering.common.util.Utils;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Random;

public class IGHydroVentEntity extends IEBaseBlockEntity implements ICapabilityProvider, IEServerTickableBE, IEClientTickableBE
{
	public final FluidTank tank = new FluidTank(250);;
	CapabilityReference<IFluidHandler> ventOutput;
	private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> {
		return this.tank;
	});
	private final Lazy<Fluid> ventType;

	public IGHydroVentEntity(BlockPos pos, BlockState state)
	{
		super(resolveEntityType(state), pos, state);
		this.ventType = () -> state.getBlock() instanceof IGBlockType type ? type.getMaterial(MaterialTexture.base).getFluid(BlockCategoryFlags.FLUID) : null;
		this.ventOutput = CapabilityReference.forNeighbor(this, ForgeCapabilities.FLUID_HANDLER, Direction.UP);
	}

	private static BlockEntityType<?> resolveEntityType(BlockState state) {
		Block block = state.getBlock();

		if (block instanceof IGBlockType ventBlock) {
			MaterialInterface<?> material = ventBlock.getMaterial(MaterialTexture.base);
			String registryKey = BlockCategoryFlags.HYDROVENT.getRegistryKey(material);
			return IGRegistrationHolder.getTE.apply(registryKey);
		}

		// Fallback to Steel if block is not of the expected type
		String fallbackKey = BlockCategoryFlags.HYDROVENT.getRegistryKey(ChemicalEnum.SulfurDioxde);
		return IGRegistrationHolder.getTE.apply(fallbackKey);
	}

	@Override
	public void invalidateCaps()
	{
		super.invalidateCaps();
	}

	@Override
	public void readCustomNBT(CompoundTag compoundTag, boolean b)
	{

	}

	@Override
	public void writeCustomNBT(CompoundTag compoundTag, boolean b)
	{

	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing)
	{
		return capability == ForgeCapabilities.FLUID_HANDLER ? this.holder.cast() : super.getCapability(capability, facing);
	}

	int tick_count = 0;
	@Override
	public void tickServer()
	{
		boolean update = false;
		tick_count++;
		if(tank.getFluidAmount() < 200&&tick_count%10==0)
		{
			if(ventType.get()==null) return;
			tank.fill(new FluidStack(ventType.get(), 10), FluidAction.EXECUTE);
			tick_count = 0;
		}

		if(this.tank.getFluidAmount() > 0)
		{
			int out = Math.min(1000, this.tank.getFluidAmount());
			IFluidHandler handler = ventOutput.getNullable();
			if(handler!=null)
			{
				int accepted = handler.fill(Utils.copyFluidStackWithAmount(this.tank.getFluid(), out, false), FluidAction.SIMULATE);
				FluidStack drained = this.tank.drain(accepted, FluidAction.EXECUTE);
				if(!drained.isEmpty())
				{
					handler.fill(drained, FluidAction.EXECUTE);
					update = true;
				}
			}
		}
		if(update)
		{
			this.setChanged();
			this.markContainingBlockForUpdate((BlockState) null);
		}
	}

	Random rand = new Random();
	@Override
	public void tickClient()
	{
		tick_count++;
		Level level = getLevel();
		if(level != null && tick_count % 4 == 0)
		{
			BlockState state = level.getBlockState(getBlockPos().above());
			boolean canSmoke = state.isAir() || state.is(Blocks.WATER);
			if(!canSmoke) return;
			level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
					getBlockPos().getX()+0.5f+rand.nextFloat(-0.25f, 0.25f), // Adjusted for block position
					(getBlockPos().getY())+1.05f,
					getBlockPos().getZ()+0.5f+rand.nextFloat(-0.25f, 0.25f),
					0, 0.1f, 0f
			);
			tick_count = 0;
		}
	}
}
