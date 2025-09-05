/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.renderer.multiblocks;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelperMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockOrientation;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext.ProcessContextInWorld;
import com.igteam.immersivegeology.client.helper.LinkedMultiSkin;
import com.igteam.immersivegeology.client.models.IGDynamicModel;
import com.igteam.immersivegeology.client.renderer.IGBlockEntityRenderer;
import com.igteam.immersivegeology.common.block.multiblocks.IGPelletizerMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.logic.PelletizerLogic;
import com.igteam.immersivegeology.common.block.multiblocks.part.PelletizerPart;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.PelletizerRecipe;
import com.igteam.immersivegeology.common.block.multiblocks.skins.IGPelletizerSkins;
import com.igteam.immersivegeology.common.config.IGClientConfig;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

@LinkedMultiSkin(multiblock = IGPelletizerMultiblock.class)
public class PelletizerRenderer extends IGBlockEntityRenderer<MultiblockBlockEntityMaster<PelletizerLogic.State>>
{
    public static final String DISH_NAME = "pelletizer_dish";
    public static final String FUNNEL_NAME = "pelletizer_funnel";

    // Cache quads per skin name
    private final Map<String, List<BakedQuad>> quadCache = new HashMap<>();
    public static IGDynamicModel DISH, FUNNEL;

    private ItemStack renderStack = ItemStack.EMPTY;

    @Override
    public void render(MultiblockBlockEntityMaster<PelletizerLogic.State> tile, float pPartialTick, PoseStack poseStack, @NotNull MultiBufferSource buffer, int pPackedLight, int pPackedOverlay)
    {
        boolean doRendering = IGClientConfig.doSpecialRenderPelletizer.get();
        if(!doRendering) return;

        IMultiblockBEHelperMaster<PelletizerLogic.State> helper = tile.getHelper();
        IMultiblockContext<PelletizerLogic.State> context = helper.getContext();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        final MultiblockOrientation orientation = context.getLevel().getOrientation();
        final PelletizerLogic.State state = context.getState();
        final IGPelletizerSkins skin = tile.getBlockState().getValue(PelletizerPart.PELLETIZER);
        float rot = state.getRotation();
        BlockPos pos = tile.getBlockPos();
        Level level = tile.getLevel();
        Direction dir = orientation.front();
        boolean isActive = state.shouldRenderActive();
        float angleDrum = isActive ? (rot) - pPartialTick : rot;
        List<MultiblockProcess<PelletizerRecipe, ProcessContextInWorld<PelletizerRecipe>>> queue = state.getProcessQueue();
        boolean canProcess = state.energy.getEnergyStored() > 0 &&
                state.tank.getFluid().getFluid().isSame(ChemicalEnum.BindingAgent.getFluid(BlockCategoryFlags.FLUID));
        ItemStack stack = state.getInventory().getStackInSlot(0);
        poseStack.pushPose();
            rotateForFacing(poseStack, dir);
            poseStack.pushPose();
                poseStack.translate(.5,1.921875,0.609375);
                poseStack.mulPose(new Quaternionf().rotateAxis(30 * Mth.DEG_TO_RAD, new Vector3f(1, 0, 0)));
                poseStack.pushPose();
                    poseStack.mulPose(new Quaternionf().rotateAxis(angleDrum * Mth.DEG_TO_RAD, new Vector3f(0, 1, 0)));
                    renderDynamicModel(DISH, poseStack, buffer, Direction.NORTH, level, pos, pPackedLight, pPackedOverlay, skin);
                poseStack.popPose();
                poseStack.translate(0,0,1);
                poseStack.pushPose();
                poseStack.mulPose(new Quaternionf().rotateAxis(-(canProcess ? 90 : 105) * Mth.DEG_TO_RAD, new Vector3f(1,0,0)));
                poseStack.pushPose();
                int maxIndex = Math.min(16, queue.size());
                for (int index = maxIndex - 1; index >= 0; --index)
                {
                    MultiblockProcess<PelletizerRecipe, ProcessContextInWorld<PelletizerRecipe>> process = queue.get(index);
                    int processTick = process.processTick;
                    int trueMax = process.getMaxTicks(level);
                    int maxProcess = Math.round(trueMax * 0.5f);
                    PelletizerRecipe recipe = process.getRecipe(tile.getLevel());

                    if (recipe != null)
                    {
                        ItemStack input = recipe.itemIn.getRandomizedExampleStack(0);
                        ItemStack output = recipe.itemOutput.get();

                        // Position across a semi-ellipse (spread horizontally, curved vertically)
                        float angle = (index / (float)(maxIndex)) * (float)Math.PI; // [0, π]
                        float radiusX = .35f; // horizontal spread max
                        float radiusY = .130f; // vertical max height

                        float xOffset = (float)(Math.cos(angle) * radiusX) - 0.0625f; // [-1.5, 1.5]
                        float yOffset = (float)(Math.sin(angle) * -radiusY) + (canProcess ? 0.125f : 0.0125f);

                        poseStack.translate(xOffset, yOffset, (index / 8f) * 0.01f);
                        poseStack.pushPose();
                        if(processTick > maxProcess)
                        {
                            float trueProcess = ((float)processTick/trueMax) - 0.5f;
                            float scale = 0.5f + trueProcess;
                            poseStack.scale(scale, scale, 1);
                        }
                        else
                        {
                            float progress = ((float)processTick/maxProcess) * 0.5f;
                            float scale = 1 - progress;
                            poseStack.scale(scale, scale, 1);
                        }
                        poseStack.mulPose(new Quaternionf().rotateAxis((((index * 15) * (((index ^ 1) == 0) ? -1 : 1)) + (angleDrum * 4) % 360) * Mth.DEG_TO_RAD, new Vector3f(0, 0, 1)));
                        itemRenderer.renderStatic(processTick > maxProcess ? output : input, ItemDisplayContext.GROUND, pPackedLight, pPackedOverlay, poseStack, buffer, level, 0);
                        poseStack.popPose();

                        poseStack.translate(-xOffset, -yOffset, -(index / 8f) * 0.01f);


                    }
                }

                if(queue.isEmpty() &! stack.isEmpty())
                {
                    int max = Math.min(7, stack.getCount());
                    for (int index = max; index >= 0; --index)
                    {

                            // Position across a semi-ellipse (spread horizontally, curved vertically)
                            float angle = (index / (float)(8)) * (float)Math.PI; // [0, π]
                            float radiusX = .35f; // horizontal spread max
                            float radiusY = .130f; // vertical max height

                            float xOffset = (float)(Math.cos(angle) * radiusX) - 0.0625f; // [-1.5, 1.5]
                            float yOffset = (float)(Math.sin(angle) * -radiusY) + (canProcess ? 0.125f : 0.0115f);

                            poseStack.translate(xOffset, yOffset, (index / 8f) * 0.01f);
                            poseStack.pushPose();
                            poseStack.mulPose(new Quaternionf().rotateAxis((((index * 15) * (((index ^ 1) == 0) ? -1 : 1)) + (angleDrum * 4) % 360) * Mth.DEG_TO_RAD, new Vector3f(0, 0, 1)));
                            itemRenderer.renderStatic(stack, ItemDisplayContext.GROUND, pPackedLight, pPackedOverlay, poseStack, buffer, level, 0);
                            poseStack.popPose();

                            poseStack.translate(-xOffset, -yOffset, -(index / 8f) * 0.01f);
                    }
                }
                poseStack.popPose();
                poseStack.popPose();
            poseStack.popPose();
        poseStack.popPose();
    }
}
