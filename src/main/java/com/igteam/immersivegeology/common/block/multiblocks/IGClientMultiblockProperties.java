package com.igteam.immersivegeology.common.block.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Objects;

public class IGClientMultiblockProperties implements ClientMultiblocks.MultiblockManualData {
    private final IGTemplateMultiblock multiblock;
    private final ItemStack renderStack;
    @Nullable
    private final Vec3 renderOffset;
    @Nullable
    private NonNullList<ItemStack> materialList;

    public IGClientMultiblockProperties(IGTemplateMultiblock multiblock){
        this(multiblock, null);
    }

    public IGClientMultiblockProperties(IGTemplateMultiblock multiblock, double offX, double offY, double offZ){
        this(multiblock, new Vec3(offX, offY, offZ));
    }

    private IGClientMultiblockProperties(IGTemplateMultiblock multiblock, @Nullable Vec3 renderOffset){
        this.multiblock = multiblock;
        this.renderStack = new ItemStack(multiblock.getBlock());
        this.renderOffset = renderOffset;
    }

    @Override
    public NonNullList<ItemStack> getTotalMaterials() {
        return null;
    }

    @Override
    public boolean canRenderFormedStructure() {
        return this.renderOffset != null;
    }

    public void renderExtras(PoseStack matrix, MultiBufferSource buffer){
    }

    public void renderCustomFormedStructure(PoseStack matrix, MultiBufferSource buffer){
    }

    @Override
    public void renderFormedStructure(PoseStack matrix, MultiBufferSource buffer) {        
        Objects.requireNonNull(this.renderOffset);

        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();        
        
        matrix.translate(this.renderOffset.x, this.renderOffset.y, this.renderOffset.z);
        renderer.renderStatic(renderStack, ItemDisplayContext.NONE, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, matrix, buffer, null, 0);
        matrix.pushPose();
        {
            renderExtras(matrix, buffer);
        }
        matrix.popPose();
    }
}
