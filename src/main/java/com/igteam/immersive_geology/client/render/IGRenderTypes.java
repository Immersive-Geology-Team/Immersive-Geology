package com.igteam.immersive_geology.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class IGRenderTypes {

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
