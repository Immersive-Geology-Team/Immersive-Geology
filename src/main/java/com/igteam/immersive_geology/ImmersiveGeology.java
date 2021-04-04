// Credit where credit due
// World Generation - Muddykat (Using Modified Source Code from TerraFirmaCraft and YungsBetterCaves)
// GNU GENERAL PUBLIC LICENSE Version 3, 29 June 2007 for YungsBetterCaves sourced assets
// European Union Public Licence V. 1.2 for for TerraFirmaCraft Sourced Assets - this includes most of the biome distribution code
//

package com.igteam.immersive_geology;

import com.igteam.immersive_geology.core.IGLib;
import com.igteam.immersive_geology.core.proxy.ClientProxy;
import com.igteam.immersive_geology.core.proxy.Proxy;
import com.igteam.immersive_geology.core.proxy.ServerProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.eventbus.api.IEventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;

@Mod(IGLib.MODID)
public class ImmersiveGeology
{

	private static final Logger LOGGER = getNewLogger();
	public static Proxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);

	public ImmersiveGeology()
	{
		// Register short-hand variables
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		modBus.addListener(this::setup);
		modBus.addListener(this::onFinishSetup);

		modBus.addListener(this::onClientSetup);

		modBus.addListener(this::onServerStarting);
		modBus.addListener(this::onServerStopped);

		modBus.addListener(this::enqueueIMC);
		modBus.addListener(this::processIMC);

		forgeBus.register(this);
	}

	private void setup(final FMLCommonSetupEvent event){
		LOGGER.info(String.format("Initializing setup for Immersive Geology V'%s'",IGLib.VERSION));
		proxy.onSetup(event);
	}

	private void onFinishSetup(final FMLLoadCompleteEvent event){
		LOGGER.info("Finishing Grimoire Setup");
		proxy.onFinishSetup(event);
	}

	private void onClientSetup(final FMLClientSetupEvent event){
		LOGGER.info("Setting up Client Stuff");
		proxy.onClientSetup(event);
	}

	public void onServerStarting(FMLServerStartingEvent event){
		LOGGER.info("Setting up Server Stuff");
		proxy.onServerStarting(event);
	}

	public void onServerStopped(FMLServerStoppedEvent event){
		LOGGER.info("Finalizing Server Stuff");
		proxy.onServerStopped(event);
	}

	private void enqueueIMC(final InterModEnqueueEvent event)
	{
		LOGGER.info("Setting up Mod Coms");
		proxy.onEnqueueModComs(event);
	}

	private void processIMC(final InterModProcessEvent event)
	{
		LOGGER.info("Processing Coms");
		proxy.onProcessModComs(event);
	}

	public static Logger getNewLogger() {
		return LogManager.getLogger(StackLocatorUtil.getCallerClass(2));
	}
}
