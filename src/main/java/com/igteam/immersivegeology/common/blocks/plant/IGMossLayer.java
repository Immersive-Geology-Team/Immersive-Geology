package com.igteam.immersivegeology.common.blocks.plant;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.blocks.IGLayerBase;
import com.igteam.immersivegeology.common.blocks.property.IGVanillaMaterials;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class IGMossLayer extends IGLayerBase {
    public IGMossLayer(MaterialUseType use_type, Material material) {
        super(use_type, material, IGVanillaMaterials.MOSS);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        int layer_index = state.get(LAYERS).intValue();
        double mult = 1.0D - (layer_index * 0.1);
        entity.setMotion(entity.getMotion().mul(mult, 1.0D, mult));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
        return SHAPES[1];
    }
}
