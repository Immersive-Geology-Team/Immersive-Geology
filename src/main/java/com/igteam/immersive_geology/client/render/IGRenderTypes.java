package com.igteam.immersive_geology.client.render;

import com.igteam.immersive_geology.core.lib.IGLib;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;


public class IGRenderTypes {

    static final ResourceLocation activeTexture = new ResourceLocation(IGLib.MODID, "textures/multiblock/bloomery_burning.png");

    public static RenderType BLOOMERY_ACTIVE;

    static final RenderState.TextureState TEXTURE_BLOOMERY_ACTIVE = new RenderState.TextureState(activeTexture, false, false);
    static final RenderState.ShadeModelState SHADE_ENABLED = new RenderState.ShadeModelState(true);
    static final RenderState.LightmapState LIGHTMAP_ENABLED = new RenderState.LightmapState(true);
    static final RenderState.OverlayState OVERLAY_ENABLED = new RenderState.OverlayState(true);
    static final RenderState.OverlayState OVERLAY_DISABLED = new RenderState.OverlayState(false);
    static final RenderState.DepthTestState DEPTH_ALWAYS = new RenderState.DepthTestState("always", GL11.GL_ALWAYS);
    static final RenderState.TransparencyState TRANSLUCENT_TRANSPARENCY = new RenderState.TransparencyState("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }, RenderSystem::disableBlend);

    static final RenderState.TransparencyState NO_TRANSPARENCY = new RenderState.TransparencyState("no_transparency", () -> {
        RenderSystem.disableBlend();
    }, () -> {
    });

    static final RenderState.DiffuseLightingState DIFFUSE_LIGHTING_ENABLED = new RenderState.DiffuseLightingState(true);

    static {
        BLOOMERY_ACTIVE = RenderType.makeType(
                IGLib.MODID + ":bloomery_active",
                DefaultVertexFormats.BLOCK,
                GL11.GL_QUADS,
                256,
                true,
                false,
                RenderType.State.getBuilder()
                        .texture(TEXTURE_BLOOMERY_ACTIVE)
                        .shadeModel(SHADE_ENABLED)
                        .lightmap(LIGHTMAP_ENABLED)
                        .overlay(OVERLAY_DISABLED)
                        .build(false)
        );
    }

    public static RenderType getEntitySolid(ResourceLocation locationIn){
        RenderType.State renderState = RenderType.State.getBuilder()
                .texture(new RenderState.TextureState(locationIn, false, false))
                .transparency(NO_TRANSPARENCY)
                .diffuseLighting(DIFFUSE_LIGHTING_ENABLED)
                .lightmap(LIGHTMAP_ENABLED)
                .overlay(OVERLAY_DISABLED)
                .build(true);
        return RenderType.makeType("entity_solid", DefaultVertexFormats.ENTITY, 7, 256, true, false, renderState);
    }
}
