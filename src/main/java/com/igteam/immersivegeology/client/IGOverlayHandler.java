/*
 * ${USER}
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelper;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.client.BlockOverlayUtils;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.common.items.VoltmeterItem;
import blusunrize.immersiveengineering.common.register.IEItems;
import blusunrize.immersiveengineering.common.register.IEItems.Tools;
import blusunrize.immersiveengineering.common.util.Utils;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.IGPositionalOverlayText;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class IGOverlayHandler
{
	@SubscribeEvent
	public void onRenderOverlay(RenderGuiOverlayEvent.Post event)
	{
		int scaledWidth = ClientUtils.mc().getWindow().getGuiScaledWidth();
		int scaledHeight = ClientUtils.mc().getWindow().getGuiScaledHeight();
		if(ClientUtils.mc().player!=null&&event.getOverlay().id().equals(VanillaGuiOverlay.ITEM_NAME.id()))
		{
			Player player = ClientUtils.mc().player;
			GuiGraphics graphics = event.getGuiGraphics();
			PoseStack transform = graphics.pose();

			for(InteractionHand hand : InteractionHand.values())
			{
				if(ClientUtils.mc().hitResult!=null)
				{

					ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
					boolean hammer = !held.isEmpty()&&Utils.isHammer(held);
					HitResult mop = ClientUtils.mc().hitResult;
					if(mop instanceof BlockHitResult &! (held.getItem() instanceof VoltmeterItem))
					{
						BlockPos pos = ((BlockHitResult)mop).getBlockPos();
						Direction face = ((BlockHitResult)mop).getDirection();
						BlockEntity tileEntity = player.level().getBlockEntity(pos);
						if((tileEntity instanceof IMultiblockBE<?> multiblock))
						{
							renderMultiblockOverlay(multiblock, hammer, transform, scaledWidth, scaledHeight);
						}
					}
				}

			}
		}
	}

	private <S extends IMultiblockState> void renderMultiblockOverlay(
			IMultiblockBE<S> be, boolean hammer, PoseStack transform, int scaledWidth, int scaledHeight
	)
	{
		final IMultiblockBEHelper<S> helper = be.getHelper();
		if(!(helper.getMultiblock().logic() instanceof IGPositionalOverlayText<S> overlayHandler))
			return;
		final List<Component> overlayText = overlayHandler.getOverlayText(helper.getState(), ClientUtils.mc().player, be.getHelper());
		if(overlayText==null)
			return;
		BlockOverlayUtils.drawBlockOverlayText(transform, overlayText, scaledWidth, scaledHeight);
	}
}
