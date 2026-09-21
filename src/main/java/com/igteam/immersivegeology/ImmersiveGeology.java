package com.igteam.immersivegeology;

import com.igteam.immersivegeology.core.CommonProxy;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;

@Mod(
		modid = IGLib.MODID,
		name = IGLib.NAME,
		version = IGLib.VERSION,
		dependencies = IGLib.DEPENDENCIES,
		useMetadata = true
)
public class ImmersiveGeology
{
	@Mod.Instance(IGLib.MODID)
	public static ImmersiveGeology instance;

	@SidedProxy(clientSide = IGLib.PROXY_CLIENT, serverSide = IGLib.PROXY_COMMON)
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		IGLib.IG_LOGGER.info("======== Starting Immersive Geology ========");
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit(event);
	}

	@Mod.EventHandler
	public void loadComplete(FMLLoadCompleteEvent event)
	{
		proxy.loadComplete(event);
	}
}
