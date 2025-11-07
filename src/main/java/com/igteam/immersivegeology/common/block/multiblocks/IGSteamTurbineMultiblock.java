/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import com.igteam.immersivegeology.common.block.multiblocks.logic.AlternatorLogic;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Consumer;

public class IGSteamTurbineMultiblock extends IGTemplateMultiblock
{
	public static final IGSteamTurbineMultiblock INSTANCE = new IGSteamTurbineMultiblock();
	public IGSteamTurbineMultiblock()
	{
		super(new ResourceLocation(IGLib.MODID, "multiblocks/steam_turbine"), new BlockPos(1, 1, 2), new BlockPos(0,1,11), new BlockPos(3,5,12), IGMultiblockProvider.STEAM_TURBINE);
	}

	@Override
	public boolean createStructure(Level world, BlockPos pos, Direction side, Player player)
	{
		final boolean excavatorFormed = super.createStructure(world, pos, side, player);
		if(excavatorFormed)
		{
			BlockEntity clickedTE = world.getBlockEntity(pos);
			if(clickedTE instanceof IMultiblockBE<?> excavator)
			{
				final IMultiblockLevel mbLevel = excavator.getHelper().getContext().getLevel();
				BlockPos wheelCenter = mbLevel.toAbsolute(AlternatorLogic.FORMATION_LOC);
				IGAlternatorMultiblock.INSTANCE.createStructure(world, wheelCenter, side.getCounterClockWise(), player);
			}
		}
		return excavatorFormed;
	}

	@Override
	public boolean canFormWithDefaultHammer()
	{
		return true;
	}

	@Override
	public float getManualScale()
	{
		return 8;
	}

	@Override
	public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer) {
		consumer.accept(new IGClientMultiblockProperties(this, 2.5, 0.5, 1.5));
	}

	@Override
	public String getName()
	{
		return "Steam Turbine";
	}

	@Override
	public int getDefaultBatchInput()
	{
		return 8;
	}

	@Override
	public int getDefaultBatchOutput()
	{
		return 8;
	};

	@Override
	public int getDefaultTime()
	{
		return 800;
	};

	@Override
	public int getDefaultEnergy()
	{
		return 64000;
	};
}
