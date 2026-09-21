package com.igteam.immersivegeology.client;

import com.igteam.immersivegeology.common.block.IGGenericBlock;
import com.igteam.immersivegeology.common.item.IGGenericBlockItem;
import com.igteam.immersivegeology.common.item.IGGenericItem;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGContent;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = IGLib.MODID, value = Side.CLIENT)
public class IGColorHandlers
{
	@SubscribeEvent
	public static void onBlockColors(ColorHandlerEvent.Block event)
	{
		List<Block> tinted = new ArrayList<>();
		for(Block block : IGContent.getRegisteredBlocks())
			if(block instanceof IGGenericBlock) tinted.add(block);

		if(tinted.isEmpty()) return;

		event.getBlockColors().registerBlockColorHandler(
				(state, level, pos, tintIndex) -> ((IGGenericBlock)state.getBlock()).getColor(tintIndex, state),
				tinted.toArray(new Block[0]));

		IGLib.IG_LOGGER.info("- Registered block color handlers for {} blocks", tinted.size());
	}

	@SubscribeEvent
	public static void onItemColors(ColorHandlerEvent.Item event)
	{
		List<Item> tintedItems = new ArrayList<>();
		List<Item> tintedBlockItems = new ArrayList<>();

		for(Item item : IGContent.getRegisteredItems())
		{
			if(item instanceof IGGenericItem) tintedItems.add(item);
			else if(item instanceof IGGenericBlockItem) tintedBlockItems.add(item);
		}

		if(!tintedItems.isEmpty())
			event.getItemColors().registerItemColorHandler(
					(stack, tintIndex) -> ((IGGenericItem)stack.getItem()).getColor(stack, tintIndex),
					tintedItems.toArray(new Item[0]));

		if(!tintedBlockItems.isEmpty())
			event.getItemColors().registerItemColorHandler(
					(stack, tintIndex) -> ((IGGenericBlockItem)stack.getItem()).getColor(stack, tintIndex),
					tintedBlockItems.toArray(new Item[0]));

		IGLib.IG_LOGGER.info("- Registered item color handlers for {} items", tintedItems.size()+tintedBlockItems.size());
	}
}
