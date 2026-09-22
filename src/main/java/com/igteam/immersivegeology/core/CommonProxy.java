package com.igteam.immersivegeology.core;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;

public class CommonProxy
{
	public void preInit(FMLPreInitializationEvent event)
	{
	}

	public void init(FMLInitializationEvent event)
	{
		net.minecraftforge.fml.common.network.NetworkRegistry.INSTANCE.registerGuiHandler(
				com.igteam.immersivegeology.ImmersiveGeology.instance,
				new com.igteam.immersivegeology.common.gui.IGGuiHandler());
		com.igteam.immersivegeology.common.block.multiblocks.recipe.IGBallmillRecipes.register();
		com.igteam.immersivegeology.common.block.multiblocks.recipe.IGBloomeryRecipes.register();
		com.igteam.immersivegeology.common.block.multiblocks.recipe.IGRevFurnaceRecipes.register();
	}

	public void postInit(FMLPostInitializationEvent event)
	{
	}

	public void loadComplete(FMLLoadCompleteEvent event)
	{
	}
}
