package com.igteam.immersivegeology.client;

import com.igteam.immersivegeology.common.CommonProxy;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import static com.igteam.immersivegeology.ImmersiveGeology.MODID;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MODID, bus = Bus.MOD)
public class ClientProxy extends CommonProxy
{

	@Override
	public void preInit()
	{
		
	}
	
	@Override
	public void preInitEnd() 
	{

	}
	
	@Override
	public void init() 
	{

	}
	
	@Override
	public void initEnd() 
	{

	}
	
	@Override
	public void postInit() 
	{

	}
	
	@Override
	public void postInitEnd() 
	{

	}

	@Override
	public void serverStarting()
	{
	}
}
