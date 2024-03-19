package com.igteam.immersivegeology.common.blocks.multiblocks.shapes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CrystallizerShape extends GenericShape {

    public static final CrystallizerShape GETTER = new CrystallizerShape();

    private CrystallizerShape(){};

    @NotNull
    @Override
    protected List<AABB> getShape(BlockPos posInMultiblock) {
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        List<AABB> main = new ArrayList<>();

        return main;
    }
}