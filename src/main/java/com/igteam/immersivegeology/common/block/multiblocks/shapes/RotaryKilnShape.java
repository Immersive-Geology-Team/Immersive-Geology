package com.igteam.immersivegeology.common.block.multiblocks.shapes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RotaryKilnShape extends GenericShape {

    public static final RotaryKilnShape GETTER = new RotaryKilnShape();

    private RotaryKilnShape(){};

    @NotNull
    @Override
    protected List<AABB> getShape(BlockPos posInMultiblock) {
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        List<AABB> main = new ArrayList<>();
        main.add(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
        return main;
    }
}
