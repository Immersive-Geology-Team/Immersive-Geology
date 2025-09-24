package com.igteam.immersivegeology.core.registration;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.gui.IEBaseContainerOld;
import blusunrize.immersiveengineering.common.gui.IEContainerMenu;
import blusunrize.immersiveengineering.common.gui.IEContainerMenu.MultiblockMenuContext;
import blusunrize.immersiveengineering.common.register.IEMenuTypes.ArgContainerConstructor;
import blusunrize.immersiveengineering.common.register.IEMenuTypes.ClientContainerConstructor;
import blusunrize.immersiveengineering.common.register.IEMenuTypes.SimpleContainerConstructor;
import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.common.block.multiblocks.gui.*;
import com.igteam.immersivegeology.common.block.multiblocks.logic.*;
import com.igteam.immersivegeology.common.menu.IGCrateMenu;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class IGMenuTypes
{
	public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.MENU_TYPES, IGLib.MODID);
	public static final MultiblockContainer<BloomeryLogic.State, BloomeryMenu> BLOOMERY = registerMultiblock(IGLib.GUIID_Bloomery, BloomeryMenu::makeServer, BloomeryMenu::makeClient);
	public static final MultiblockContainer<RevFurnaceLogic.State, ReverberationFurnaceMenu> REVERBERATION_FURNACE = registerMultiblock(IGLib.GUIID_RevFurnace, ReverberationFurnaceMenu::makeServer, ReverberationFurnaceMenu::makeClient);
	public static final MultiblockContainer<CrystallizerLogic.State, CrystallizerMenu> CRYSTALLIZER = registerMultiblock(IGLib.GUIID_Crystallizer, CrystallizerMenu::makeServer, CrystallizerMenu::makeClient);
	public static final MultiblockContainer<ChemicalReactorLogic.State, ChemicalReactorMenu> CHEMICAL_REACTOR = registerMultiblock(IGLib.GUIID_ChemicalReactor, ChemicalReactorMenu::makeServer, ChemicalReactorMenu::makeClient);
	public static final MultiblockContainer<SmallChemicalReactorLogic.State, SmallChemicalReactorMenu> SMALL_CHEMICAL_REACTOR = registerMultiblock(IGLib.GUIID_SmallChemicalReactor, SmallChemicalReactorMenu::makeServer, SmallChemicalReactorMenu::makeClient);
	public static final MultiblockContainer<RotaryKilnLogic.State, RotaryKilnMenu> ROTARY_KILN = registerMultiblock(IGLib.GUIID_RotaryKiln, RotaryKilnMenu::makeServer, RotaryKilnMenu::makeClient);
	public static final MultiblockContainer<GeothermalExchangerLogic.State, GeothermalExchangerMenu> GEOTHERMAL_EXCHANGER = registerMultiblock(IGLib.GUIID_GeothermalExchanger, GeothermalExchangerMenu::makeServer, GeothermalExchangerMenu::makeClient);

	public static final RegistryObject<MenuType<IGCrateMenu>> CRATE = registerSimple(IGLib.GUIID_Crate, IGCrateMenu::new);

	public static <M extends AbstractContainerMenu>
	RegistryObject<MenuType<M>> registerSimple(String name, SimpleContainerConstructor<M> factory)
	{
		return REGISTER.register(
				name, () -> {
					Mutable<MenuType<M>> typeBox = new MutableObject<>();
					MenuType<M> type = new MenuType<>((id, inv) -> factory.construct(typeBox.getValue(), id, inv), FeatureFlagSet.of());
					typeBox.setValue(type);
					return type;
				}
		);
	}

	public static <T, C extends IEContainerMenu>
	ArgContainer<T, C> registerArg(
			String name, ArgContainerConstructor<T, C> container, ClientContainerConstructor<C> client
	)
	{
		RegistryObject<MenuType<C>> typeRef = registerType(name, client);
		return new ArgContainer<>(typeRef, container);
	}


	public static <T extends BlockEntity, C extends IEBaseContainerOld<? super T>>
	ArgContainer<T, C> register(String name, ArgContainerConstructor<T, C> container)
	{
		RegistryObject<MenuType<C>> typeRef = REGISTER.register(
				name, () -> {
					Mutable<MenuType<C>> typeBox = new MutableObject<>();
					MenuType<C> type = new MenuType<>((IContainerFactory<C>)(windowId, inv, data) -> {
						Level world = ImmersiveGeology.proxy.getClientWorld();
						BlockPos pos = data.readBlockPos();
						BlockEntity te = world.getBlockEntity(pos);
						return container.construct(typeBox.getValue(), windowId, inv, (T)te);
					}, FeatureFlagSet.of());
					typeBox.setValue(type);
					return type;
				}
		);
		return new ArgContainer<>(typeRef, container);
	}

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
			return this.provide(new IEContainerMenu.MultiblockMenuContext<>(ctx, ctx.getLevel().toAbsolute(relativeClicked)));
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
			Mutable<MenuType<C>> typeBox = new MutableObject<>();
			MenuType<C> type = new MenuType<>((id, inv) -> {
				return client.construct(typeBox.getValue(), id, inv);
			}, FeatureFlagSet.of());
			typeBox.setValue(type);
			return type;
		});
	}
}
