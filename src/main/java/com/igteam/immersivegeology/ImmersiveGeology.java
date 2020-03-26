package com.igteam.immersivegeology;

import com.igteam.immersivegeology.client.ClientProxy;
import com.igteam.immersivegeology.common.CommonProxy;
import com.igteam.immersivegeology.common.IGContent;
import com.igteam.immersivegeology.common.util.IGLogger;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nonnull;

@Mod(ImmersiveGeology.MODID)
public class ImmersiveGeology 
{
	public static final String MODID = "immersivegeology";
	public static final String MODNAME = "Immersive Geology";
	public static final String VERSION = "${version}";
	public static final String NETWORK_VERSION = "1";
	public static CommonProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(),
			() -> () -> new CommonProxy());

	public static final SimpleChannel packetHandler = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(MODID, "main"))
			.networkProtocolVersion(() -> NETWORK_VERSION)
			.serverAcceptedVersions(NETWORK_VERSION::equals)
			.clientAcceptedVersions(NETWORK_VERSION::equals)
			.simpleChannel();
	
	public ImmersiveGeology()
	{
		IGLogger.logger = LogManager.getLogger(MODID);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);

		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarted);
		IGContent.modConstruction();
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

	public static ItemGroup itemGroup = new ItemGroup(MODID)
	{
		@Override
		@Nonnull
		public ItemStack createIcon()
		{
			return new ItemStack(Blocks.IRON_ORE); //TODO add proper tab icon
		}
	};

}
