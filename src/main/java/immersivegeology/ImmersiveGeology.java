package immersivegeology;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ImmersiveGeology.MODID)
public class ImmersiveGeology 
{
	public static final String MODID = "immersivegeology";
	public static final String MODNAME = "Immersive Geology";
	public static final String VERSION = "${version}";
	public static final String NETWORK_VERSION = "1";
	
	public ImmersiveGeology()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
		

		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarted);

	}
	
	public void setup(FMLCommonSetupEvent event)
	{
		//Previously in PREINIT
		
		
		
		//Previously in INIT
		
		
		
		//Previously in POSTINIT
		
		
		
	}

	public void loadComplete(FMLLoadCompleteEvent event)
	{
		
	}

	public void serverStarting(FMLServerStartingEvent event)
	{
		
	}

	public void serverStarted(FMLServerStartedEvent event)
	{
		
	}

}
