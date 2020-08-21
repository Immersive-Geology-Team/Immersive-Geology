package com.igteam.immersivegeology.common.blocks;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.entity.IGFallingBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TorchBlock;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class IGMaterialFallingBlock extends IGMaterialBlock
{
    public IGMaterialFallingBlock(MaterialUseType subtype, Material... materials) {
        super(subtype, materials);
    }

    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(worldIn));
    }

    /**
     * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific face passed in.
     */
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, this.tickRate(worldIn));
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
        if (!worldIn.isRemote) {
            this.checkFallable(worldIn, pos);
        }

    }

    private void checkFallable(World worldIn, BlockPos pos) {
        if (worldIn.isAirBlock(pos.down()) || canFallThrough(worldIn.getBlockState(pos.down())) && pos.getY() >= 0) {
            if (!worldIn.isRemote) {
                IGFallingBlockEntity fallingblockentity = new IGFallingBlockEntity(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, worldIn.getBlockState(pos));
                this.onStartFalling(fallingblockentity);
                worldIn.addEntity(fallingblockentity);
            }

        }
    }

    protected void onStartFalling(FallingBlockEntity fallingEntity) {
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(IWorldReader worldIn) {
        return 2;
    }

    public static boolean canFallThrough(BlockState state) {
        Block block = state.getBlock();
        net.minecraft.block.material.Material material = state.getMaterial();
        return state.isAir() || block == Blocks.FIRE || material.isLiquid() || material.isReplaceable() || !state.isSolid();
    }

    public void onEndFalling(World worldIn, BlockPos pos, BlockState fallingState, BlockState hitState)
    {

    }

    public void onBroken(World worldIn, BlockPos pos)
    {

    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (rand.nextInt(16) == 0)
        {
            BlockPos blockpos = pos.down();
            if (worldIn.isAirBlock(blockpos) || canFallThrough(worldIn.getBlockState(blockpos)))
            {
                double d0 = (double)((float)pos.getX() + rand.nextFloat());
                double d1 = (double)pos.getY() - 0.05D;
                double d2 = (double)((float)pos.getZ() + rand.nextFloat());
                worldIn.addParticle(new BlockParticleData(ParticleTypes.FALLING_DUST, stateIn), d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }
        }

    }
}
