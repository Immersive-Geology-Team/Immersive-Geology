package com.igteam.immersive_geology.common.block.tileentity;

import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import com.google.common.collect.ImmutableSet;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.multiblocks.HydroJetCutterMultiblock;
import com.igteam.immersive_geology.core.registration.IGTileTypes;
import igteam.immersive_geology.processing.recipe.HydrojetRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

//HydroJetCutterTileEntity - Used in IGTileTypes when linking it to it's BlockReference
// This tile entity is dependent on the HydroJetRecipe which is defined in
public class HydroJetCutterTileEntity extends PoweredMultiblockTileEntity<HydroJetCutterTileEntity, HydrojetRecipe> implements IEBlockInterfaces.IPlayerInteraction, IBlockBounds, IIEInventory {


    //Used In IGTileType - Dependent on HydroJetCutterMultiblock and HydroJetRecipe
    Logger log = ImmersiveGeology.getNewLogger();

    public FluidTank[] tanks = new FluidTank[]{
            new FluidTank(12* FluidAttributes.BUCKET_VOLUME)
    };

    public NonNullList<ItemStack> inventory;
    private LazyOptional<IItemHandler> insertionHandler;
    private LazyOptional<IItemHandler> extractionHandler;

    public float progress = 0;

    public HydroJetCutterTileEntity(){
        super(HydroJetCutterMultiblock.INSTANCE, 2000, true, IGTileTypes.HYDROJET.get());
    }

    @Override
    public boolean isEnergyPos() {
        return getEnergyPos().contains(this.posInMultiblock);
    }

    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        return super.getCapability(capability, facing);
    }

    @Override
    public void readCustomNBT(CompoundNBT nbt, boolean descPacket)
    {
        super.readCustomNBT(nbt, descPacket);
        progress = nbt.getFloat("progress");
    }

    @Override
    public void writeCustomNBT(CompoundNBT nbt, boolean descPacket)
    {
        super.writeCustomNBT(nbt, descPacket);
        nbt.putFloat("progress", progress);
    }

    @Nullable
    @Override
    protected HydrojetRecipe getRecipeForId(ResourceLocation resourceLocation) {
        return null;
    }
    @Override
    public void tick()
    {

    }

    public float getProgress() {
        return progress;
    }

    private static final CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES =
            CachedShapesWithTransform.createForMultiblock(HydroJetCutterTileEntity::getShape);

    @Override
    public VoxelShape getBlockBounds(@Nullable ISelectionContext ctx)
    {
        return getShape(SHAPES);
    }

    @Override
    public Set<BlockPos> getEnergyPos()
    {
        return ImmutableSet.of(
                new BlockPos(0, 1, 2)
        );
    }

    @Override
    public Set<BlockPos> getRedstonePos()
    {
        return ImmutableSet.of(
                new BlockPos(1, 0, 1)
        );
    }

    @Override
    public boolean isInWorldProcessingMachine()
    {
        return true;
    }

    @Override
    public boolean additionalCanProcessCheck(MultiblockProcess<HydrojetRecipe> process)
    {
        return false;
    }

    @Override
    public void doProcessOutput(ItemStack output)
    {

    }

    @Override
    public void doProcessFluidOutput(FluidStack output)
    {

    }

    @Override
    public void onProcessFinish(MultiblockProcess<HydrojetRecipe> multiblockProcess) {

    }

    @Override
    public int getMaxProcessPerTick()
    {
        return 1;
    }

    @Override
    public int getProcessQueueMaxLength()
    {
        return 1;
    }

    @Override
    public float getMinProcessDistance(MultiblockProcess<HydrojetRecipe> multiblockProcess) {
        return 0;
    }

    @Override
    public NonNullList<ItemStack> getInventory()
    {
        HydroJetCutterTileEntity master = this.master();
        if(master != null){
            return master.inventory;
        }
        return null;
    }

    @Override
    public boolean isStackValid(int slot, ItemStack stack)
    {
        return true;
    }

    @Override
    public int getSlotLimit(int i) {
        return 8;
    }

    @Override
    public IFluidTank[] getInternalTanks() {
        HydroJetCutterTileEntity master = this.master();
        return master.tanks;
    }

    @Nullable
    @Override
    public HydrojetRecipe findRecipeForInsertion(ItemStack itemStack) {
        return null;
    }


    @Override
    protected IFluidTank[] getAccessibleFluidTanks(Direction side)
    {
        return new FluidTank[0];
    }

    @Override
    protected boolean canFillTankFrom(int iTank, Direction side, FluidStack resource)
    {
        return false;
    }

    @Override
    protected boolean canDrainTankFrom(int iTank, Direction side)
    {
        return false;
    }

   @Override
    public void doGraphicalUpdates()
    {
        this.markDirty();
        this.markContainingBlockForUpdate(null);
    }

    @Override
    public int[] getOutputSlots() {
        return new int[]{0};
    }

    @Override
    public int[] getOutputTanks() {
        return new int[]{0};
    }

    private static List<AxisAlignedBB> getShape(BlockPos posInMultiblock){
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        if (bX == 1 && bZ != 1)
        {
            if (bY == 1)
            {
                return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.125, 1.0));
            }
        }
        if (bX == 0 && bZ ==0){
            if (bY == 1)
            {
                return Arrays.asList(new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9325, 1.0, 0.9325));
            }
            if (bY == 0)
            {
                return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
                        new AxisAlignedBB(0.0625, 0.5, 0.0625, 0.9325, 1.0, 0.9325)
                        );
            }
        }
        if (bX == 0 && bZ == 1 && bY == 0)
        {
            return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));
        }
        if (bX == 1 && bZ == 1)
        {
            return Arrays.asList(
                    new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.125, 1.0),
                    new AxisAlignedBB(0.0, 0.0, 0.25, 1.0, 1.0, 0.75)
            );

        }
        return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
    }

    @Override
    public boolean interact(Direction direction, PlayerEntity playerEntity, Hand hand, ItemStack itemStack, float v, float v1, float v2) {
        return false;
    }
}
