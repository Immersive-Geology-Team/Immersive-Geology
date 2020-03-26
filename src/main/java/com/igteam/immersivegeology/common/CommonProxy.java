package com.igteam.immersivegeology.common;

import com.igteam.immersivegeology.common.event.MaterialRegistryEvent;
import com.igteam.immersivegeology.common.util.IGLogger;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy
{
	public void preInit()
	{
		IGLogger.info("mats");
		MinecraftForge.EVENT_BUS.post(new MaterialRegistryEvent());
	}
	
	public void preInitEnd()
	{
		
	}

	public void init()
	{
		
	}

	public void initEnd()
	{
		
	}

	public void postInit()
	{
		
	}

	public void postInitEnd()
	{
		
	}

	public void serverStarting()
	{
		
	}

	public void onWorldLoad()
	{
		
	}
}
