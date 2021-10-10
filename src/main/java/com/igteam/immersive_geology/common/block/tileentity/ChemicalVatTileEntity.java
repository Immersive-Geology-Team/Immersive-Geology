package com.igteam.immersive_geology.common.block.tileentity;

import blusunrize.immersiveengineering.api.IEEnums;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.RefineryRecipe;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.api.utils.DirectionalBlockPos;
import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import blusunrize.immersiveengineering.common.blocks.metal.RefineryTileEntity;
import blusunrize.immersiveengineering.common.util.Utils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.igteam.immersive_geology.api.crafting.recipes.recipe.VatRecipe;
import com.igteam.immersive_geology.common.multiblocks.ChemicalVatMultiblock;
import com.igteam.immersive_geology.core.registration.IGTileTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.*;

public class ChemicalVatTileEntity extends PoweredMultiblockTileEntity<ChemicalVatTileEntity, VatRecipe> implements IBlockBounds {

    public static final int OUTPUT_EMPTY = 4;
    public static final int OUTPUT_FILLED = 5;
    public FluidTank[] tanks = new FluidTank[]{
            new FluidTank(12* FluidAttributes.BUCKET_VOLUME),
            new FluidTank(12* FluidAttributes.BUCKET_VOLUME),
            new FluidTank(24* FluidAttributes.BUCKET_VOLUME)
    };
    public NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);

    public ChemicalVatTileEntity(){
        super(ChemicalVatMultiblock.INSTANCE, 16000, true, IGTileTypes.VAT.get());
    }

    @Override
    public void readCustomNBT(CompoundNBT nbt, boolean descPacket)
    {
        super.readCustomNBT(nbt, descPacket);
        tanks[0].readFromNBT(nbt.getCompound("tank_input_1"));
        tanks[1].readFromNBT(nbt.getCompound("tank_input_2"));
        tanks[2].readFromNBT(nbt.getCompound("tank_output"));
        if(!descPacket)
            inventory = Utils.readInventory(nbt.getList("inventory", 10), 6);
    }

    @Override
    public void writeCustomNBT(CompoundNBT nbt, boolean descPacket)
    {
        super.writeCustomNBT(nbt, descPacket);
        nbt.put("tank_input_1", tanks[0].writeToNBT(new CompoundNBT()));
        nbt.put("tank_input_2", tanks[1].writeToNBT(new CompoundNBT()));
        nbt.put("tank_output", tanks[2].writeToNBT(new CompoundNBT()));
        if(!descPacket)
            nbt.put("inventory", Utils.writeInventory(inventory));
    }

    @Override
    public void tick()
    {
        super.tick();
        if(world.isRemote||isDummy())
            return;

        boolean update = false;
        if(energyStorage.getEnergyStored() > 0&&processQueue.size() < this.getProcessQueueMaxLength())
        {
            if(tanks[0].getFluidAmount() > 0||tanks[1].getFluidAmount() > 0)
            {
                VatRecipe recipe = VatRecipe.findRecipe(inventory.get(0), tanks[0].getFluid(), tanks[1].getFluid());
                if(recipe!=null)
                {
                    MultiblockProcessInMachine<VatRecipe> process = new MultiblockProcessInMachine<>(recipe)
                            .setInputTanks((tanks[0].getFluidAmount() > 0&&tanks[1].getFluidAmount() > 0)?new int[]{0, 1}: tanks[0].getFluidAmount() > 0?new int[]{0}: new int[]{1});
                    if(this.addProcessToQueue(process, true))
                    {
                        this.addProcessToQueue(process, false);
                        update = true;
                    }
                }
            }
        }

        if(update)
        {
            this.markDirty();
            this.markContainingBlockForUpdate(null);
        }
    }

    private static final CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES =
            CachedShapesWithTransform.createForMultiblock(ChemicalVatTileEntity::getShape);

    @Override
    public VoxelShape getBlockBounds(@Nullable ISelectionContext ctx)
    {
        return getShape(SHAPES);
    }
