package com.igteam.immersivegeology.client;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.common.IGContent;
import com.igteam.immersivegeology.common.blocks.IGOreBearingBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.RenderList;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.BlockItem;
import net.minecraft.scoreboard.ScoreCriteria.RenderType;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

/**
 * @author Sciwhiz12 - Thanks for the help man!
 *
 */

@EventBusSubscriber(value = Dist.CLIENT, modid = ImmersiveGeology.MODID, bus = Bus.MOD)
public class BlockColorRegistration {
	public static final IBlockColor BLOCK_COLOR = (state, world, pos, tintIndex) -> {
		// returns int value with 0xRRGGBB
		if (tintIndex == 1) {
			if (state.getBlock() instanceof IGOreBearingBlock) {
				IGOreBearingBlock oreBlock = (IGOreBearingBlock) state.getBlock();
				return oreBlock.getOverlayColor(state);
			}
		} else if (tintIndex == 0) {
			if (state.getBlock() instanceof IGOreBearingBlock) {
				IGOreBearingBlock oreBlock = (IGOreBearingBlock) state.getBlock();
				return oreBlock.getColor(state, null, pos, tintIndex);// I think this is how it works
			} else {
				return 0xFFFFFF;
			}
		}
		return 0xFFFFFF;
	};
	public static final IItemColor ITEM_COLOR = (stack, tintIndex) -> BLOCK_COLOR
			.getColor(((BlockItem) stack.getItem()).getBlock().getDefaultState(), null, null, tintIndex);

	@SubscribeEvent
	static void onItemColors(final ColorHandlerEvent.Item event) {
		BlockColors blockColors = event.getBlockColors();
		ItemColors itemColors = event.getItemColors();

		// Block and item colors both need to be registerd
		// Block colors give different color based on the given block
		// Item colors simply pass-through to the block colors

		for (Block block : IGContent.registeredIGBlocks.values()) {
			if (block instanceof IGOreBearingBlock) {
				blockColors.register(BLOCK_COLOR, block);
				itemColors.register(ITEM_COLOR, ((IGOreBearingBlock) block).getItemBlock());
			}
		}
	}
}
