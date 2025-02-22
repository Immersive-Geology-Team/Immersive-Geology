package com.igteam.immersivegeology.common.menu;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.common.gui.IEBaseContainerOld;
import blusunrize.immersiveengineering.common.gui.IESlot;
import blusunrize.immersiveengineering.mixin.accessors.ContainerAccess;
import com.igteam.immersivegeology.common.block.entity.DrawingTableBlockEntity;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SchematicsContainerMenu extends IEBaseContainerOld<DrawingTableBlockEntity>
{
	public static final int MAX_NUM_DYNAMIC_SLOTS = 8;
	public final Inventory inventoryPlayer;
	public SchematicInventory inventorySchematic;
	private final Level world;

	public int selected_schematic = 0;
	public final List<TemplateMultiblock> availableMultiblocks = MultiblockHandler.getMultiblocks().stream().filter(mb -> mb instanceof TemplateMultiblock).map(mb -> (TemplateMultiblock) mb).toList();
	public Boolean isMirroredSchematic;

	public SchematicsContainerMenu(MenuType<?> type, int id, Inventory inventoryPlayer, DrawingTableBlockEntity tile)
	{
		super(type, tile, id);
		this.inventoryPlayer = inventoryPlayer;
		this.world = tile.getLevelNonnull();
		selected_schematic = 0;
		this.isMirroredSchematic = false;
		this.inventorySchematic = new SchematicInventory(this, List.of(availableMultiblocks.get(selected_schematic)));
		rebindSlots();
	}

	private void bindPlayerInv(Inventory inventoryPlayer) {
		int i;
		for(i = 0; i < 3; ++i) {
			for(int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(inventoryPlayer, j + i * 9 + 9, 35 + j * 18, 137 + i * 18));
			}
		}

		for(i = 0; i < 9; ++i) {
			this.addSlot(new Slot(inventoryPlayer, i, 35 + i * 18, 195));
		}
	}



	public void rebindSlots()
	{
		this.slots.clear();
		((ContainerAccess)this).getLastSlots().clear();
		((ContainerAccess)this).getRemoteSlots().clear();
		assert this.inv!=null;
		this.addSlot(new SchematicInputSlot(this, this.inv, 0, 144, 89, 64));
		ownSlotCount = 1;

		int amount_of_schematics = List.of(availableMultiblocks.get(selected_schematic)).size();

		this.inventorySchematic = new SchematicInventory(this, List.of(availableMultiblocks.get(selected_schematic)));
		if(this.inv.getItem(0).getCount() > 0)
		{
			for(int i = 0; i < amount_of_schematics; i++)
			{
				TemplateMultiblock template = availableMultiblocks.get(i);
				int y = 89+(i < 9?i/3: (-(i-6)/3))*18;
				this.addSlot(new SchematicSlot(this, inventorySchematic, this.inv, i, 190+(i%3*18), y, template));
				ownSlotCount++;
				inventorySchematic.updateOutputs(inv);
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
			if(currentScreen instanceof IEContainerScreen<?>) currentScreen.init(Minecraft.getInstance(), currentScreen.width, currentScreen.height);
		}
	}

	public void nextSchematic()
	{
		this.selected_schematic = (selected_schematic + 1) % availableMultiblocks.size();
		this.inventorySchematic = new SchematicInventory(this, List.of(availableMultiblocks.get(selected_schematic)));
	}

	public void previousSchematic()
	{
		int toSelect = selected_schematic - 1;
		if(toSelect < 0) toSelect = availableMultiblocks.size() -1;
		this.selected_schematic = toSelect;
		this.inventorySchematic = new SchematicInventory(this, List.of(availableMultiblocks.get(selected_schematic)));
	}


	public void flipSchematic()
	{
		isMirroredSchematic = !isMirroredSchematic;
	}

	@Override
	public void clicked(int id, int dragType, ClickType clickType, Player player)
	{
		super.clicked(id, dragType, clickType, player);
		tile.markContainingBlockForUpdate(null);
		if(!world.isClientSide)
			broadcastChanges();
	}

	@Override
	public void receiveMessageFromScreen(CompoundTag nbt)
	{
		if(nbt.contains("index"))
		{
			int index = nbt.getInt("index");
			jumpToSchematic(index);
			this.rebindSlots();
		}

		if(nbt.contains("mirrored"))
		{
			this.isMirroredSchematic = nbt.getBoolean("mirrored");
			this.rebindSlots();
		}
	}

	public Boolean getSchematicMirrorState()
	{
		return this.isMirroredSchematic;
	}

	public void jumpToSchematic(Integer index)
	{
		int toSelect = index;
		if(toSelect < 0) toSelect = availableMultiblocks.size() -1;
		this.selected_schematic = toSelect % availableMultiblocks.size();
		this.inventorySchematic = new SchematicInventory(this, List.of(availableMultiblocks.get(selected_schematic)));
	}

	public static class SchematicInputSlot extends IESlot
	{
		int size;
		SchematicsContainerMenu schematicMenu;
		public SchematicInputSlot(SchematicsContainerMenu containerMenu, Container inv, int id, int x, int y, int size)
		{
			super(containerMenu, inv, id, x, y);
			this.size = size;
			this.schematicMenu = containerMenu;
		}
		@Override
		public boolean mayPlace(ItemStack itemStack)
		{
			if(itemStack.isEmpty()) return false;
			if(itemStack.getItem().equals(Items.PAPER)) return true;
			return false;
		}

		@Override
		public int getMaxStackSize(ItemStack stack)
		{
			return size;
		}

		@Override
		public void setChanged()
		{
			super.setChanged();
			schematicMenu.rebindSlots();
		}

		@Override
		public boolean mayPickup(Player pPlayer)
		{
			return true;
		}

	}

	public static class SchematicSlot extends IESlot {
		private final Container inputInventory;
		public final TemplateMultiblock template;
		public SchematicSlot(AbstractContainerMenu container, SchematicInventory inv, Container inputInventory, int id, int x, int y, TemplateMultiblock template)
		{
			super(container, inv, id, x, y);
			this.inputInventory = inputInventory;
			this.template = template;
		}

		@Override
		public boolean mayPlace(@NotNull ItemStack stack)
		{
			return false;
		}

		@Override
		public boolean mayPickup(@NotNull Player player)
		{
			return true;
		}

		@Override
		public boolean isActive() {
			return this.hasItem();
		}

		@Override
		public void onTake(Player player, ItemStack stack) {
			((SchematicInventory)this.container).reduceInputs(this.inputInventory, stack);
			super.onTake(player, stack);
		}
	}
}