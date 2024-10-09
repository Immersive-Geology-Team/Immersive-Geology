/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client;

import blusunrize.immersiveengineering.client.render.tile.DynamicModel;
import com.igteam.immersivegeology.client.models.IGDynamicModel;
import com.igteam.immersivegeology.client.renderer.multiblocks.ChemicalReactorRenderer;
import com.igteam.immersivegeology.client.renderer.multiblocks.CoreDrillRenderer;
import com.igteam.immersivegeology.common.block.multiblocks.IGClientMultiblockProperties;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
	}

	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
		registerBlockEntityRenderers(event);
	}

	public static void registerBlockEntityRenderers(RegisterRenderers event)
	{
		registerBERenderNoContext(event, IGMultiblockProvider.COREDRILL.masterBE(), CoreDrillRenderer::new);
		registerBERenderNoContext(event, IGMultiblockProvider.CHEMICAL_REACTOR.masterBE(), ChemicalReactorRenderer::new);
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
