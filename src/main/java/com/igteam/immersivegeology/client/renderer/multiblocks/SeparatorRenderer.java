/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.renderer.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelperMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockOrientation;
import com.igteam.immersivegeology.client.renderer.IGBlockEntityRenderer;
import com.igteam.immersivegeology.common.block.multiblocks.logic.GravitySeparatorLogic;
import com.igteam.immersivegeology.common.block.multiblocks.logic.helper.SeparatorProcess;
import com.igteam.immersivegeology.common.config.IGClientConfig;
import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.particle.IGParticles;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Random;

public class SeparatorRenderer extends IGBlockEntityRenderer<MultiblockBlockEntityMaster<GravitySeparatorLogic.State>>
{
    private ItemStack renderStack = ItemStack.EMPTY;
    Random rand = new Random();
    @Override
    public void render(MultiblockBlockEntityMaster<GravitySeparatorLogic.State> tile, float pPartialTick, PoseStack poseStack, @NotNull MultiBufferSource buffer, int pPackedLight, int pPackedOverlay)
    {
        boolean doRendering = IGClientConfig.doSpecialRenderGravitySeparator.get();
        if(!doRendering) return;

        IMultiblockBEHelperMaster<GravitySeparatorLogic.State> helper = tile.getHelper();
        IMultiblockContext<GravitySeparatorLogic.State> context = helper.getContext();
        final MultiblockOrientation orientation = context.getLevel().getOrientation();
        final GravitySeparatorLogic.State state = context.getState();
        boolean hasWater = !state.tank.isEmpty();
        ArrayList<SeparatorProcess> processList = state.separatorProcessesQueue;
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        if(processList.isEmpty()) return;
        BlockPos pos = tile.getBlockPos();
        Level level = tile.getLevel();
        Direction dir = orientation.front();
        boolean isActive = state.shouldRenderActive();

        if(level == null && !isActive) return;
        assert level!=null;
        poseStack.pushPose();
            rotateForFacing(poseStack, dir);
            poseStack.pushPose();
                poseStack.translate(.5f,4.5f,.5f);
                if(hasWater && level.getGameTime() % 5 == 0)
				{
					level.addParticle(IGParticles.FLOWING_WATER.get(),
							pos.getX()+0.5f+rand.nextFloat(-0.25f, 0.25f), // Adjusted for block position
							(pos.getY())+5.25f+rand.nextFloat(-0.25f, 0.25f),
							pos.getZ()+1.05f,
							0, -0.06f, 0.1f
					);
				}

                float twists = 11;
                float sin, cos;
                Quaternionf rotationY = new Quaternionf();
                Quaternionf rotationZ = new Quaternionf().rotateAxis(30 * Mth.DEG_TO_RAD, new Vector3f(0, 0, -1));
                Quaternionf rotationX = new Quaternionf().rotateAxis(-80 * Mth.DEG_TO_RAD, new Vector3f(1, 0, 0));

                for (SeparatorProcess process : processList) {
                    float progress = process.getRelativeProcessStep(level);
                    renderStack = progress > 0.66f ? process.getCurrentOutput() : process.getInput();
                    // Precompute trigonometric functions
                    sin = Mth.sin(progress * twists);
                    cos = Mth.cos(progress * twists);

                    float ax = -cos * 0.8f;
                    float ay = -3.5f * progress;
                    float az = sin * 0.8f;

                    float POINT_IN_ROT = (float) Math.toDegrees(Math.atan2(ax, az));

                    poseStack.pushPose();
                    poseStack.translate(ax, ay, az);

                    // Reuse Quaternion object instead of creating new ones
                    rotationY.identity().rotateAxis(POINT_IN_ROT * Mth.DEG_TO_RAD, new Vector3f(0, 1, 0));
                    poseStack.mulPose(rotationY);
                    poseStack.mulPose(rotationZ);
                    poseStack.mulPose(rotationX);

                    itemRenderer.renderStatic(renderStack, ItemDisplayContext.GROUND, pPackedLight, pPackedOverlay, poseStack, buffer, level, 0);
                    poseStack.popPose();
                    if (level.getGameTime() % 10 == 0 && hasWater) {
                        //Primary Ramp
                        level.addParticle(IGParticles.FLOWING_WATER.get(),
                                pos.getX()+az+0.4f+rand.nextFloat(-0.2f, 0.2f), // Adjusted for block position
                                (pos.getY()+ay)+4.5f + rand.nextFloat(-0.2f, 0.2f),
                                pos.getZ()-ax+0.4f+rand.nextFloat(-0.2f, 0.2f),
                                ax*0.05f,
                                -0.001f,
                                az*0.05f
                        );
                        // Secondary
                        /*
                        level.addParticle(ParticleTypes.END_ROD,
                                pos.getX()-az+0.5, // Adjusted for block position
                                (pos.getY()+ay)+4.8f,
                                pos.getZ()+ax+0.5,
                                (rand.nextDouble() - 0.5) * 0.02,
                                -0.0025f, // Slight downward drift
                                (rand.nextDouble() - 0.5) * 0.02
                        );
                         */
                    }
                }
            poseStack.popPose();
        poseStack.popPose();
    }
}
