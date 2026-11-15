/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core;


import blusunrize.immersiveengineering.api.client.ieobj.IEOBJCallbacks;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import com.igteam.immersivegeology.common.block.entity.cable.EnergyPipeCallback;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;

public class ClientProxy extends CommonProxy
{

	@Override
	public void reinitializeGUI()
	{
		Screen currentScreen = Minecraft.getInstance().screen;
		if(currentScreen instanceof IEContainerScreen)
			currentScreen.init(Minecraft.getInstance(), currentScreen.width, currentScreen.height);
	}

	@Override
	public void modConstruction(IEventBus modEventBus)
	{
		super.modConstruction(modEventBus);
		IEOBJCallbacks.register(ResourceLocation.fromNamespaceAndPath(IGLib.MODID, "energy_pipe"), EnergyPipeCallback.INSTANCE);
	}

	@Override
	public Level getClientWorld()
	{
		return Minecraft.getInstance().level;
	}

	@Override
	public Player getClientPlayer()
	{
		return Minecraft.getInstance().player;
	}
}
