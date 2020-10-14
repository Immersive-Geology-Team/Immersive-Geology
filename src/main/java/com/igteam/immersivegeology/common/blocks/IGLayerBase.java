package com.igteam.immersivegeology.common.blocks;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.client.menu.helper.ItemSubGroup;
import com.igteam.immersivegeology.common.blocks.IGBaseBlock;
import com.igteam.immersivegeology.common.blocks.IGMaterialBlock;
import com.igteam.immersivegeology.common.blocks.property.IGVanillaMaterials;
import net.minecraft.block.*;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.*;

import javax.annotation.Nullable;
import java.util.Random;

public class IGLayerBase extends IGMaterialBlock {

    protected static final VoxelShape[] SHAPES;
    public static final IntegerProperty LAYERS;

    public IGLayerBase(MaterialUseType use_type, Material material, net.minecraft.block.material.Material van_mat)
    {
        super("", Properties.create(van_mat).doesNotBlockMovement().sound(SoundType.PLANT), use_type, material);
        this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(LAYERS, 1));
        this.mobilityFlag = PushReaction.DESTROY;
        this.notNormalBlock = true;
    }

    @Override
    public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
        switch(p_196266_4_) {
            case LAND:
                return (Integer)p_196266_1_.get(LAYERS) < 5;
            case WATER:
                return false;
            case AIR:
                return false;
            default:
                return false;
        }
    }

    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return SHAPES[(Integer)p_220053_1_.get(LAYERS)];
    }

    public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
        return SHAPES[(Integer)p_220071_1_.get(LAYERS) - 1];
    }

    public boolean func_220074_n(BlockState p_220074_1_) {
        return true;
    }

    public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
        BlockState lvt_4_1_ = p_196260_2_.getBlockState(p_196260_3_.down());
        Block lvt_5_1_ = lvt_4_1_.getBlock();
        if (lvt_5_1_ != Blocks.ICE && lvt_5_1_ != Blocks.PACKED_ICE && lvt_5_1_ != Blocks.BARRIER) {
            return Block.doesSideFillSquare(lvt_4_1_.getCollisionShape(p_196260_2_, p_196260_3_.down()), Direction.UP) || lvt_5_1_ == this && (Integer)lvt_4_1_.get(LAYERS) == 8;
        } else {
            return false;
        }
    }
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader world, BlockPos pos) {
        return true;
    }

    public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
        return !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
    }

    public boolean isReplaceable(BlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
        int lvt_3_1_ = (Integer)p_196253_1_.get(LAYERS);
        if (p_196253_2_.getItem().getItem() == this.asItem() && lvt_3_1_ < 8) {
            if (p_196253_2_.replacingClickedOnBlock()) {
                return p_196253_2_.getFace() == Direction.UP;
            } else {
                return true;
            }
        } else {
            return lvt_3_1_ == 1;
        }
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
        BlockState lvt_2_1_ = p_196258_1_.getWorld().getBlockState(p_196258_1_.getPos());
        if (lvt_2_1_.getBlock() == this) {
            int lvt_3_1_ = (Integer)lvt_2_1_.get(LAYERS);
            return (BlockState)lvt_2_1_.with(LAYERS, Math.min(8, lvt_3_1_ + 1));
        } else {
            return super.getStateForPlacement(p_196258_1_);
        }
    }

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        super.fillStateContainer(builder);
        builder.add(LAYERS);
    }

    static {
        LAYERS = BlockStateProperties.LAYERS_1_8;
        SHAPES = new VoxelShape[]{VoxelShapes.empty(), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};
    }
}
