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
import com.igteam.immersivegeology.client.IGClientRenderHandler;
import com.igteam.immersivegeology.client.models.DrawingTableCallbacks;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = IGLib.MODID, bus = Bus.MOD)
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
	public void modConstruction()
	{
		super.modConstruction();
		IEOBJCallbacks.register(new ResourceLocation(IGLib.MODID, "drawing_table"), DrawingTableCallbacks.INSTANCE);
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
