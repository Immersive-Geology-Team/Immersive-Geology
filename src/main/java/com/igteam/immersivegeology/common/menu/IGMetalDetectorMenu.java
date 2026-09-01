/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.menu;

import com.igteam.immersivegeology.common.block.entity.device.IGMetalDetectorEntity;
import com.igteam.immersivegeology.core.registration.IGMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The detector has no inventory of its own; the player's is here because the GUI art draws the panel for it.
 * <p>
 * Two things have to reach the client and they travel separately. The charge goes through {@link ContainerData},
 * which resyncs every tick but only to the player with the screen open. The sweep goes on the block entity's
 * description packet instead - it is 289 bytes and only changes once every few seconds, so pushing it through a
 * data slot array would be silly.
 */
public class IGMetalDetectorMenu extends AbstractContainerMenu
{
	/** Slot geometry read off the GUI texture: recesses start at x=7, first inventory row at y=84. */
	private static final int SLOT_X = 8;
	private static final int INVENTORY_Y = 85;
	private static final int HOTBAR_Y = 143;

	/** Both recesses in the panel left of the console, measured off the GUI texture. */
	public static final int MAP_SLOT_X = 10;
	public static final int MAP_IN_Y = 22;
	public static final int MAP_OUT_Y = 55;

	/** Map goes in the top slot and comes back out of the bottom one; the player's inventory follows both. */
	public static final int MAP_IN_SLOT = 0;
	public static final int MAP_OUT_SLOT = 1;
	private static final int FIRST_PLAYER_SLOT = 2;
	private static final int END_PLAYER_SLOT = FIRST_PLAYER_SLOT+27;
	private static final int END_HOTBAR_SLOT = END_PLAYER_SLOT+9;

	public static final int DATA_ENERGY = 0;
	public static final int DATA_CAPACITY = 1;
	public static final int DATA_PROGRESS = 2;
	public static final int DATA_IDLE = 3;
	public static final int DATA_SWEEPING = 4;
	/** Ticks until a settled survey's next brief check, so the tooltip can say it in seconds. */
	public static final int DATA_COUNTDOWN = 5;
	public static final int DATA_SIZE = 6;

	private final ContainerData data;
	@Nullable
	private final IGMetalDetectorEntity detector;
	private final BlockPos pos;

	/** Server side: reads straight off the block entity. */
	public IGMetalDetectorMenu(int id, Inventory inventory, IGMetalDetectorEntity detector)
	{
		this(id, inventory, detector, detector.getBlockPos(), new ContainerData()
		{
			@Override
			public int get(int index)
			{
				return switch(index)
						{
							case DATA_ENERGY -> detector.energyStorage.getEnergyStored();
							case DATA_CAPACITY -> detector.energyStorage.getMaxEnergyStored();
							case DATA_PROGRESS -> detector.getProgress();
							case DATA_IDLE -> detector.isIdle()?1: 0;
							case DATA_SWEEPING -> detector.isSweeping()?1: 0;
							case DATA_COUNTDOWN -> detector.getTicksToNextCheck();
							default -> 0;
						};
			}

			@Override
			public void set(int index, int value)
			{
			}

			@Override
			public int getCount()
			{
				return DATA_SIZE;
			}
		});
	}

	/** Client side: the block entity is whatever is already at the position the server sent. */
	public IGMetalDetectorMenu(int id, Inventory inventory, FriendlyByteBuf extraData)
	{
		this(id, inventory, extraData.readBlockPos());
	}

	private IGMetalDetectorMenu(int id, Inventory inventory, BlockPos pos)
	{
		this(id, inventory, findDetector(inventory.player.level(), pos), pos, new SimpleContainerData(DATA_SIZE));
	}

