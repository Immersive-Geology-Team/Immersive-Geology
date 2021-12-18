package com.igteam.immersive_geology.common.block.tileentity;

import blusunrize.immersiveengineering.api.IEEnums;
import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.client.IModelOffsetProvider;
import blusunrize.immersiveengineering.api.energy.immersiveflux.FluxStorage;
import blusunrize.immersiveengineering.client.models.IOBJModelCallback;
import blusunrize.immersiveengineering.common.blocks.IEBaseTileEntity;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.metal.ClocheTileEntity;
import blusunrize.immersiveengineering.common.config.IEServerConfig;
import blusunrize.immersiveengineering.common.util.EnergyHelper;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.core.registration.IGMultiblockRegistrationHolder;
import com.igteam.immersive_geology.core.registration.IGTileTypes;
import javafx.scene.effect.Bloom;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.Property;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

import javax.annotation.Nonnull;

/**
 * Another Apology to Immersive Engineering, Using their internal Classes isn't a good thing to do... But it does make some things easier.
 */
public class BloomeryTileEntity extends IEBaseTileEntity implements ITickableTileEntity, IEBlockInterfaces.IStateBasedDirectional, IEBlockInterfaces.IBlockBounds, IEBlockInterfaces.IHasDummyBlocks,
        IIEInventory, EnergyHelper.IIEInternalFluxHandler, IEBlockInterfaces.IInteractionObjectIE, IOBJModelCallback<BlockState>, IModelOffsetProvider {

    public int dummy = 0;
    private NonNullList<ItemStack> inventory = NonNullList.withSize(4, ItemStack.EMPTY);

    public BloomeryTileEntity() {
        super(IGTileTypes.BLOOMERY.get());
    }

    @Override
    public void readCustomNBT(CompoundNBT compoundNBT, boolean b) {

    }

    @Override
    public void writeCustomNBT(CompoundNBT compoundNBT, boolean b) {

    }

    @Override
    public BlockPos getModelOffset(BlockState blockState, Vector3i vector3i) {
        return new BlockPos(0, dummy, 0);
    }

    @Nonnull
    @Override
    public VoxelShape getBlockBounds(ISelectionContext iSelectionContext) {
        return VoxelShapes.fullCube();
    }

    @Override
    public Property<Direction> getFacingProperty() {
        return IEProperties.FACING_HORIZONTAL;
    }

    @Override
    public PlacementLimitation getFacingLimitation() {
        return PlacementLimitation.HORIZONTAL;
    }

    @Override
    public void placeDummies(BlockItemUseContext ctx, BlockState state)
    {
        ImmersiveGeology.getNewLogger().warn("Placed Dummies Called");
        state = state.with(IEProperties.MULTIBLOCKSLAVE, true);
        for(int i = 1; i <= 1; i++)
        {
            ImmersiveGeology.getNewLogger().warn("Placed Called");
            world.setBlockState(pos.up(i), state);
            ((BloomeryTileEntity)world.getTileEntity(pos.up(i))).dummy = i;
            ((BloomeryTileEntity)world.getTileEntity(pos.up(i))).setFacing(getFacing());
        }
    }

    @Override
    public void setFacing(Direction facing) {
        BlockPos lowest = pos.down(dummy);
        for(int i = 0; i < 3; ++i)
        {
            BlockPos pos = lowest.up(i);
            BlockState state = getWorldNonnull().getBlockState(pos);
            if(state.getBlock()== IGMultiblockRegistrationHolder.Multiblock.bloomery)
                getWorldNonnull().setBlockState(pos, state.with(getFacingProperty(), facing));
        }
    }

    @Override
    public void breakDummies(BlockPos blockPos, BlockState blockState) {
        tempMasterTE = master();
        for(int i = 0; i <= 1; i++)
        {
            BlockPos p = getPos().down(dummy).up(i);
            if(world.getTileEntity(p) instanceof BloomeryTileEntity)
                world.removeBlock(p, false);
        }
    }

    @Override
    public IEBlockInterfaces.IGeneralMultiblock master() {
        if(!isDummy())
            return this;
        if(tempMasterTE instanceof BloomeryTileEntity)
            return (BloomeryTileEntity) tempMasterTE;
        BlockPos masterPos = getPos().down(dummy);
        TileEntity te = Utils.getExistingTileEntity(world, masterPos);
        return te instanceof BloomeryTileEntity ? (BloomeryTileEntity) te : null;
    }

    @Override
    public boolean isDummy()
    {
        return dummy!=0;
    }

    @Override
    public IEBlockInterfaces.IInteractionObjectIE getGuiMaster() {
        return null;
    }

    @Override
    public boolean canUseGui(PlayerEntity playerEntity) {
        return false;
    }

    public FluxStorage energyStorage = new FluxStorage(0, 0);

    @Nonnull
    @Override
    public FluxStorage getFluxStorage() {
        if(dummy!=0)
        {
            TileEntity te = world.getTileEntity(getPos().down(dummy));
            if(te instanceof ClocheTileEntity)
                return ((ClocheTileEntity)te).energyStorage;
        }
        return this.energyStorage;
    }

    @Nonnull
    @Override
    public IEEnums.IOSideConfig getEnergySideConfig(Direction facing) {
        return facing==null||(dummy==0&&facing.getAxis()==this.getFacing().rotateY().getAxis())||(dummy==1&&facing==Direction.UP)? IEEnums.IOSideConfig.INPUT: IEEnums.IOSideConfig.NONE;
    }

    @Override
    public EnergyHelper.IEForgeEnergyWrapper getCapabilityWrapper(Direction direction) {
        return null;
    }

    @Override
    public NonNullList<ItemStack> getInventory() {
        return inventory;
    }

    @Override
    public boolean isStackValid(int i, ItemStack itemStack) {
        return false;
    }

    @Override
    public int getSlotLimit(int i) {
        return 0;
    }

    @Override
    public void doGraphicalUpdates() {

    }

    AxisAlignedBB renderBB;

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        if(renderBB==null)
            renderBB = new AxisAlignedBB(0, 0, 0, 1, 2, 1).offset(pos);
        return renderBB;
    }

    @Override
    public void tick() {

    }

    @Override
    public boolean canHammerRotate(Direction side, Vector3d hit, LivingEntity entity)
    {
        return true;
    }

    @Override
    public boolean canRotate(Direction axis)
    {
        return true;
    }
}
