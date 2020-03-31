package com.igteam.immersivegeology;

import org.apache.logging.log4j.LogManager;

import com.igteam.immersivegeology.client.ClientProxy;
import com.igteam.immersivegeology.client.menu.IGItemGroup;
import com.igteam.immersivegeology.client.menu.handler.CreativeMenuHandler;
import com.igteam.immersivegeology.common.CommonProxy;
import com.igteam.immersivegeology.common.IGContent;
import com.igteam.immersivegeology.common.util.IGLogger;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(ImmersiveGeology.MODID)
public class ImmersiveGeology
{
	public static final String MODID = "immersivegeology";
	public static final String MODNAME = "Immersive Geology";
	public static final String VERSION = "${version}";
	public static final String NETWORK_VERSION = "1";
	public static final SimpleChannel packetHandler = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(MODID, "main"))
			.networkProtocolVersion(() -> NETWORK_VERSION)
			.serverAcceptedVersions(NETWORK_VERSION::equals)
			.clientAcceptedVersions(NETWORK_VERSION::equals)
			.simpleChannel();
	public static CommonProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
	public static final IGItemGroup IGgroup = new IGItemGroup(MODID);

	public ImmersiveGeology()
	{
		IGLogger.logger = LogManager.getLogger(MODID);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);

		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarted);
		IGContent.modConstruction();
	}

	@SubscribeEvent
	public void clientSetup(FMLClientSetupEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new CreativeMenuHandler()); 
	}
	
	@SubscribeEvent
	public void setup(FMLCommonSetupEvent event)
	{
		//Previously in PREINIT
		proxy.preInit();

		proxy.preInitEnd();
		//Previously in INIT
		proxy.init();
		IGContent.init();

		proxy.initEnd();
		//Previously in POSTINIT
		proxy.postInit();
		IGContent.postInit();

		proxy.postInitEnd();
	}

	@SubscribeEvent
	public void loadComplete(FMLLoadCompleteEvent event)
	{

	}

	@SubscribeEvent
	public void serverStarting(FMLServerStartingEvent event)
	{
		proxy.serverStarting();
	}

	@SubscribeEvent
	public void serverStarted(FMLServerStartedEvent event)
	{

	}

}
