/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import com.igteam.immersivegeology.client.models.IGDynamicModel;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Consumer;

import static com.igteam.immersivegeology.client.renderer.multiblocks.BallmillRenderer.AXLE;
import static com.igteam.immersivegeology.client.renderer.multiblocks.BallmillRenderer.DRUM;

public class IGGeothermalExchangerMultiblock extends IGTemplateMultiblock {

    public static final IGGeothermalExchangerMultiblock INSTANCE = new IGGeothermalExchangerMultiblock();

    public IGGeothermalExchangerMultiblock() {
        super(new ResourceLocation(IGLib.MODID, "multiblocks/geothermal_exchanger"), new BlockPos(2,4,1), new BlockPos(2,5,1), new BlockPos(5,6, 3), IGMultiblockProvider.GEOTHERMAL_EXCHANGER);
    }

    @Override
    public boolean canFormWithDefaultHammer()
    {
        return true;
    }

    @Override
    public float getManualScale() {
        return 12;
    }

    @Override
    public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer) {
        consumer.accept(new IGClientMultiblockProperties(this, 1.5, 4, 1.5));
    }

    @Override
    public String getName()
    {
        return "Geothermal Exchanger";
    }
}
