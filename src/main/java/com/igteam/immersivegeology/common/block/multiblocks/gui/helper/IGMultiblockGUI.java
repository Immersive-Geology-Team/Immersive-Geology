/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.gui.helper;

import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IMultiblockComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.register.IEMenuTypes;
import com.igteam.immersivegeology.core.registration.IGMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public record IGMultiblockGUI<S extends IMultiblockState>(
		IGMenuTypes.MultiblockContainer<S, ?> menu) implements IMultiblockComponent<S>
{
	public IGMultiblockGUI(IGMenuTypes.MultiblockContainer<S, ?> menu) {
		this.menu = menu;
	}

	public InteractionResult click(IMultiblockContext<S> ctx, BlockPos posInMultiblock, Player player, InteractionHand hand, BlockHitResult absoluteHit, boolean isClient) {
		if (!isClient) {
			player.openMenu(this.menu.provide(ctx, posInMultiblock));
		}

		return InteractionResult.SUCCESS;
	}

	public IGMenuTypes.MultiblockContainer<S, ?> menu() {
		return this.menu;
	}
}