/*
    private static List<AxisAlignedBB> getShape(BlockPos posInMultiblock)
    {
        if(posInMultiblock.getZ()%2==0&&posInMultiblock.getY()==0&&posInMultiblock.getX()%4==0)
        {
            List<AxisAlignedBB> list = Utils.flipBoxes(posInMultiblock.getZ()==0, posInMultiblock.getX()==0,
                    new AxisAlignedBB(0, 0, 0, 1, .5f, 1),
                    new AxisAlignedBB(0.25, .5f, 0, 0.5, 1.375f, 0.25)
            );
            if(new BlockPos(4, 0, 2).equals(posInMultiblock))
            {
                list.add(new AxisAlignedBB(0.125, .5f, 0.625, 0.25, 1, 0.875));
                list.add(new AxisAlignedBB(0.75, .5f, 0.625, 0.875, 1, 0.875));
            }
            return list;
        }
        if(posInMultiblock.getZ()%2==0&&posInMultiblock.getY()==0&&posInMultiblock.getX()%2==1)
            return Utils.flipBoxes(posInMultiblock.getZ()==0, posInMultiblock.getX()==1,
                    new AxisAlignedBB(0, 0, 0, 1, .5f, 1),
                    new AxisAlignedBB(0, .5f, 0, 0.25, 1.375f, 0.25)
            );

        if(posInMultiblock.getZ() < 2&&posInMultiblock.getY() > 0&&posInMultiblock.getX()%4==0)
        {
            float minZ = -.25f;
            float maxZ = 1.25f;
            float minY = posInMultiblock.getY()==1?.5f: -.5f;
            float maxY = posInMultiblock.getY()==1?2f: 1f;
            if(posInMultiblock.getZ()==0)
            {
                minZ += 1;
                maxZ += 1;
            }
            return Utils.flipBoxes(false, posInMultiblock.getX()==4,
                    new AxisAlignedBB(0.5, minY, minZ, 2, maxY, maxZ)
            );
        }
        if(posInMultiblock.getZ() < 2&&posInMultiblock.getY() > 0&&posInMultiblock.getX()%2==1)
        {
            float minZ = -.25f;
            float maxZ = 1.25f;
            float minY = posInMultiblock.getY()==1?.5f: -.5f;
            float maxY = posInMultiblock.getY()==1?2f: 1f;
            if(posInMultiblock.getZ()==0)
            {
                minZ += 1;
                maxZ += 1;
            }
            return Utils.flipBoxes(false, posInMultiblock.getX()==3,
                    new AxisAlignedBB(-0.5, minY, minZ, 1, maxY, maxZ)
            );
        }
        else if(ImmutableSet.of(
                new BlockPos(0, 0, 2),
                new BlockPos(1, 0, 2),
                new BlockPos(3, 0, 2)
        ).contains(posInMultiblock))
            return ImmutableList.of(new AxisAlignedBB(0, 0, 0, 1, .5f, 1));
        else if(new BlockPos(4, 1, 2).equals(posInMultiblock))
            return ImmutableList.of(new AxisAlignedBB(0, 0, 0.5, 1, 1, 1));
        else if(new BlockPos(2, 1, 2).equals(posInMultiblock))
            return ImmutableList.of(new AxisAlignedBB(.0625f, 0, .0625f, .9375f, 1, .9375f));
        else
            return ImmutableList.of(new AxisAlignedBB(0, 0, 0, 1, 1, 1));
    }
*/
    @Override
    public Set<BlockPos> getEnergyPos()
    {
        return ImmutableSet.of(
                new BlockPos(3, 1, 0)
        );
    }

    @Override
    public Set<BlockPos> getRedstonePos()
    {
        return ImmutableSet.of(
                new BlockPos(3, 1, 0)
        );
    }

    @Override
    public boolean isInWorldProcessingMachine()
    {
        return true;
    }

    @Override
    public boolean additionalCanProcessCheck(MultiblockProcess<VatRecipe> process)
    {
        return true;
    }

    private CapabilityReference<IItemHandler> output = CapabilityReference.forTileEntityAt(this,
            () -> new DirectionalBlockPos(getPos().add(1, 0, 0).offset(getFacing(), -2), getFacing()),
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

    @Override
    public void doProcessOutput(ItemStack output)
    {
        output = Utils.insertStackIntoInventory(this.output, output, false);
        if(!output.isEmpty())
            Utils.dropStackAtPos(world, getPos().add(1, 0, 0).offset(getFacing(), -2), output, getFacing().getOpposite());
    }

    @Override
    public void doProcessFluidOutput(FluidStack output)
    {
    }

    @Override
    public void onProcessFinish(MultiblockProcess<VatRecipe> process)
    {

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
    public float getMinProcessDistance(MultiblockProcess<VatRecipe> process)
    {
        return 0;
    }


    @Override
    public NonNullList<ItemStack> getInventory()
    {
        return inventory;
    }

    @Override
    public boolean isStackValid(int slot, ItemStack stack)
    {
        return true;
    }

    @Override
    public int getSlotLimit(int i) {
        return 0;
    }

    @Override
    public IFluidTank[] getInternalTanks() {
        return tanks;
    }

    private static final BlockPos outputOffset = new BlockPos(1, 0, 0);
    private static final Set<BlockPos> inputOffsets = ImmutableSet.of(
            new BlockPos(3, 0, 1),
            new BlockPos(3, 0, 2)
    );

    @Override
    protected IFluidTank[] getAccessibleFluidTanks(Direction side)
    {
        ChemicalVatTileEntity master = this.master();
        if(master!=null)
        {
            if(outputOffset.equals(posInMultiblock)&&(side==null||side==getFacing().getOpposite()))
                return new FluidTank[]{master.tanks[2]};
            if(inputOffsets.contains(posInMultiblock)&&(side==null||side.getAxis()==getFacing().rotateYCCW().getAxis()))
                return new FluidTank[]{master.tanks[0], master.tanks[1]};
        }
        return new FluidTank[0];
    }

    @Override
    protected boolean canFillTankFrom(int iTank, Direction side, FluidStack resource)
    {
        if(inputOffsets.contains(posInMultiblock)&&(side==null||side.getAxis()==getFacing().rotateYCCW().getAxis()))
        {
            ChemicalVatTileEntity master = this.master();
            if(master==null||master.tanks[iTank].getFluidAmount() >= master.tanks[iTank].getCapacity())
                return false;
            if(master.tanks[0].getFluid().isEmpty()&&master.tanks[1].getFluid().isEmpty())
            {
                Optional<VatRecipe> incompleteRecipes = VatRecipe.findIncompleteVatRecipe(resource, FluidStack.EMPTY);
                return incompleteRecipes.isPresent();
            }
            else
            {
                FluidStack otherFluid = master.tanks[iTank==0?1: 0].getFluid();
                Optional<VatRecipe> incompleteRecipes = VatRecipe.findIncompleteVatRecipe(resource, otherFluid);
                return incompleteRecipes.isPresent();
            }
        }
        return false;
    }

    @Override
    protected boolean canDrainTankFrom(int iTank, Direction side)
    {
        return outputOffset.equals(posInMultiblock)&&(side==null||side==getFacing().getOpposite());
    }

   @Override
    public void doGraphicalUpdates()
    {
        this.markDirty();
        this.markContainingBlockForUpdate(null);
    }

    @Override
    public VatRecipe findRecipeForInsertion(ItemStack inserting) {
        return null;
    }

    @Override
    public int[] getOutputSlots() {
        return new int[0];
    }

    @Override
    public int[] getOutputTanks() {
        return new int[]{2};
    }

    @Override
    protected VatRecipe getRecipeForId(ResourceLocation id) {
        return VatRecipe.recipes.get(id);
    }

    //Direct Copy from IP's Pumpjack, this will need to be changed.
    private static List<AxisAlignedBB> getShape(BlockPos posInMultiblock){
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        //Empty space
        if (bX == 0 && bZ == 0)
        {
            if (bY == 2 || bY == 3)
            {
                return new ArrayList<>();
            }
            if (bY == 1)
            {
                return Arrays.asList(new AxisAlignedBB(0.1875, 0.0, 0.0, 1.0, 1.0, 1.0));
            }
        }
        return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
    }
}
