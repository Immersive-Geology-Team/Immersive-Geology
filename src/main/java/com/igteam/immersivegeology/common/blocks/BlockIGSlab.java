package com.igteam.immersivegeology.common.blocks;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.common.IGContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

public class BlockIGSlab<T extends Block & IIGBlock> extends SlabBlock implements IIGBlock
{
    private final T base;

    public BlockIGSlab(String name, Properties props, Class<? extends BlockItem> itemBlock, T base)
    {
        super(props);
        ResourceLocation registryName = new ResourceLocation(ImmersiveGeology.MODID, name);
        setRegistryName(registryName);

        IGContent.registeredIGBlocks.add(this);
        try
        {
            IGContent.registeredIGItems.add(itemBlock.getConstructor(Block.class)
                    .newInstance(this));
        } catch(Exception e)
        {
            //TODO e.printStackTrace();
            throw new RuntimeException(e);
        }
        this.base = base;
    }

    @Override
    public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity)
    {
        double relativeEntityPosition = entity.getPosition().getY()-pos.getY();
        switch(state.get(SlabBlock.TYPE))
        {
            case TOP:
                return 0.5 < relativeEntityPosition&&relativeEntityPosition < 1;
            case BOTTOM:
                return 0 < relativeEntityPosition&&relativeEntityPosition < 0.5;
            case DOUBLE:
                return true;
        }
        return false;
    }

    @Override
    public boolean hasFlavour()
    {
        return base.hasFlavour();
    }

    @Override
    public String getNameForFlavour()
    {
        return base.getNameForFlavour();
    }

    @Override
    public BlockRenderLayer getRenderLayer()
    {
        return base.getRenderLayer();
    }

    @Override
    public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer)
    {
        return base.canRenderInLayer(base.getDefaultState(), layer);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getOpacity(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return base.getOpacity(state, worldIn, pos);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos)
    {
        return base.propagatesSkylightDown(state, reader, pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean causesSuffocation(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return base.causesSuffocation(state, worldIn, pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return base.isNormalCube(state, worldIn, pos);
    }
}
