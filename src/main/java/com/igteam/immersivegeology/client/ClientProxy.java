package com.igteam.immersivegeology.client;

import blusunrize.immersiveengineering.client.IEDefaultColourHandlers;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.items.IEItemInterfaces;
import com.igteam.immersivegeology.common.CommonProxy;
import com.igteam.immersivegeology.common.IGContent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import static com.igteam.immersivegeology.ImmersiveGeology.MODID;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MODID, bus = Bus.MOD)
public class ClientProxy extends CommonProxy
{

	public static Minecraft mc()
	{
		return Minecraft.getInstance();
	}

	@Override
	public void preInit()
	{
		super.preInit();
	}

	@Override
	public void preInitEnd()
	{
		super.preInitEnd();
	}

	@Override
	public void init()
	{
		super.init();

		for(Item item : IGContent.registeredIGItems)
			if(item instanceof IEItemInterfaces.IColouredItem&&((IEItemInterfaces.IColouredItem)item).hasCustomItemColours())
				mc().getItemColors().register(IEDefaultColourHandlers.INSTANCE, item);
		for(Block block : IGContent.registeredIGBlocks.values())
			if(block instanceof IEBlockInterfaces.IColouredBlock&&((IEBlockInterfaces.IColouredBlock)block).hasCustomBlockColours())
				mc().getBlockColors().register(IEDefaultColourHandlers.INSTANCE, block);
	}

	@Override
	public void initEnd()
	{
		super.initEnd();
	}

	@Override
	public void postInit()
	{
		super.postInit();
	}

	@Override
	public void postInitEnd()
	{
		super.postInitEnd();
	}

	@Override
	public void serverStarting()
	{
		super.serverStarting();
	}
}
