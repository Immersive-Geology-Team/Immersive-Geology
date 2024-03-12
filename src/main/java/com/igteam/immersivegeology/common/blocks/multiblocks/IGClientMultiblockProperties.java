package com.igteam.immersivegeology.common.blocks.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

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
        return false;
    }

    @Override
    public void renderFormedStructure(PoseStack poseStack, MultiBufferSource multiBufferSource) {

    }
}
