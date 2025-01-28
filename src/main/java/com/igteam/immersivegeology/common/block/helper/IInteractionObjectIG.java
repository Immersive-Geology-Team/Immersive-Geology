/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.helper;

import com.google.common.base.Preconditions;
import com.igteam.immersivegeology.core.registration.IGMenuTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IInteractionObjectIG<T extends BlockEntity & IInteractionObjectIG<T>> extends MenuProvider
{
	@Nullable
	T getGuiMaster();

	IGMenuTypes.ArgContainer<? super T, ?> getContainerType();

	boolean canUseGui(Player var1);

	default boolean isValid() {
		return this.getGuiMaster() != null;
	}

	@Nonnull
	default AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity) {
		T master = this.getGuiMaster();
		Preconditions.checkNotNull(master);
		IGMenuTypes.ArgContainer<? super T, ?> type = this.getContainerType();
		return type.create(id, playerInventory, master);
	}

	default Component getDisplayName() {
		return Component.literal("");
	}
}