	private IGMetalDetectorMenu(int id, Inventory inventory, @Nullable IGMetalDetectorEntity detector,
								BlockPos pos, ContainerData data)
	{
		super(IGMenuTypes.METAL_DETECTOR.get(), id);
		this.detector = detector;
		this.pos = detector!=null?detector.getBlockPos(): pos;
		this.data = data;
		addDataSlots(data);

		if(detector!=null)
		{
			addSlot(new SlotItemHandler(detector.getMapSlots(), IGMetalDetectorEntity.SLOT_MAP_IN,
					MAP_SLOT_X, MAP_IN_Y));
			addSlot(new SlotItemHandler(detector.getMapSlots(), IGMetalDetectorEntity.SLOT_MAP_OUT,
					MAP_SLOT_X, MAP_OUT_Y)
			{
				@Override
				public boolean mayPlace(@NotNull ItemStack stack)
				{
					// Output only. A marked map leaves here, nothing goes back in.
					return false;
				}
			});
		}
		else
		{
			// The client can be built before the block entity is reachable; placeholders keep the slot indices
			// identical on both sides, which is what quickMoveStack and the sync depend on.
			SimpleContainer placeholder = new SimpleContainer(2);
			addSlot(new Slot(placeholder, IGMetalDetectorEntity.SLOT_MAP_IN, MAP_SLOT_X, MAP_IN_Y));
			addSlot(new Slot(placeholder, IGMetalDetectorEntity.SLOT_MAP_OUT, MAP_SLOT_X, MAP_OUT_Y));
		}

		for(int row = 0; row < 3; row++)
		{
			for(int col = 0; col < 9; col++)
			{
				addSlot(new Slot(inventory, col+row*9+9, SLOT_X+col*18, INVENTORY_Y+row*18));
			}
		}
		for(int col = 0; col < 9; col++)
		{
			addSlot(new Slot(inventory, col, SLOT_X+col*18, HOTBAR_Y));
		}
	}

	@Nullable
	private static IGMetalDetectorEntity findDetector(Level level, BlockPos pos)
	{
		return level.getBlockEntity(pos) instanceof IGMetalDetectorEntity detector?detector: null;
	}

	@Nullable
	public IGMetalDetectorEntity getDetector()
	{
		return detector;
	}

	public int getEnergyStored()
	{
		return data.get(DATA_ENERGY);
	}

	public int getEnergyCapacity()
	{
		return Math.max(1, data.get(DATA_CAPACITY));
	}

	/** Confirmation progress through the survey, 0-100. */
	public int getProgress()
	{
		return Math.max(0, Math.min(100, data.get(DATA_PROGRESS)));
	}

	public boolean isIdle()
	{
		return data.get(DATA_IDLE)!=0;
	}

	/** True while chunks are actually being read; false between a settled survey's brief checks. */
	public boolean isSweeping()
	{
		return data.get(DATA_SWEEPING)!=0;
	}

	/** Seconds until a settled survey's next brief check, rounded up so it never reads zero while still waiting. */
	public int getSecondsToNextCheck()
	{
		return (Math.max(0, data.get(DATA_COUNTDOWN))+19)/20;
	}

	@Override
	public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index)
	{
		Slot slot = slots.get(index);
		if(!slot.hasItem()) return ItemStack.EMPTY;

		ItemStack stack = slot.getItem();
		ItemStack original = stack.copy();

		if(index==MAP_IN_SLOT||index==MAP_OUT_SLOT)
		{
			if(!moveItemStackTo(stack, FIRST_PLAYER_SLOT, END_HOTBAR_SLOT, true)) return ItemStack.EMPTY;
		}
		else if(stack.getItem() instanceof MapItem&&moveItemStackTo(stack, MAP_IN_SLOT, MAP_IN_SLOT+1, false))
		{
			// A map goes into the detector first; anything else just moves between the player's own rows.
		}
		else
		{
			boolean fromMainInventory = index < END_PLAYER_SLOT;
			int from = fromMainInventory?END_PLAYER_SLOT: FIRST_PLAYER_SLOT;
			int to = fromMainInventory?END_HOTBAR_SLOT: END_PLAYER_SLOT;
			if(!moveItemStackTo(stack, from, to, false)) return ItemStack.EMPTY;
		}

		if(stack.isEmpty()) slot.set(ItemStack.EMPTY);
		else slot.setChanged();
		return original;
	}

	@Override
	public boolean stillValid(@NotNull Player player)
	{
		if(detector==null) return false;
		return player.level().getBlockEntity(pos)==detector&&player.distanceToSqr(
				pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5) <= 64.0;
	}
}
