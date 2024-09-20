package com.igteam.immersivegeology.common.block.multiblocks.shapes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RevFurnaceShape extends GenericShape {

    public static final RevFurnaceShape GETTER = new RevFurnaceShape();

    private RevFurnaceShape(){};

    @NotNull
    @Override
    protected List<AABB> getShape(BlockPos posInMultiblock) {
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        List<AABB> main = new ArrayList<>();

        if (bX < 3) {
            if (bY == 2) {
                if (bZ == 0) {
                    main.add(bX == 0 ? new AABB(0.125, 0.0, 0.25, 1.0, 0.5, 1.0) : new AABB(0.0, 0.0, 0.25, 1.0, 0.5, 1.0));
                } else if (bZ == 2 || bZ == 3) {
                    main.add(bX == 0 ? new AABB(0.125, 0.0, 0.0, 1.0, 0.5, 1.0) : new AABB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));
                } else if (bZ == 5) {
                    main.add(bX == 0 ? new AABB(0.125, 0.0, 0.0, 1.0, 0.5, 0.75) : new AABB(0.0, 0.0, 0.0, 1.0, 0.5, 0.75));
                }
            } else if (bY < 2) {
                if (bZ == 0) {
                    main.add(bX == 0 ? new AABB(0.125, 0.0, 0.25, 1.0, 1.0, 1.0) : new AABB(0.0, 0.0, 0.25, 1.0, 1.0, 1.0));
                } else if (bZ == 5) {
                    main.add(bX == 0 ? new AABB(0.125, 0.0, 0.0, 1.0, 1.0, 0.75) : new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 0.75));
                } else if (bX == 0) {
                    if (bY == 0 && (bZ == 1 || bZ == 4)) {
                        main.add(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
                    } else {
                        main.add(new AABB(0.125, 0.0, 0.0, 1.0, 1.0, 1.0));
                    }
                }
            }
        }

        if (bY >= 6 && bY < 10) {
            if (bX == 3) {
                main.add(new AABB(0.5, 0.0, 0.0, 1.0, 1.0, 1.0));
            } else if (bX == 5) {
                main.add(new AABB(0.0, 0.0, 0.0, 0.5, 1.0, 1.0));
            } else if (bX == 4) {
                if (bZ == 5 || bZ == 2) {
                    main.add(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 0.5));
                } else if (bZ == 0 || bZ == 3) {
                    main.add(new AABB(0.0, 0.0, 0.5, 1.0, 1.0, 1.0));
                }
            }
        }

        if (main.isEmpty()) {
            main.add(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
        }

        return main;
    }
}
