package com.igteam.immersivegeology.common.entity;

import com.igteam.immersivegeology.api.util.IGMathHelper;
import com.igteam.immersivegeology.common.IGContent;
import com.igteam.immersivegeology.common.blocks.IGMaterialBlock;
import com.igteam.immersivegeology.common.world.chunk.data.ChunkData;
import com.igteam.immersivegeology.common.world.chunk.data.ChunkDataProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class IGFallingBlockEntity extends FallingBlockEntity
{
    protected BlockState fallTile = Blocks.SAND.getDefaultState();
    protected boolean dontSetBlock;
    protected double gravity = 9.81;
    protected float drag = 1.05f;

    public IGFallingBlockEntity(World worldIn, double x, double y, double z, BlockState fallingBlockState)
    {
        super(worldIn, x, y, z, fallingBlockState);
    }

    public IGFallingBlockEntity(World worldIn, double x, double y, double z, BlockState fallingBlockState, double gravity)
    {
        super(worldIn, x, y, z, fallingBlockState);
        setGravity(gravity);
    }

    public IGFallingBlockEntity(World worldIn, double x, double y, double z, BlockState fallingBlockState, double gravity, float drag)
    {
        super(worldIn, x, y, z, fallingBlockState);
        setGravity(gravity);
        setDrag(drag);
    }

    public IGFallingBlockEntity(World worldIn, double x, double y, double z, BlockState fallingBlockState, float drag)
    {
        super(worldIn, x, y, z, fallingBlockState);
        setDrag(drag);
    }

    public void setGravity(double gravity)
    {
        this.gravity = gravity;
    }

    public double getGravity()
    {
        return gravity;
    }

    public void setDrag(float drag)
    {
        this.drag = drag;
    }

    public float getDrag()
    {
        return drag;
    }

    @Override
    public void tick()
    {
        //super.tick(); //Old, vanilla falling
        //Mostly the same as vanilla, with a few differences
        if (this.fallTile.isAir())
        {
            this.remove();
        }
        else
        {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            Block block = this.fallTile.getBlock();
            if (this.fallTime++ == 0) {
                BlockPos blockpos = new BlockPos(this);
                if (this.world.getBlockState(blockpos).getBlock() == block) {
                    this.world.removeBlock(blockpos, false);
                } else if (!this.world.isRemote) {
                    this.remove();
                    return;
                }
            }

            if (!this.hasNoGravity()) {
                this.setMotion(this.getMotion().add(0.0D, -(getGravity()/20), 0.0D));
            }
            if (this.getMotion().length() != 0)
            {
                double air_density = IGMathHelper.air_density(IGMathHelper.barometric_air_pressure(this.posY-this.world.getSeaLevel()));
                double x_drag = IGMathHelper.apply_drag(this.getMotion().x, this.drag, 1, air_density);
                double y_drag = IGMathHelper.apply_drag(this.getMotion().y, this.drag, 1, air_density);
                double z_drag = IGMathHelper.apply_drag(this.getMotion().z, this.drag, 1, air_density);

                this.setMotion(this.getMotion().add(x_drag, y_drag, z_drag));
            }

            this.move(MoverType.SELF, this.getMotion());
            if (!this.world.isRemote)
            {
                BlockPos blockpos1 = new BlockPos(this);
                boolean flag = this.fallTile.has(BlockStateProperties.WATERLOGGED);
                boolean flag1 = this.world.getFluidState(blockpos1).isTagged(FluidTags.WATER);
                double d0 = this.getMotion().lengthSquared();
                if (d0 > 1d)
                {
                    BlockRayTraceResult blockraytraceresult = this.world.rayTraceBlocks(new RayTraceContext(new Vec3d(this.prevPosX, this.prevPosY, this.prevPosZ), new Vec3d(this.posX, this.posY, this.posZ), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.SOURCE_ONLY, this));
                    if (blockraytraceresult.getType() != RayTraceResult.Type.MISS && this.world.getFluidState(blockraytraceresult.getPos()).isTagged(FluidTags.WATER))
                    {
                        blockpos1 = blockraytraceresult.getPos();
                        if(flag)
                            flag1 = true;
                    }
                }

                if (!this.onGround && !flag1)
                {
                    if (!this.world.isRemote && (this.fallTime > 100 && (blockpos1.getY() < 1 || blockpos1.getY() > 256) || this.fallTime > 600))
                    {
                        if (this.shouldDropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS))
                        {
                            this.entityDropItem(block);
                        }
                        this.remove();
                    }
                }
                else
                {
                    BlockState blockstate = this.world.getBlockState(blockpos1);
                    if(flag1)
                        this.setMotion(this.getMotion().mul(0.7D, 0.5D, 0.7D));
                    if (!this.dontSetBlock)
                    {
                        boolean flag2 = blockstate.isReplaceable(new DirectionalPlaceContext(this.world, blockpos1, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
                        boolean flag3 = this.fallTile.isValidPosition(this.world, blockpos1);
                        if (flag2 && flag3)
                        {
                            if (this.fallTile.has(BlockStateProperties.WATERLOGGED) && this.world.getFluidState(blockpos1).getFluid() == Fluids.WATER)
                            {
                                this.fallTile = this.fallTile.with(BlockStateProperties.WATERLOGGED, Boolean.valueOf(true));
                            }

                            if (this.world.setBlockState(blockpos1, this.fallTile, 3))
                            {
                                if (block instanceof FallingBlock)
                                {
                                    ((FallingBlock) block).onEndFalling(this.world, blockpos1, this.fallTile, blockstate);
                                }

                                if (this.tileEntityData != null && this.fallTile.hasTileEntity()) {
                                    TileEntity tileentity = this.world.getTileEntity(blockpos1);
                                    if (tileentity != null) {
                                        CompoundNBT compoundnbt = tileentity.write(new CompoundNBT());

                                        for(String s : this.tileEntityData.keySet()) {
                                            INBT inbt = this.tileEntityData.get(s);
                                            if (!"x".equals(s) && !"y".equals(s) && !"z".equals(s)) {
                                                compoundnbt.put(s, inbt.copy());
                                            }
                                        }

                                        tileentity.read(compoundnbt);
                                        tileentity.markDirty();
                                    }
                                    else if (this.shouldDropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS))
                                    {
                                        this.entityDropItem(block);
                                    }
                                }
                                else if (this.shouldDropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS))
                                {
                                    this.entityDropItem(block);
                                }
                            }
                            else if (block instanceof FallingBlock)
                            {
                                ((FallingBlock)block).onBroken(this.world, blockpos1);
                            }
                        }
                    }
                }
            }
        }
    }
}
