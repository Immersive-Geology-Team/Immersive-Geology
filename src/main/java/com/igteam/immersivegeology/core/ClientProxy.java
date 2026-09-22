package com.igteam.immersivegeology.core;

import com.igteam.immersivegeology.client.manual.IGManualEntries;
import com.igteam.immersivegeology.client.renderer.multiblocks.BallmillRenderer;
import com.igteam.immersivegeology.common.block.multiblocks.entity.TileEntityBallmill;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import com.igteam.immersivegeology.client.pack.IGPackInjector;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;

public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);
		IGPackInjector.inject();
		OBJLoader.INSTANCE.addDomain(IGLib.MODID);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBallmill.class, new BallmillRenderer());
	}

	@Override
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);
		IGManualEntries.register();
	}

	@Override
	public void loadComplete(FMLLoadCompleteEvent event)
	{
		super.loadComplete(event);
		IGPackInjector.populateAndReload();
	}
}
