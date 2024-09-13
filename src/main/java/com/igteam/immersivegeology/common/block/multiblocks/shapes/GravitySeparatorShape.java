package com.igteam.immersivegeology.common.block.multiblocks.shapes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GravitySeparatorShape extends GenericShape {

    public static final GravitySeparatorShape GETTER = new GravitySeparatorShape();

    private GravitySeparatorShape(){};

    @NotNull
    @Override
    protected List<AABB> getShape(BlockPos posInMultiblock) {
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        List<AABB> main = new ArrayList<>();
        if (bY == 6) {
            if (bZ == 1 && bX == 2) {
                main.add(new AABB(0.0, 0.75, 0.0, 0.5, 1.0, 1.0));
            } else if (bZ == 2 && bX == 1) {
                main.add(new AABB(0.0, 0.75, 0.0, 1.0, 1.0, 0.5));
            } else if (bZ == 0 && bX == 1) {
                main.add(new AABB(0.0, 0.75, 0.5, 1.0, 1.0, 1.0));
            } else if (bZ == 1 && bX == 0) {
                main.add(new AABB(0.5, 0.75, 0.0, 1.0, 1.0, 1.0));
            } else {
                main.add(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
            }
        } else {
            main.add(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
        }

        return main;
    }
}
