/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.entity;

import blusunrize.immersiveengineering.api.IEApi;
import blusunrize.immersiveengineering.api.utils.CapabilityUtils;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.wooden.WoodenCrateBlockEntity;
import blusunrize.immersiveengineering.common.gui.CrateEntityContainer;
import blusunrize.immersiveengineering.common.gui.CrateMenu;
import blusunrize.immersiveengineering.common.register.IEBlocks.WoodenDevices;
import blusunrize.immersiveengineering.common.register.IEMenuTypes;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.IEInventoryHandler;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.common.menu.IGCrateMenu;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.registration.IGMenuTypes;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class IGCrateEntity extends RandomizableContainerBlockEntity implements IIEInventory, IEBlockInterfaces.IBlockEntityDrop, IEBlockInterfaces.IComparatorOverride
{
	public static final int CONTAINER_SIZE = 27;
	private NonNullList<ItemStack> inventory;
	private ListTag enchantments;
	private final LazyOptional<IItemHandler> inventoryCap;

	public IGCrateEntity(BlockPos pos, BlockState state) {
		super(resolveEntityType(state), pos, state);
		this.inventory = NonNullList.withSize(27, ItemStack.EMPTY);
		this.inventoryCap = CapabilityUtils.constantOptional(new IEInventoryHandler(27, this));
	}

	private static BlockEntityType<?> resolveEntityType(BlockState state) {
		Block block = state.getBlock();

		if (block instanceof IGBlockType crateBlock) {
			MaterialInterface<?> material = crateBlock.getMaterial(MaterialTexture.base);
			String registryKey = BlockCategoryFlags.CRATE.getRegistryKey(material);
			return IGRegistrationHolder.getTE.apply(registryKey);
		}

		// Fallback to Steel if block is not of the expected type
		String fallbackKey = BlockCategoryFlags.CRATE.getRegistryKey(MetalEnum.Steel);
		return IGRegistrationHolder.getTE.apply(fallbackKey);
	}

	@Override
	protected Component getDefaultName()
	{
		Block b = this.getBlockState().getBlock();
		if(b instanceof IGCrateEntityType block)
		{
			return Component.translatable("block.immersivegeology.crate", block.getMaterial(MaterialTexture.base).getTranslationName());
		}
		return Component.literal("Crate");
	}

	@Override
	protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory)
	{
		return new IGCrateMenu(IGMenuTypes.CRATE.get(), pContainerId, pInventory, this);
	}


	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.loadIEData(nbt);
	}

	private void loadIEData(CompoundTag nbt) {
		if (nbt.contains("enchantments", 9)) {
			this.enchantments = nbt.getList("enchantments", 10);
		}

		if (nbt.contains("lootTable", 8) && !nbt.contains("LootTable")) {
			nbt.putString("LootTable", nbt.getString("lootTable"));
		}

		if (!this.tryLoadLootTable(nbt)) {
			loadBigStacks(nbt.getList("Items", Tag.TAG_COMPOUND), this.inventory);
		}

	}

	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		if (this.enchantments != null && this.enchantments.size() > 0) {
			nbt.put("enchantments", this.enchantments);
		}

		if (!this.trySaveLootTable(nbt)) {
			nbt.put("Items", saveBigStacks(this.inventory));
		}

	}

	public static ListTag saveBigStacks(NonNullList<ItemStack> items) {
		ListTag tagList = new ListTag();
		for (int i = 0; i < items.size(); ++i) {
			ItemStack stack = items.get(i);
			if (!stack.isEmpty()) {
				CompoundTag itemTag = new CompoundTag();
				itemTag.putByte("Slot", (byte) i);
				itemTag.putString("id", stack.getItem().getDescriptionId());
				itemTag.put("tag", stack.getTag() == null ? new CompoundTag() : stack.getTag()); // optional
				itemTag.putInt("LargeCount", stack.getCount());
				stack.save(itemTag);
				tagList.add(itemTag);
			}
		}
		return tagList;
	}

	public static void loadBigStacks(ListTag tagList, NonNullList<ItemStack> items) {
		items.clear();
		for (int i = 0; i < items.size(); i++) {
			items.set(i, ItemStack.EMPTY);
		}

		for (int i = 0; i < tagList.size(); ++i) {
			CompoundTag tag = tagList.getCompound(i);
			int slot = tag.getByte("Slot") & 255;
			if (slot >= 0 && slot < items.size()) {
				ItemStack stack = ItemStack.of(tag);
				stack.setCount(tag.getInt("LargeCount")); // Restore large count
				items.set(slot, stack);
			}
		}
	}


	protected NonNullList<ItemStack> getItems() {
		return this.inventory;
	}

	protected void setItems(NonNullList<ItemStack> pItemStacks) {
		this.inventory = pItemStacks;
	}

	@Nonnull
	public NonNullList<ItemStack> getInventory() {
		return this.inventory;
	}

	public boolean isStackValid(int slot, ItemStack stack) {
		return IEApi.isAllowedInCrate(stack);
	}

	public int getSlotLimit(int slot) {
		return 64;
	}

	public void doGraphicalUpdates() {
		this.setChanged();
	}

	public void getBlockEntityDrop(LootContext context, Consumer<ItemStack> drop) {
		ItemStack stack = new ItemStack(this.getBlockState().getBlock(), 1);
		CompoundTag tag = new CompoundTag();
		ContainerHelper.saveAllItems(tag, this.inventory, false);
		if (!tag.isEmpty()) {
			stack.setTag(tag);
		}

		Component customName = this.getCustomName();
		if (customName != null) {
			stack.setHoverName(customName);
		}

		if (this.enchantments != null && this.enchantments.size() > 0) {
			stack.getOrCreateTag().put("ench", this.enchantments);
		}

		drop.accept(stack);
	}

	public void onBEPlaced(BlockPlaceContext ctx) {
		this.onBEPlaced(ctx.getItemInHand());
	}

	public void onBEPlaced(ItemStack stack) {
		if (stack.hasTag()) {
			this.loadIEData(stack.getOrCreateTag());
			if (stack.hasCustomHoverName()) {
				this.setCustomName(stack.getHoverName());
			}

			this.enchantments = stack.getEnchantmentTags();
		}

	}

	@Nonnull
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		return cap == ForgeCapabilities.ITEM_HANDLER ? this.inventoryCap.cast() : super.getCapability(cap, side);
	}

	public void invalidateCaps() {
		super.invalidateCaps();
		this.inventoryCap.invalidate();
	}

	public boolean canPlaceItem(int index, ItemStack stack) {
		return this.isStackValid(index, stack);
	}

	public int getComparatorInputOverride() {
		return Utils.calcRedstoneFromInventory(this);
	}

	public int getContainerSize() {
		return 27;
	}
}
