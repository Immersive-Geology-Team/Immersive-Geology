/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.event;


import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler.IMultiblock;
import com.igteam.immersivegeology.common.item.blueprint.IGBlueprintSettings;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MiscEnum;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class IGCommonForgeEvents
{

	@SubscribeEvent
	public void handleBlueprintPickBlock(InputEvent.MouseButton event)
	{
		if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_MIDDLE && event.getAction() == GLFW.GLFW_PRESS) {
			Minecraft mc = Minecraft.getInstance();
			LocalPlayer player = mc.player;

			if (player != null && mc.hitResult instanceof BlockHitResult hit &&
					player.getOffhandItem().is(MiscEnum.Blueprint.getItem(ItemCategoryFlags.BLUEPRINT))) {

				ItemStack schematic = player.getOffhandItem();
				Level world = player.level();
				IGBlueprintSettings settings = new IGBlueprintSettings(schematic);

				if (settings.getMultiblock() == null || !settings.isPlaced() || settings.getPos() == null) return;

				IMultiblock multiblock = settings.getMultiblock();
				Vec3i size = multiblock.getSize(world);
				Vec3i offset = new Vec3i(-size.getX() / 2, 0, -size.getZ() / 2);
				BlockPos structure_placed_at = settings.getPos().offset(offset);

				List<StructureBlockInfo> structure_info = multiblock.getStructure(world);

				// Find the current working layer by checking from bottom-up
				int workingLayer = 0;
				boolean layerFound = false;

				for (int y = 0; y < size.getY(); y++) {
					boolean layerComplete = true;

					for (StructureBlockInfo info : structure_info) {
						BlockPos structure_block_world_position = structure_placed_at.offset(info.pos());

						// If this block is at the current height level
						if (structure_block_world_position.getY() == (structure_placed_at.getY() + y)) {
							BlockState worldState = world.getBlockState(structure_block_world_position);
							if (!worldState.getBlock().equals(info.state().getBlock())) {
								layerComplete = false;
								break; // Stop checking once we find the first mismatch
							}
						}
					}

					if (!layerComplete) {
						workingLayer = y;
						layerFound = true;
						break;
					}
				}

				if (!layerFound) return; // If somehow no working layer was found, exit early.
				// Find the block in the correct layer to pick
				ItemStack stack = ItemStack.EMPTY;
				for (StructureBlockInfo info : structure_info) {
					// Check if we're in the working layer
					if (info.pos().getY() == workingLayer) {
						BlockPos structure_block_world_position = structure_placed_at.offset(info.pos());
						// If the hit block is the same as the structure block
						if (structure_block_world_position.equals(hit.getBlockPos().above())) {
							// Find the structure block directly above
							for (StructureBlockInfo aboveInfo : structure_info) {
								if (structure_placed_at.offset(aboveInfo.pos()).equals(structure_block_world_position) && world.getBlockState(structure_block_world_position).isAir()) {
									stack = new ItemStack(aboveInfo.state().getBlock().asItem()).copy();
									break;
								}
							}
							break;
						}
					}
				}

				// Ensure missing parts of the structure aren't picked
				if (!stack.isEmpty()) {
					Inventory inventory = player.getInventory();
					int i = inventory.findSlotMatchingItem(stack);
					if (player.getAbilities().instabuild) {
						inventory.setPickedItem(stack);
						mc.gameMode.handleCreativeModeItemAdd(player.getItemInHand(InteractionHand.MAIN_HAND), 36 + inventory.selected);
					} else if (i != -1) {
						if (Inventory.isHotbarSlot(i)) {
							inventory.selected = i;
						} else {
							mc.gameMode.handlePickItem(i);
						}
					}
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void interruptLootTableLoading(LootTableLoadEvent event)
	{
		String namespace = event.getName().getNamespace();
		if(!IGLib.MODID.equals(namespace)) return;

		// Used to remove loot tables for inactive content
		String path = event.getName().getPath();
		for(ModFlags mods : ModFlags.values())
		{
			if(path.contains(mods.getName()) &! mods.isLoaded())
			{
				event.setCanceled(true);
			}
		}
	}
}
