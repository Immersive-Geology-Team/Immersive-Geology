/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.entity.crate;

import blusunrize.immersiveengineering.api.IEApi;
import blusunrize.immersiveengineering.api.utils.CapabilityUtils;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.IEInventoryHandler;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.common.menu.IGCrateMenu;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.registration.IGMenuTypes;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import com.mojang.serialization.Dynamic;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class IGCrateEntity extends RandomizableContainerBlockEntity implements IIEInventory, IEBlockInterfaces.IBlockEntityDrop, IEBlockInterfaces.IComparatorOverride
{
	public static final int CONTAINER_SIZE = 36;
	private NonNullList<ItemStack> inventory;
	private ItemEnchantments enchantments = ItemEnchantments.EMPTY;
	private final LazyOptional<IItemHandler> inventoryCap;

	public IGCrateEntity(BlockPos pos, BlockState state) {
		super(resolveEntityType(state), pos, state);
		this.inventory = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
		this.inventoryCap = CapabilityUtils.constantOptional(new IEInventoryHandler(CONTAINER_SIZE, this));
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
	protected @NotNull Component getDefaultName()
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

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
		super.loadAdditional(nbt, provider);
		this.loadIEData(nbt, provider);
	}

	private void loadIEData(CompoundTag nbt, HolderLookup.Provider provider) {
		Tag enchantmentsTag = nbt.get("enchantments");
		if (enchantmentsTag instanceof ListTag legacyEnchantments) {
			CompoundTag levels = new CompoundTag();
			for (int i = 0; i < legacyEnchantments.size(); ++i) {
				CompoundTag enchantment = legacyEnchantments.getCompound(i);
				if (enchantment.contains("id", Tag.TAG_STRING) && enchantment.contains("lvl", Tag.TAG_ANY_NUMERIC))
					levels.putInt(enchantment.getString("id"), Mth.clamp(enchantment.getInt("lvl"), 0, 255));
			}
			CompoundTag converted = new CompoundTag();
			converted.put("levels", levels);
			enchantmentsTag = converted;
		}

		if (enchantmentsTag instanceof CompoundTag) {
			this.enchantments = ItemEnchantments.CODEC
					.parse(provider.createSerializationContext(NbtOps.INSTANCE), enchantmentsTag)
					.resultOrPartial(error -> IGLib.IG_LOGGER.warn("Tried to load invalid enchantment at '{}: {}'", this.worldPosition, error))
					.orElse(ItemEnchantments.EMPTY);
		}
		else {
			this.enchantments = ItemEnchantments.EMPTY;
		}

		if (nbt.contains("lootTable", 8) && !nbt.contains("LootTable")) {
			nbt.putString("LootTable", nbt.getString("lootTable"));
		}

		if (!this.tryLoadLootTable(nbt)) {
			CompoundTag inventoryTag = nbt.copy();
			ListTag itemsTag = inventoryTag.getList("Items", Tag.TAG_COMPOUND);
			for (int i = 0; i < itemsTag.size(); ++i) {
				CompoundTag itemTag = itemsTag.getCompound(i);
				if (itemTag.contains("Count", Tag.TAG_ANY_NUMERIC)) {
					itemsTag.set(i, DataFixers.getDataFixer().update(
									References.ITEM_STACK,
									new Dynamic<>(NbtOps.INSTANCE, itemTag),
									3465,
									SharedConstants.getCurrentVersion().getDataVersion().getVersion()
					).getValue());
				}
			}
			ContainerHelper.loadAllItems(inventoryTag, this.inventory, provider);
		}

	}

	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
		super.saveAdditional(nbt, provider);
		if (!this.enchantments.isEmpty()) {
			nbt.put("enchantments", ItemEnchantments.CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), this.enchantments).getOrThrow());
		}

		if (!this.trySaveLootTable(nbt)) {
			ContainerHelper.saveAllItems(nbt, this.inventory, provider);
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
		if (this.inventory.stream().anyMatch(item -> !item.isEmpty()))
			stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.inventory));

		Component customName = this.getCustomName();
		if (customName != null) {
			stack.set(DataComponents.CUSTOM_NAME, customName);
		}

		if (!this.enchantments.isEmpty()) {
			stack.set(DataComponents.ENCHANTMENTS, this.enchantments);
		}

		drop.accept(stack);
	}

	public void onBEPlaced(BlockPlaceContext ctx) {
		this.onBEPlaced(ctx.getItemInHand());
	}

	public void onBEPlaced(ItemStack stack) {
		this.enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
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
		return CONTAINER_SIZE;
	}
}
