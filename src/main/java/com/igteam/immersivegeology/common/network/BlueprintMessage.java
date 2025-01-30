/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.network;

import com.igteam.immersivegeology.common.item.blueprint.IGBlueprintSettings;
import com.igteam.immersivegeology.core.material.data.enums.MiscEnum;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;
public class BlueprintMessage implements INetMessage {
	private final BlockPos blockPos;

	// Constructor that takes the block position
	public BlueprintMessage(BlockPos blockPos) {
		this.blockPos = blockPos;
	}

	// For decoding the packet from the network (from bytes)
	public BlueprintMessage(FriendlyByteBuf buf) {
		this.blockPos = buf.readBlockPos();
	}

	// For encoding the packet to be sent over the network
	@Override
	public void toBytes(FriendlyByteBuf buf) {
		buf.writeBlockPos(blockPos);
	}

	// For handling the packet on the server side
	@Override
	public void process(Supplier<Context> context) {
		Context ctx = context.get();
		ctx.enqueueWork(() -> {
			// Handle the logic on the server side
			ServerPlayer player = ctx.getSender();
			if (player != null) {
				handleServerSide(player);
			}
		});
		ctx.setPacketHandled(true);
	}

	// This method is called on the server side to handle the received packet
	private void handleServerSide(ServerPlayer player) {
		// Handle the block position (e.g., update the inventory or select the block)
		Level world = player.level();
		BlockState state = world.getBlockState(blockPos);
		ItemStack stack = new ItemStack(state.getBlock().asItem());

		// If the item is valid, update the inventory
		if (!stack.isEmpty()) {
			Inventory inventory = player.getInventory();
			int existingSlot = inventory.findSlotMatchingItem(stack);

			if (existingSlot != -1) {
				// Select the existing item instead of adding a duplicate
				inventory.selected = existingSlot;
			} else if (player.getAbilities().instabuild) {
				// Creative Mode: Add to hotbar if not already present
				for (int i = 0; i < 9; i++) {
					if (inventory.getItem(i).isEmpty()) {
						inventory.setItem(i, stack);
						inventory.selected = i;
						break;
					}
				}
				// Force server synchronization
				player.containerMenu.broadcastChanges();
			}
		}
	}
}
