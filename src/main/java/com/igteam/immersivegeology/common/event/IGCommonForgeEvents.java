/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.event;


import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler.IMultiblock;
import com.igteam.immersivegeology.common.item.blueprint.BlueprintProjection;
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
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
				BlockPos structure_placed_at = settings.getPos();

				BlueprintProjection projection = new BlueprintProjection(world, multiblock);
				projection.setFlip(settings.isMirrored());
				projection.setRotation(settings.getRotation());

				// Find the current working layer by checking from bottom-up
				int workingLayer = 0;

				for (int y = 0; y < size.getY(); y++) {
					int finalY = y;
					boolean layerComplete = projection.process(y, p ->
					{
						BlockPos structure_block_world_position = structure_placed_at.offset(p.tPos);

						// If this block is at the current height level
						if (structure_block_world_position.getY() == (structure_placed_at.getY() + finalY)) {
							BlockState worldState = world.getBlockState(structure_block_world_position);
							return !worldState.getBlock().equals(p.tBlockInfo.state().getBlock());
						}

						return false;
					});

					if (layerComplete) {
						workingLayer = y;
						break;
					}
				}

				projection.process(workingLayer, p ->
				{
					ItemStack stack = ItemStack.EMPTY;
					BlockPos structure_block_world_position = structure_placed_at.offset(p.tPos);
					BlockPos hitPos = hit.getBlockPos();
					Vec3i offset = hit.getDirection().getNormal();
					hitPos = hitPos.offset(offset);

					// If the hit block is the same as the structure block
					if (structure_block_world_position.equals(hitPos) && world.getBlockState(structure_block_world_position).isAir()) {
						// Find the structure block directly above
						stack = new ItemStack(p.tBlockInfo.state().getBlock().asItem()).copy();
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
						return true;
					}
					return false;
				});
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
