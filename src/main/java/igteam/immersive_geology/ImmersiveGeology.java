// Credit where credit due
// World Generation - Muddykat (Using Modified Source Code from TerraFirmaCraft and YungsBetterCaves)
// GNU GENERAL PUBLIC LICENSE Version 3, 29 June 2007 for YungsBetterCaves sourced assets
// European Union Public Licence V. 1.2 for for TerraFirmaCraft Sourced Assets - this includes most of the biome distribution code
//

package igteam.immersive_geology;

import igteam.api.IGApi;
import igteam.api.processing.Serializers;
import igteam.immersive_geology.common.crafting.recipes.RecipeReloadListener;
import igteam.immersive_geology.common.loot.LootIG;
import igteam.immersive_geology.common.world.IGInteractionHandler;
import igteam.immersive_geology.core.config.IGConfigurationHandler;
import igteam.immersive_geology.core.lib.IGLib;
import igteam.immersive_geology.core.proxy.ClientProxy;
import igteam.immersive_geology.core.proxy.Proxy;
import igteam.immersive_geology.core.proxy.ServerProxy;
import igteam.immersive_geology.core.registration.IGMultiblockRegistrationHolder;
import igteam.immersive_geology.core.registration.IGRegistrationHolder;
import igteam.immersive_geology.core.registration.IGTileTypes;
import igteam.api.tags.IGTags;
import net.minecraft.client.Minecraft;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DatagenModLoader;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;

@Mod(IGLib.MODID)
public class ImmersiveGeology
{
	private static final Logger LOGGER = getNewLogger();
	public static Proxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
	IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
	IEventBus forgeBus = MinecraftForge.EVENT_BUS;

	public ImmersiveGeology()
	{
		IGRegistrationHolder.initialize();

		IGTags.initialize();

		LootIG.initialize();


		//setup configs
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, IGConfigurationHandler.Common.ALL);

		//setup the listeners
		modBus.addListener(this::setup);
		modBus.addListener(this::onFinishSetup);
		modBus.addListener(this::onClientSetup);
		modBus.addListener(this::enqueueIMC);
		modBus.addListener(this::processIMC);

		//Register Classes for Mod and forge Bus
		modBus.register(IGRegistrationHolder.class);
		forgeBus.register(IGInteractionHandler.class);
		forgeBus.register(this);

		IGMultiblockRegistrationHolder.populate();
		IGMultiblockRegistrationHolder.initialize();

		IGTileTypes.REGISTER.register(modBus);

		forgeBus.addListener(this::addReloadListeners);

		Serializers.RECIPE_SERIALIZERS.register(modBus); //Recipe Structure Builder is Now located in IGDataProvider - As it's only used during Data Gen anyway.
		proxy.registerContainersAndScreens();


		if(!DatagenModLoader.isRunningDataGen()) {
			//Prevents Issues with datagen runs
		}
		IGApi.init();
	}

	public void addReloadListeners(AddReloadListenerEvent event){
		event.addListener(new RecipeReloadListener(event.getDataPackRegistries()));
	}

	private void setup(final FMLCommonSetupEvent event){
		LOGGER.info(String.format("Initializing setup for Immersive Geology V%s%s",IGLib.VERSION, IGLib.MINECRAFT_VERSION));

		proxy.onSetup(event);
	}

	private void onFinishSetup(final FMLLoadCompleteEvent event){
		LOGGER.info("Finishing Immersive Geology Setup");
		proxy.onFinishSetup(event);
	}

	private void onClientSetup(final FMLClientSetupEvent event){
		LOGGER.info("Setting up Client Stuff");
		proxy.onClientSetup(event);
	}

	@SubscribeEvent
	public static void onServerStarting(FMLServerStartingEvent event){
		LOGGER.info("Setting up Server Stuff");
		proxy.onServerStarting(event);
	}

	@SubscribeEvent
	public static void onServerStopped(FMLServerStoppedEvent event){
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
