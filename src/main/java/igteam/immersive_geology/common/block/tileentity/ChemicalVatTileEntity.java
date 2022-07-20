package igteam.immersive_geology.common.block.tileentity;

import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.api.utils.DirectionalBlockPos;
import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.IEInventoryHandler;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import com.google.common.collect.ImmutableSet;
import igteam.immersive_geology.ImmersiveGeology;
import igteam.immersive_geology.common.multiblocks.ChemicalVatMultiblock;
import igteam.api.processing.recipe.VatRecipe;
import igteam.immersive_geology.core.registration.IGTileTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

//Sorry to IE for using their internal classes, we should have used an API, and we'll maybe fix it later.
public class ChemicalVatTileEntity extends PoweredMultiblockTileEntity<ChemicalVatTileEntity, VatRecipe> implements IEBlockInterfaces.IBlockOverlayText, IEBlockInterfaces.IPlayerInteraction, IBlockBounds, IIEInventory {

    Logger log = ImmersiveGeology.getNewLogger();
    public FluidTank[] tanks = new FluidTank[]{
            new FluidTank(12* FluidAttributes.BUCKET_VOLUME),
            new FluidTank(12* FluidAttributes.BUCKET_VOLUME),
            new FluidTank(24* FluidAttributes.BUCKET_VOLUME)
    };

    public NonNullList<ItemStack> inventory;
    private LazyOptional<IItemHandler> insertionHandler;
    private LazyOptional<IItemHandler> extractionHandler;

    protected final int inputSlot = 0;
    protected final int outputSlot = 1;
    public float activeTicks;
    public ItemStack holdItem;

    private static final BlockPos outputOffset = new BlockPos(1, 0, 2);
    private static final BlockPos outputItemOffset = new BlockPos(0, 0, 1);

    private static final Set<BlockPos> inputPrimary = ImmutableSet.of(
            new BlockPos(3, 0, 0)
    );

