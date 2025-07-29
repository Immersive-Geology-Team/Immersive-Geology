/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client;

import com.igteam.immersivegeology.client.menu.multiblock.BloomeryScreen;
import com.igteam.immersivegeology.client.models.IGDynamicModel;
import com.igteam.immersivegeology.client.renderer.multiblocks.*;
import com.igteam.immersivegeology.common.block.multiblocks.gui.BloomeryMenu;
import com.igteam.immersivegeology.common.particle.IGParticles;
import com.igteam.immersivegeology.common.particle.providers.FlowingWaterParticleProvider;
import com.igteam.immersivegeology.common.particle.types.FlowingWaterParticle;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMenuTypes;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.debug.GameEventListenerRenderer;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapDecoration.Type;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ColorResolverManager;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.client.event.RegisterColorHandlersEvent.ColorResolvers;
import net.minecraftforge.common.data.SpriteSourceProvider;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;


@Mod.EventBusSubscriber(modid = IGLib.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModBusEventHandlers {

	@SubscribeEvent
	public static void registerModelLoaders(ModelEvent.RegisterGeometryLoaders event)
	{
		CoreDrillRenderer.DRILL_BIT = new IGDynamicModel(CoreDrillRenderer.DRILL_BIT_NAME);
		CoreDrillRenderer.DRILL_ENGINE = new IGDynamicModel(CoreDrillRenderer.DRILL_ENGINE_NAME);
		CoreDrillRenderer.DRILL_ENGINE_SUPPORT = new IGDynamicModel(CoreDrillRenderer.DRILL_ENGINE_SUPPORT_NAME);
		CoreDrillRenderer.DRILL_GEARSET = new IGDynamicModel(CoreDrillRenderer.DRILL_GEARSET_NAME);
		BallmillRenderer.DRUM = new IGDynamicModel(BallmillRenderer.DRUM_NAME);
		BallmillRenderer.AXLE = new IGDynamicModel(BallmillRenderer.AXLE_NAME);
		PelletizerRenderer.DISH = new IGDynamicModel(PelletizerRenderer.DISH_NAME);
		RotaryKilnRenderer.TUBE = new IGDynamicModel(RotaryKilnRenderer.TUBE_NAME);
		SteamTurbineRenderer.ROTOR = new IGDynamicModel(SteamTurbineRenderer.ROTOR_NAME);
		AlternatorRenderer.WHEEL = new IGDynamicModel(AlternatorRenderer.WHEEL_NAME);
		CentrifugeRenderer.DRUM = new IGDynamicModel(CentrifugeRenderer.DRUM_NAME);
	}

	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
		registerBlockEntityRenderers(event);
	}

	@SubscribeEvent
	public static void registerParticleFactories(RegisterParticleProvidersEvent event)
	{
		event.registerSpriteSet(IGParticles.FLOWING_WATER.get(), FlowingWaterParticleProvider::new);
	}

	public static void registerBlockEntityRenderers(RegisterRenderers event)
	{
		registerBERenderNoContext(event, IGMultiblockProvider.COREDRILL.masterBE(), CoreDrillRenderer::new);
		registerBERenderNoContext(event, IGMultiblockProvider.BALLMILL.masterBE(), BallmillRenderer::new);
		registerBERenderNoContext(event, IGMultiblockProvider.PELLETIZER.masterBE(), PelletizerRenderer::new);
		registerBERenderNoContext(event, IGMultiblockProvider.ROTARYKILN.masterBE(), RotaryKilnRenderer::new);
		registerBERenderNoContext(event, IGMultiblockProvider.GRAVITY_SEPARATOR.masterBE(), SeparatorRenderer::new);
		registerBERenderNoContext(event, IGMultiblockProvider.CHEMICAL_REACTOR.masterBE(), ChemicalReactorRenderer::new);
		registerBERenderNoContext(event, IGMultiblockProvider.CENTRIFUGE.masterBE(), CentrifugeRenderer::new);
		registerBERenderNoContext(event, IGMultiblockProvider.STEAM_TURBINE.masterBE(), SteamTurbineRenderer::new);
		registerBERenderNoContext(event, IGMultiblockProvider.ALTERNATOR.masterBE(), AlternatorRenderer::new);
	}

	private static <T extends BlockEntity>
	void registerBERenderNoContext(RegisterRenderers event, Supplier<BlockEntityType<? extends T>> type, Supplier<BlockEntityRenderer<T>> render)
	{
		ClientModBusEventHandlers.registerBERenderNoContext(event, type.get(), render);
	}

	private static <T extends BlockEntity>
	void registerBERenderNoContext(RegisterRenderers event, BlockEntityType<? extends T> type, Supplier<BlockEntityRenderer<T>> render)
	{
		event.registerBlockEntityRenderer(type, $ -> render.get());
	}
}
