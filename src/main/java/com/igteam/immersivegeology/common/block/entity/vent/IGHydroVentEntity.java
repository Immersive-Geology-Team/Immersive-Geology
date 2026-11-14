/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.entity.vent;

import blusunrize.immersiveengineering.common.blocks.IEBaseBlockEntity;
import blusunrize.immersiveengineering.common.blocks.ticking.IEClientTickableBE;
import blusunrize.immersiveengineering.common.blocks.ticking.IEServerTickableBE;
import blusunrize.immersiveengineering.common.util.Utils;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.Random;
import java.util.function.Supplier;

public class IGHydroVentEntity extends IEBaseBlockEntity implements IEServerTickableBE, IEClientTickableBE
{
	public final FluidTank tank = new FluidTank(250);
	private BlockCapabilityCache<IFluidHandler, Direction> ventOutput;
	private final Supplier<Fluid> ventType;

	public IGHydroVentEntity(BlockPos pos, BlockState state)
	{
		super(resolveEntityType(state), pos, state);
		this.ventType = () -> state.getBlock() instanceof IGBlockType type ? type.getMaterial(MaterialTexture.base).getFluid(BlockCategoryFlags.FLUID) : null;
	}

	private static BlockEntityType<?> resolveEntityType(BlockState state) {
		Block block = state.getBlock();

		if (block instanceof IGBlockType ventBlock) {
			MaterialInterface<?> material = ventBlock.getMaterial(MaterialTexture.base);
			String registryKey = BlockCategoryFlags.HYDROVENT.getRegistryKey(material);
			return IGRegistrationHolder.getTE.apply(registryKey);
		}

		// Fallback to Sulfur Dioxde if block is not of the expected type
		String fallbackKey = BlockCategoryFlags.HYDROVENT.getRegistryKey(ChemicalEnum.SulfurDioxde);
		return IGRegistrationHolder.getTE.apply(fallbackKey);
	}

	@Override
	public void onLoad()
	{
		super.onLoad();
		if(level instanceof ServerLevel serverLevel)
			this.ventOutput = BlockCapabilityCache.create(
					Capabilities.FluidHandler.BLOCK,
					serverLevel,
					getBlockPos().above(),
					Direction.DOWN
			);
	}

	@Override
	public void readCustomNBT(CompoundTag compoundTag, boolean b, HolderLookup.Provider provider)
	{

	}

	@Override
	public void writeCustomNBT(CompoundTag compoundTag, boolean b, HolderLookup.Provider provider)
	{

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
			IFluidHandler handler = ventOutput.getCapability();
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