    private static final Set<BlockPos> inputSecondary = ImmutableSet.of(
            new BlockPos(3, 0, 1)
    );
    public ChemicalVatTileEntity(){
        super(ChemicalVatMultiblock.INSTANCE, 16000, true, IGTileTypes.VAT.get());
        this.inventory = NonNullList.withSize(2, ItemStack.EMPTY);
        holdItem = ItemStack.EMPTY;
        activeTicks = 0;
        this.extractionHandler = this.registerConstantCap(new IEInventoryHandler(1, this.master(), 1, false, true));
        this.insertionHandler = this.registerConstantCap(new IEInventoryHandler(1, this.master(), 0, true, false){
            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                ChemicalVatTileEntity master = (ChemicalVatTileEntity) master(); //Need to manually tell the inserter to insert to Master tile only
                if (!stack.isEmpty()) {
                    if (!master.isStackValid(slot, stack)) {
                        return stack;
                    } else {
                        int offsetSlot = inputSlot;
                        ItemStack currentStack = (ItemStack)master.getInventory().get(offsetSlot);
                        int accepted;
                        if (currentStack.isEmpty()) {
                            accepted = Math.min(stack.getMaxStackSize(), master.getSlotLimit(offsetSlot));
                            if (accepted < stack.getCount()) {
                                stack = stack.copy();
                                if (!simulate) {
                                    master.getInventory().set(offsetSlot, stack.split(accepted));
                                    master.doGraphicalUpdates();
                                } else {
                                    stack.shrink(accepted);
                                }

                                return stack;
                            } else {
                                if (!simulate) {
                                    master.getInventory().set(offsetSlot, stack.copy());
                                    master.doGraphicalUpdates();
                                }

                                return ItemStack.EMPTY;
                            }
                        } else if (!ItemHandlerHelper.canItemStacksStack(stack, currentStack)) {
                            return stack;
                        } else {
                            accepted = Math.min(stack.getMaxStackSize(), master.getSlotLimit(offsetSlot)) - currentStack.getCount();
                            ItemStack newStack;
                            if (accepted < stack.getCount()) {
                                stack = stack.copy();
                                if (!simulate) {
                                    newStack = stack.split(accepted);
                                    newStack.grow(currentStack.getCount());
                                    master.getInventory().set(offsetSlot, newStack);
                                    master.doGraphicalUpdates();
                                } else {
                                    stack.shrink(accepted);
                                }

                                return stack;
                            } else {
                                if (!simulate) {
                                    newStack = stack.copy();
                                    newStack.grow(currentStack.getCount());
                                    master.getInventory().set(offsetSlot, newStack);
                                    master.doGraphicalUpdates();
                                }

                                return ItemStack.EMPTY;
                            }
                        }
                    }
                } else {
                    return stack;
                }
            }
        });
    }

    @Override
    public boolean isEnergyPos() {
        return getEnergyPos().contains(this.posInMultiblock);
    }

    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {

            if (outputItemOffset.equals(this.posInMultiblock)) {
                return this.extractionHandler.cast();
            }
            else {
                ChemicalVatTileEntity master = (ChemicalVatTileEntity) this.master();
                if (master == null) {
                    return LazyOptional.empty();
                }

                return this.insertionHandler.cast();
            }
        }
        return super.getCapability(capability, facing);
    }


    public float getActiveTicks() {
        ChemicalVatTileEntity master = this.master();
        return master.activeTicks;
    }

    @Override
    public void readCustomNBT(CompoundNBT nbt, boolean descPacket)
    {
        super.readCustomNBT(nbt, descPacket);
        tanks[0].readFromNBT(nbt.getCompound("tank_input_1"));
        tanks[1].readFromNBT(nbt.getCompound("tank_input_2"));
        tanks[2].readFromNBT(nbt.getCompound("tank_output"));
        inventory = Utils.readInventory(nbt.getList("inventory", 10), 2);
    }

    @Override
    public void writeCustomNBT(CompoundNBT nbt, boolean descPacket)
    {
        super.writeCustomNBT(nbt, descPacket);
        nbt.put("tank_input_1", tanks[0].writeToNBT(new CompoundNBT()));
        nbt.put("tank_input_2", tanks[1].writeToNBT(new CompoundNBT()));
        nbt.put("tank_output", tanks[2].writeToNBT(new CompoundNBT()));
        nbt.put("inventory", Utils.writeInventory(inventory));
    }

    @Override
    public void tick()
    {

        activeTicks++;
        activeTicks = activeTicks % 360;

        if(world.isRemote||isDummy())
            return;
        super.tick();

        ChemicalVatTileEntity master = (ChemicalVatTileEntity) this.master();
        boolean update = false;
        if(master.energyStorage.getEnergyStored() > 0 && master.processQueue.size() < master.getProcessQueueMaxLength())
        {
            if(master.tanks[0].getFluidAmount() > 0 || master.tanks[1].getFluidAmount() > 0)
            {
                ItemStack inputStack = master.getInventory().get(inputSlot); //Input Item
                VatRecipe recipe = VatRecipe.findRecipe(inputStack, master.tanks[0].getFluid(), master.tanks[1].getFluid());
                if(recipe!=null)
                {
                    MultiblockProcessInMachine<VatRecipe> process = new MultiblockProcessInMachine<>(recipe, inputSlot)
                            .setInputTanks(master.tanks[1].getFluidAmount() == 0 ? new int[]{0} : (master.tanks[0].getFluidAmount() == 0 ? new int[]{1} : new int[]{0,1}));
                    if(master.addProcessToQueue(process, true, true))
                    {
                        master.addProcessToQueue(process, false, true);
                        update = true;
                    }
                }
            }
        }

        if (master.tanks[2].getFluidAmount() > 0) {
            FluidStack out = Utils.copyFluidStackWithAmount(master.tanks[2].getFluid(), Math.min(master.tanks[2].getFluidAmount(), 80), false);
            Direction fw = this.getFacing().getOpposite();
            Direction shift_1 =  this.getIsMirrored() ?  this.getFacing().rotateY() : this.getFacing().rotateYCCW();
            BlockPos outputPos = master.getPos().offset(shift_1, 2).offset(fw, 3);
            update |= (Boolean) FluidUtil.getFluidHandler(this.world, outputPos,fw.getOpposite()).map((output) -> {
                int accepted = output.fill(out, IFluidHandler.FluidAction.SIMULATE);
                if (accepted > 0) {
                    int drained = output.fill(Utils.copyFluidStackWithAmount(out, Math.min(out.getAmount(), accepted), false),
                            IFluidHandler.FluidAction.EXECUTE);
                    master.tanks[2].drain(drained, IFluidHandler.FluidAction.EXECUTE);
                    return true;
                } else {
                    return false;
                }
            }).orElse(false);
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

    @Override
    public Set<BlockPos> getEnergyPos()
    {
        return ImmutableSet.of(
                new BlockPos(3, 1, 2)
        );
    }

    @Override
    public Set<BlockPos> getRedstonePos()
    {
        return ImmutableSet.of(
                new BlockPos(3, 1, 2)
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
        ChemicalVatTileEntity master = (ChemicalVatTileEntity) master();
        return master.tanks[2].getFluidAmount() < master.tanks[2].getCapacity();
    }

    //TODO set the position of this to the correct output area.
    private CapabilityReference<IItemHandler> output = CapabilityReference.forTileEntityAt(this,
            () ->new DirectionalBlockPos(pos.offset(this.getIsMirrored() ? this.getFacing().rotateY() : this.getFacing().rotateYCCW(), 4)
                    .offset(this.getFacing().getOpposite(), 1), getFacing()),
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

    @Override
    public void doProcessOutput(ItemStack output)
    {
        output = Utils.insertStackIntoInventory(this.output, output, false);
        if(!output.isEmpty()) {
            Direction fw = this.getFacing().getOpposite();
            Direction shift_1 = this.getIsMirrored() ? this.getFacing().rotateYCCW() : this.getFacing().rotateY();

            Utils.dropStackAtPos(world, new DirectionalBlockPos(pos.offset(shift_1.getOpposite(), 4).offset(fw, 1),
                   fw).getPosition(), output, fw.getOpposite());
            master().getInventory().get(outputSlot).shrink(output.getCount());
        }
    }

    @Override
    public void doProcessFluidOutput(FluidStack output)
    {

    }

    @Override
    public void onProcessFinish(@Nonnull MultiblockProcess<VatRecipe> process)
    {
        ChemicalVatTileEntity master = (ChemicalVatTileEntity) master();
        //TODO take into account reverse recipes

        if(!process.recipe.getItemInputs().isEmpty()) {
            int shrinkAmount = process.recipe.getItemInputs().get(0).getCount();
            master.getInventory().get(inputSlot).shrink(shrinkAmount);
        }

        int primaryDrainAmount = process.recipe.getInputFluids().get(0).getAmount();
        master.tanks[0].drain(primaryDrainAmount, IFluidHandler.FluidAction.EXECUTE);

        if(process.recipe.getInputFluids().size() == 2) {
            int secondaryDrainAmount = process.recipe.getInputFluids().get(1).getAmount();
            master.tanks[1].drain(secondaryDrainAmount, IFluidHandler.FluidAction.EXECUTE);
        }

        doProcessOutput(process.recipe.getRecipeOutput());
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
        ChemicalVatTileEntity master = this.master();
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
        ChemicalVatTileEntity master = this.master();
        return master.tanks;
    }


    @Override
    protected IFluidTank[] getAccessibleFluidTanks(Direction side)
    {
        ChemicalVatTileEntity master = this.master();
        if(master!=null)
        {
            if(outputOffset.equals(posInMultiblock)&&(side==null||side==getFacing().getOpposite()))
                return new FluidTank[]{master.tanks[2]};
            if(inputPrimary.contains(posInMultiblock)&&(side==null||side.getAxis()==getFacing().rotateYCCW().getAxis()))
                return new FluidTank[]{master.tanks[0]};
            if(inputSecondary.contains(posInMultiblock)&&(side==null||side.getAxis()==getFacing().rotateYCCW().getAxis()))
                return new FluidTank[]{master.tanks[1]};
        }
        return new FluidTank[0];
    }

    @Override
    protected boolean canFillTankFrom(int iTank, Direction side, FluidStack resource)
    {
        if(inputPrimary.contains(posInMultiblock)&&(side==null||side.getAxis()==getFacing().rotateYCCW().getAxis()))
        {
            ChemicalVatTileEntity master = this.master();
            if(master == null || (master.tanks[0].getFluidAmount() >= master.tanks[0].getCapacity()))
                return false;
            return true;
        }

        if(inputSecondary.contains(posInMultiblock)&&(side==null||side.getAxis()==getFacing().rotateYCCW().getAxis()))
        {
            ChemicalVatTileEntity master = this.master();
            if(master == null || (master.tanks[1].getFluidAmount() >= master.tanks[1].getCapacity()))
                return false;
            return true;
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
        VatRecipe primeSec = VatRecipe.findRecipe(inserting, tanks[0].getFluid(), tanks[1].getFluid());
        return primeSec;
    }

    @Override
    public int[] getOutputSlots() {
        return new int[]{outputSlot};
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
            if (bY == 1)
            {
                return Arrays.asList(new AxisAlignedBB(0.1875, 0.0, 0.0, 1.0, 1.0, 1.0));
            }
        }
        return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
    }

    public ITextComponent[] getOverlayText(PlayerEntity player, RayTraceResult mop, boolean hammer) {
        if (Utils.isFluidRelatedItemStack(player.getHeldItem(Hand.MAIN_HAND))) {
            ChemicalVatTileEntity master = (ChemicalVatTileEntity)this.master();
            if(master != null) {
                FluidStack fs1 = master != null ? master.tanks[0].getFluid() : this.tanks[0].getFluid();
                FluidStack fs2 = master != null ? master.tanks[1].getFluid() : this.tanks[1].getFluid();
                FluidStack fs3 = master != null ? master.tanks[2].getFluid() : this.tanks[2].getFluid();
                StringTextComponent primary = new StringTextComponent("Primary: " + fs1.getDisplayName().getString());
                StringTextComponent secondary = new StringTextComponent("Secondary: " + fs2.getDisplayName().getString());
                StringTextComponent output = new StringTextComponent("Output: " + fs3.getDisplayName().getString());

                ItemStack input = ItemStack.EMPTY;
                ItemStack outputItemStack = ItemStack.EMPTY;

                if (master.getInventory() != null) {
                    input = master.getInventory().get(inputSlot);
                    outputItemStack = master.getInventory().get(outputSlot);
                }
                StringTextComponent inputItem = new StringTextComponent("Input Item: " + input.getDisplayName().getString());
                StringTextComponent outputItem = new StringTextComponent("Output Item: " + outputItemStack.getDisplayName().getString());
                return new ITextComponent[]{primary, secondary, output, inputItem, outputItem};
            }
            return null;
        } else {
            return null;
        }
    }

    @Override
    public boolean useNixieFont(PlayerEntity playerEntity, RayTraceResult rayTraceResult) {
        return false;
    }

    public boolean interact(Direction side, PlayerEntity player, Hand hand, ItemStack heldItem, float hitX, float hitY, float hitZ) {
        return false;
    }

    public boolean shouldStir() {
        return energyStorage.getEnergyStored() > 0 && processQueue.size() > 0 && hasEnergyToProcess();
    }

    private boolean hasEnergyToProcess() {
        return energyStorage.getEnergyStored() >= 256;
    }

}
