/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class IGGeothermalExchangerMultiblock extends IGTemplateMultiblock {

    public static final IGGeothermalExchangerMultiblock INSTANCE = new IGGeothermalExchangerMultiblock();

    public IGGeothermalExchangerMultiblock() {
        super(ResourceLocation.fromNamespaceAndPath(IGLib.MODID, "multiblocks/geothermal_exchanger"),
                new BlockPos(2,3,1),
                new BlockPos(3,4,2),
                new BlockPos(5, 7, 3), IGMultiblockProvider.GEOTHERMAL_EXCHANGER);
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
        consumer.accept(new IGClientMultiblockProperties(this, 2.5, 3.5, 1.5));
    }

    @Override
    public String getName()
    {
        return "Geothermal Exchanger";
    }
}
