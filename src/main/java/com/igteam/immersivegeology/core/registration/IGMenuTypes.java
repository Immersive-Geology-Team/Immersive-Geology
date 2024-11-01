/*
 * ${USER}
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.registration;


import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.gui.IEContainerMenu;
import blusunrize.immersiveengineering.common.gui.IEContainerMenu.MultiblockMenuContext;
import blusunrize.immersiveengineering.common.register.IEMenuTypes;
import blusunrize.immersiveengineering.common.register.IEMenuTypes.ArgContainer;
import blusunrize.immersiveengineering.common.register.IEMenuTypes.ArgContainerConstructor;
import blusunrize.immersiveengineering.common.register.IEMenuTypes.ClientContainerConstructor;
import blusunrize.immersiveengineering.common.register.IEMenuTypes.MultiblockContainer;
import com.igteam.immersivegeology.common.block.multiblocks.gui.BloomeryMenu;
import com.igteam.immersivegeology.common.block.multiblocks.logic.BloomeryLogic;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class IGMenuTypes
{
	public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.MENU_TYPES, IGLib.MODID);
	public static final MultiblockContainer<BloomeryLogic.State, BloomeryMenu> BLOOMERY = registerMultiblock(IGLib.GUIID_Bloomery, BloomeryMenu::makeServer, BloomeryMenu::makeClient);

	public static <S extends IMultiblockState, C extends IEContainerMenu> MultiblockContainer<S, C> registerMultiblock(String name, ArgContainerConstructor<IEContainerMenu.MultiblockMenuContext<S>, C> container, ClientContainerConstructor<C> client) {
		RegistryObject<MenuType<C>> typeRef = registerType(name, client);
		return new MultiblockContainer<>(typeRef, container);
	}

	public static class MultiblockContainer<S extends IMultiblockState, C extends IEContainerMenu> extends ArgContainer<MultiblockMenuContext<S>, C>
	{
		private MultiblockContainer(RegistryObject<MenuType<C>> type, ArgContainerConstructor<IEContainerMenu.MultiblockMenuContext<S>, C> factory) {
			super(type, factory);
		}

		public MenuProvider provide(IMultiblockContext<S> ctx, BlockPos relativeClicked) {
			return this.provide(new IEContainerMenu.MultiblockMenuContext(ctx, ctx.getLevel().toAbsolute(relativeClicked)));
		}
	}

	public static class ArgContainer<T, C extends IEContainerMenu> {
		private final RegistryObject<MenuType<C>> type;
		private final ArgContainerConstructor<T, C> factory;

		private ArgContainer(RegistryObject<MenuType<C>> type, ArgContainerConstructor<T, C> factory) {
			this.type = type;
			this.factory = factory;
		}

		public C create(int windowId, Inventory playerInv, T tile) {
			return this.factory.construct(this.getType(), windowId, playerInv, tile);
		}

		public MenuProvider provide(final T arg) {
			return new MenuProvider() {
				@Nonnull
				public Component getDisplayName() {
					return Component.empty();
				}

				@Nullable
				public AbstractContainerMenu createMenu(int containerId, @Nonnull Inventory inventory, @Nonnull Player player) {
					return IGMenuTypes.ArgContainer.this.create(containerId, inventory, arg);
				}
			};
		}

		public MenuType<C> getType() {
			return this.type.get();
		}
	}

	private static <C extends IEContainerMenu> RegistryObject<MenuType<C>> registerType(String name, ClientContainerConstructor<C> client) {
		return REGISTER.register(name, () -> {
			Mutable<MenuType<C>> typeBox = new MutableObject();
			MenuType<C> type = new MenuType((id, inv) -> {
				return client.construct(typeBox.getValue(), id, inv);
			}, FeatureFlagSet.of());
			typeBox.setValue(type);
			return type;
		});
	}
}
