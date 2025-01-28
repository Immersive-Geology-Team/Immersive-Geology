/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.menu;

import blusunrize.immersiveengineering.api.crafting.BlueprintCraftingRecipe;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler.IMultiblock;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.api.tool.IConfigurableTool;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IEMultiblocks;
import blusunrize.immersiveengineering.common.gui.BlueprintInventory;
import blusunrize.immersiveengineering.common.gui.IEBaseContainerOld;
import blusunrize.immersiveengineering.common.gui.IESlot;
import blusunrize.immersiveengineering.common.items.EngineersBlueprintItem;
import blusunrize.immersiveengineering.mixin.accessors.ContainerAccess;
import com.igteam.immersivegeology.common.block.entity.DrawingTableBlockEntity;
import com.mojang.datafixers.kinds.IdF.Mu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

public class SchematicsContainerMenu extends IEBaseContainerOld<DrawingTableBlockEntity>
{
	public static final int MAX_NUM_DYNAMIC_SLOTS = 64;
	public final Inventory inventoryPlayer;
	private final Level world;

	public SchematicsContainerMenu(MenuType<?> type, int id, Inventory inventoryPlayer, DrawingTableBlockEntity tile)
	{
		super(type, tile, id);
		this.inventoryPlayer = inventoryPlayer;
		this.world = tile.getLevelNonnull();
		rebindSlots();
	}

	private void bindPlayerInv(Inventory inventoryPlayer) {
		int i;
		for(i = 0; i < 3; ++i) {
			for(int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 87 + i * 18));
			}
		}

		for(i = 0; i < 9; ++i) {
			this.addSlot(new Slot(inventoryPlayer, i, 8 + i * 18, 145));
		}
	}

	public void rebindSlots()
	{
		this.slots.clear();
		((ContainerAccess)this).getLastSlots().clear();
		((ContainerAccess)this).getRemoteSlots().clear();
		assert this.inv!=null;
		this.addSlot(new Slot(this.inv, 0, 24, 22));
		ownSlotCount = 1;

		int amount_of_schematics = MultiblockHandler.getMultiblocks().size();
		List<IMultiblock> multiblocks = MultiblockHandler.getMultiblocks();
		for(int i = 0; i < amount_of_schematics; i++)
		{
			IMultiblock mb = multiblocks.get(i);
			if(mb instanceof TemplateMultiblock template)
			{
				int y = 21+(i < 9?i/3: (-(i-6)/3))*18;
				this.addSlot(new SchematicSlot(this.inv, i + 1, 118+(i%3*18), y, template));
				ownSlotCount++;
			}
		}
		// Add "useless" slots to keep the number of slots (and therefore the IDs of the player inventory slots)
		// constant. MC doesn't handle changing slot IDs well, causing desyncs
		for(; ownSlotCount < MAX_NUM_DYNAMIC_SLOTS; ++ownSlotCount)
			addSlot(new IESlot.AlwaysEmptySlot(this));
		bindPlayerInv(inventoryPlayer);

		if(FMLLoader.getDist().isClient())
		{
			Screen currentScreen = Minecraft.getInstance().screen;
			if(currentScreen instanceof IEContainerScreen<?>)
				currentScreen.init(Minecraft.getInstance(), currentScreen.width, currentScreen.height);
		}
	}
	@Override
	public void clicked(int id, int dragType, ClickType clickType, Player player)
	{
		super.clicked(id, dragType, clickType, player);
		tile.markContainingBlockForUpdate(null);
		if(!world.isClientSide)
			broadcastChanges();
	}
	public static class SchematicSlot extends Slot {

		public final TemplateMultiblock template;
		public SchematicSlot(Container pContainer, int pSlot, int pX, int pY, TemplateMultiblock template)
		{
			super(pContainer, pSlot, pX, pY);
			this.template = template;
		}

		@Override
		public boolean mayPlace(ItemStack stack)
		{
			return false;
		}

		@Override
		public boolean mayPickup(Player player)
		{
			return true;
		}
	}
}
