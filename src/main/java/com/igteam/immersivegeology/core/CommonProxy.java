/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core;

import com.igteam.immersivegeology.common.block.entity.cable.IGIMCHandler;
import com.igteam.immersivegeology.common.fluid.IGFluid;
import com.igteam.immersivegeology.common.tag.IGTags;
import com.igteam.immersivegeology.common.world.IGWorldGen;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGContent;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import com.igteam.immersivegeology.core.registration.IGRecipeTypes;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonProxy
{

	public void modConstruction(IEventBus modEventBus)
	{
		IGMultiblockProvider.forceClassLoad();
		IGRegistrationHolder.initialize();
		IGRegistrationHolder.initializeLootModifications();
		IGTags.initialize();
		IGWorldGen.init(modEventBus);
		IGRecipeTypes.init(modEventBus);
		IGContent.initializeIETweaks();
		IGIMCHandler.init();
		IGIMCHandler.handleIMCMessages(InterModComms.getMessages(IGLib.MODID));
	}

	public void registerFluidBehaviour(FMLCommonSetupEvent event)
	{
		event.enqueueWork(() -> {
			for(Fluid value : IGRegistrationHolder.supplyDeferredFluids().get())
			{
				IGFluid fluid = (IGFluid)value;
				DispenserBlock.registerBehavior(fluid.getBucket(), IGFluid.BUCKET_DISPENSE_BEHAVIOR);
			}
		});
	}

	public void reinitializeGUI(){}

	public Level getClientWorld()
	{
		return null;
	}

	public Player getClientPlayer()
	{
		return null;
	}
}
