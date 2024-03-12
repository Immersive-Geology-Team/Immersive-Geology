package com.igteam.immersivegeology.common.blocks.multiblocks.shapes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

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

        if (bY == 0) {

            if (bX == 0 && bZ == 0) {
                return Arrays.asList(new AABB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
                        new AABB(0.0625, 0.5, 0.0625, 0.3175, 1.0, 0.3175));
            }

            if (bX == 0 && bZ == 2) {
                return Arrays.asList(new AABB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
                        new AABB(0.0625, 0.5, 0.9375, 0.3175, 1.0, 0.6825));
            }

            if (bX == 2 && bZ == 2) {
                return Arrays.asList(new AABB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
                        new AABB(0.9375, 0.5, 0.9375, 0.6825, 1.0, 0.6825));
            }

            if (bX == 2 && bZ == 0) {
                return Arrays.asList(new AABB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
                        new AABB(0.9375, 0.5, 0.0625, 0.6825, 1.0, 0.3175));
            }
            return Arrays.asList(new AABB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));
        }

        if (bY == 1) {
            if (bX == 0 && bZ == 0) {
                return Arrays.asList(new AABB(0.0625, 0.0, 0.0625, 1, 1.0, 1));
            }

            if (bX == 0 && bZ == 2) {
                return Arrays.asList(
                        new AABB(0.0625, 0.0, 0.9375, 1, 1.0, 0));
            }

            if (bX == 2 && bZ == 2) {
                return Arrays.asList(new AABB(0.9375, 0.0, 0.9375, 0, 1.0, 0));
            }

            if (bX == 2 && bZ == 0) {
                return Arrays.asList(new AABB(0.9375, 0.0, 0.0625, 0, 1.0, 1));
            }
        }
        if (bY == 2) {
            if (bX == 0 && bZ == 0) {
                return Arrays.asList(new AABB(0.0625, 0.0, 0.0625, 1, 0.25, 1));
            }

            if (bX == 0 && bZ == 2) {
                return Arrays.asList(
                        new AABB(0.0625, 0.0, 0.9375, 1, 0.25, 0));
            }

            if (bX == 2 && bZ == 2) {
                return Arrays.asList(new AABB(0.9375, 0.0, 0.9375, 0, 0.25, 0));
            }

            if (bX == 2 && bZ == 0) {
                return Arrays.asList(new AABB(0.9375, 0.0, 0.0625, 0, 0.25, 1));
            }
            if (bX == 1 && bZ == 1) {
                return Arrays.asList(new AABB(0.125, 0.0, 0.125, 0.875, 0.75, 0.875),
                        new AABB(0.25, 0.75, 0.25, 0.75, 1.0, 0.75));
            }
            return Arrays.asList(new AABB(0.0, 0.0, 0.0, 1.0, 0.25, 1.0));

        }

        return Arrays.asList(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
    }
}
