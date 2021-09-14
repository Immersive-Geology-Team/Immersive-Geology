package com.igteam.immersive_geology.common.block.tileentity;

import blusunrize.immersiveengineering.api.IEEnums;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import com.google.common.collect.ImmutableSet;
import com.igteam.immersive_geology.common.multiblocks.ChemicalVatMultiblock;
import com.igteam.immersive_geology.core.registration.IGTileTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ChemicalVatTileEntity extends PoweredMultiblockTileEntity<ChemicalVatTileEntity, MultiblockRecipe> implements IBlockBounds {
    /** Template-Location of the Redstone Input Port. (0, 1, 5) */
    public static final Set<BlockPos> Redstone_IN = ImmutableSet.of(new BlockPos(3, 0, 0));

    /** Template-Location of the Energy Input Port. (3, 1, 0) */
    public static final Set<BlockPos> Energy_IN = ImmutableSet.of(new BlockPos(3, 1, 0));

    public static final Set<BlockPos> ITEM_IN = ImmutableSet.of(new BlockPos(1, 3, 1));

    public static final BlockPos FLUID_PRIMARY_IN = new BlockPos(3, 0, 1);
    public static final BlockPos FLUID_SECONDARY_IN = new BlockPos(3, 0, 2);

    /** Template-Location of the Eastern Item Input Port. (1, 0, 0) and also it's the master block */
    public static final BlockPos ITEM_OUT = new BlockPos(1, 0, 0);


    public boolean wasActive = false;
    public float activeTicks = 0;
    public FluidTank fakeTank = new FluidTank(0);
    public ChemicalVatTileEntity(){
        super(ChemicalVatMultiblock.INSTANCE, 16000, true, null);
    }

    @Override
    public TileEntityType<?> getType() {
        return IGTileTypes.VAT.get();
    }

    //used to load stored states.
    @Override
    public void readCustomNBT(CompoundNBT nbt, boolean descPacket) {
        super.readCustomNBT(nbt, descPacket);
    }

    //Used to store states is active? etc.
    @Override
    public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
        super.writeCustomNBT(nbt, descPacket);
    }

    @Override
    public void tick() {
        super.tick();

        if((this.world.isRemote || isDummy()) && this.wasActive) {
            this.activeTicks++;
        }
    }

    @Override
    public Set<BlockPos> getEnergyPos(){
        return Energy_IN;
    }

    @Override
    public IEEnums.IOSideConfig getEnergySideConfig(Direction facing){
        if(this.formed && this.isEnergyPos() && (facing == null || facing == Direction.UP))
            return IEEnums.IOSideConfig.INPUT;

        return IEEnums.IOSideConfig.NONE;
    }

    @Override
    public Set<BlockPos> getRedstonePos(){
        return Redstone_IN;
    }

    @Override
    public boolean isInWorldProcessingMachine(){
        return false;
    }

    @Override
    public void doProcessOutput(ItemStack output){
    }

    @Override
    public void doProcessFluidOutput(FluidStack output){
    }

    @Override
    public void onProcessFinish(MultiblockProcess<MultiblockRecipe> process){
    }

    @Override
    public boolean additionalCanProcessCheck(MultiblockProcess<MultiblockRecipe> process){
        return false;
    }

    @Override
    public float getMinProcessDistance(MultiblockProcess<MultiblockRecipe> process){
        return 0;
    }

    @Override
    public int getMaxProcessPerTick(){
        return 1;
    }

    @Override
    public int getProcessQueueMaxLength(){
        return 1;
    }

    @Override
    public boolean isStackValid(int slot, ItemStack stack){
        return true;
    }

    @Override
    public int getSlotLimit(int slot){
        return 64;
    }

    @Override
    public int[] getOutputSlots(){
        return null;
    }

    @Override
    public int[] getOutputTanks(){
        return new int[]{1};
    }

    @Override
    public void doGraphicalUpdates(int slot){
        this.markDirty();
        this.markContainingBlockForUpdate(null);
    }

    @Override
    public MultiblockRecipe findRecipeForInsertion(ItemStack inserting){
        return null;
    }

    @Override
    protected MultiblockRecipe getRecipeForId(ResourceLocation id){
        return null;
    }

    @Override
    public NonNullList<ItemStack> getInventory(){
        return null;
    }

    @Override
    public IFluidTank[] getInternalTanks(){
        return null;
    }

    @Override
    protected IFluidTank[] getAccessibleFluidTanks(Direction side){
        return new FluidTank[0];
        /*
         ChemicalVatTileEntity master = master();
        if(master != null){
            if(this.posInMultiblock.equals(ITEM_OUT)){ //master block location
                if(side == null || (getIsMirrored() ? (side == getFacing().rotateYCCW()) : (side == getFacing().rotateY()))){
                    return new NonNullList<ItemStack>();
                }
            }
        }
         */


    }



    @Override
    protected boolean canFillTankFrom(int iTank, Direction side, FluidStack resource){
        return false;
    }

    @Override
    protected boolean canDrainTankFrom(int iTank, Direction side){
        return false;
    }

    private static CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES = CachedShapesWithTransform.createForMultiblock(ChemicalVatTileEntity::getShape);

    @Override
    public VoxelShape getBlockBounds(ISelectionContext ctx){
        return SHAPES.get(this.posInMultiblock, Pair.of(getFacing(), getIsMirrored()));
    }

    //Direct Copy from IP's Pumpjack, this will need to be changed.
    private static List<AxisAlignedBB> getShape(BlockPos posInMultiblock){
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        // Most of the arm doesnt need collision. Dumb anyway.
        if((bY == 3 && bX == 1 && bZ != 2) || (bX == 1 && bY == 2 && bZ == 0)){
            return new ArrayList<>();
        }

        // Motor
        if(bY < 3 && bX == 1 && bZ == 4){
            List<AxisAlignedBB> list = new ArrayList<>();
            if(bY == 2){
                list.add(new AxisAlignedBB(0.25, 0.0, 0.0, 0.75, 0.25, 1.0));
            }else{
                list.add(new AxisAlignedBB(0.25, 0.0, 0.0, 0.75, 1.0, 1.0));
            }
            if(bY == 0){
                list.add(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));
            }
            return list;
        }

        // Support
        if(bZ == 2 && bY > 0){
            if(bX == 0){
                if(bY == 1){
                    List<AxisAlignedBB> list = new ArrayList<>();
                    list.add(new AxisAlignedBB(0.6875, 0.0, 0.0, 1.0, 1.0, 0.25));
                    list.add(new AxisAlignedBB(0.6875, 0.0, 0.75, 1.0, 1.0, 1.0));
                    return list;
                }
                if(bY == 2){
                    List<AxisAlignedBB> list = new ArrayList<>();
                    list.add(new AxisAlignedBB(0.8125, 0.0, 0.0, 1.0, 0.5, 1.0));
                    list.add(new AxisAlignedBB(0.8125, 0.5, 0.25, 1.0, 1.0, 0.75));
                    return list;
                }
                if(bY == 3){
                    return Arrays.asList(new AxisAlignedBB(0.9375, 0.0, 0.375, 1.0, 0.125, 0.625));
                }
            }
            if(bX == 1 && bY == 3){
                return Arrays.asList(new AxisAlignedBB(0.0, -0.125, 0.375, 1.0, 0.125, 0.625));
            }
            if(bX == 2){
                if(bY == 1){
                    List<AxisAlignedBB> list = new ArrayList<>();
                    list.add(new AxisAlignedBB(0.0, 0.0, 0.0, 0.3125, 1.0, 0.25));
                    list.add(new AxisAlignedBB(0.0, 0.0, 0.75, 0.3125, 1.0, 1.0));
                    return list;
                }
                if(bY == 2){
                    List<AxisAlignedBB> list = new ArrayList<>();
                    list.add(new AxisAlignedBB(0.0, 0.0, 0.0, 0.1875, 0.5, 1.0));
                    list.add(new AxisAlignedBB(0.0, 0.5, 0.25, 0.1875, 1.0, 0.75));
                    return list;
                }
                if(bY == 3){
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.375, 0.0625, 0.125, 0.625));
                }
            }
        }

        // Redstone Controller
        if(bX == 0 && bZ == 5){
            if(bY == 0){ // Bottom
                return Arrays.asList(
                        new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
                        new AxisAlignedBB(0.75, 0.0, 0.625, 0.875, 1.0, 0.875),
                        new AxisAlignedBB(0.125, 0.0, 0.625, 0.25, 1.0, 0.875)
                );
            }
            if(bY == 1){ // Top
                return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.5, 1.0, 1.0, 1.0));
            }
        }

        // Below the power-in block, base height
        if(bX == 2 && bY == 0 && bZ == 5){
            return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
        }

        // Misc
        if(bY == 0){

            // Legs Bottom Front
            if(bZ == 1 && (bX == 0 || bX == 2)){
                List<AxisAlignedBB> list = new ArrayList<>();

                list.add(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));

                if(bX == 0){
                    list.add(new AxisAlignedBB(0.5, 0.5, 0.5, 1.0, 1.0, 1.0));
                }
                if(bX == 2){
                    list.add(new AxisAlignedBB(0.0, 0.5, 0.5, 0.5, 1.0, 1.0));
                }

                return list;
            }

            // Legs Bottom Back
            if(bZ == 3 && (bX == 0 || bX == 2)){
                List<AxisAlignedBB> list = new ArrayList<>();

                list.add(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));

                if(bX == 0){
                    list.add(new AxisAlignedBB(0.5, 0.5, 0.0, 1.0, 1.0, 0.5));
                }
                if(bX == 2){
                    list.add(new AxisAlignedBB(0.0, 0.5, 0.0, 0.5, 1.0, 0.5));
                }

                return list;
            }

            // Fluid Outputs
            if(bZ == 2 && (bX == 0 || bX == 2)){
                return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
            }

            if(bX == 1){
                // Well
                if(bZ == 0){
                    return Arrays.asList(new AxisAlignedBB(0.3125, 0.5, 0.8125, 0.6875, 0.875, 1.0), new AxisAlignedBB(0.1875, 0, 0.1875, 0.8125, 1.0, 0.8125));
                }

                // Pipes
                if(bZ == 1){
                    return Arrays.asList(
                            new AxisAlignedBB(0.3125, 0.5, 0.0, 0.6875, 0.875, 1.0),
                            new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0)
                    );
                }
                if(bZ == 2){
                    return Arrays.asList(
                            new AxisAlignedBB(0.3125, 0.5, 0.0, 0.6875, 0.875, 0.6875),
                            new AxisAlignedBB(0.0, 0.5, 0.3125, 1.0, 0.875, 0.6875),
                            new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0)
                    );
                }
            }

            return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));
        }

        return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
    }
}
