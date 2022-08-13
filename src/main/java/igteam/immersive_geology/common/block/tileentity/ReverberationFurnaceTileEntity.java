package igteam.immersive_geology.common.block.tileentity;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import blusunrize.immersiveengineering.common.blocks.metal.ClocheTileEntity;
import blusunrize.immersiveengineering.common.network.MessageTileSync;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.IEInventoryHandler;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import com.google.common.collect.ImmutableSet;
import igteam.immersive_geology.ImmersiveGeology;
import igteam.immersive_geology.common.block.helpers.CapabilityReverberationProgress;
import igteam.immersive_geology.common.block.helpers.IProgress;
import igteam.immersive_geology.common.block.helpers.RevProgressHandler;
import igteam.immersive_geology.common.multiblocks.ReverberationFurnaceMultiblock;
import igteam.immersive_geology.core.registration.IGTileTypes;
import igteam.api.materials.GasEnum;
import igteam.api.processing.recipe.ReverberationRecipe;
import igteam.api.materials.pattern.FluidPattern;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

//Sorry to IE for using their internal classes, we should have used an API, and we'll maybe fix it later.
public class ReverberationFurnaceTileEntity extends PoweredMultiblockTileEntity<ReverberationFurnaceTileEntity, ReverberationRecipe> implements IIEInventory, IEBlockInterfaces.IActiveState, IEBlockInterfaces.IInteractionObjectIE, IEBlockInterfaces.IProcessTile, IBlockBounds {
    private static final Set<BlockPos> redStonePos = ImmutableSet.of(
            new BlockPos(1, 0, 0)
    );
    private static final ImmutableSet<BlockPos> gasOutputs = ImmutableSet.of(new BlockPos(4, 11, 1), new BlockPos(4,11,4));

    public static HashMap<Item, Integer> fuelMap = new HashMap<>();
    private static CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES = CachedShapesWithTransform.createForMultiblock(ReverberationFurnaceTileEntity::getShape);
    public int FUEL_SLOT1 = 0, FUEL_SLOT2 = 1;
    public int OUTPUT_SLOT1 = 2, OUTPUT_SLOT2 = 3;
    public int INPUT_SLOT1 = 4, INPUT_SLOT2 = 5;
    public final FluidTank gasTank;

    protected NonNullList<ItemStack> inventory;
    private Logger log = ImmersiveGeology.getNewLogger();
    private int[] burntime= new int[2];
    private int maxBurntime = 100;
    private final LazyOptional<IFluidHandler> tankCap;
    private LazyOptional<IItemHandler> insertionHandler1,insertionHandler2;
    private final LazyOptional<IItemHandler> extractionHandler1, extractionHandler2;

    private static final BlockPos input1Pos = new BlockPos(1, 1, 1);
    private static final BlockPos input2Pos = new BlockPos(1, 1, 4);

    private static final BlockPos output1Pos = new BlockPos(0, 0, 1);
    private static final BlockPos output2Pos = new BlockPos(0, 0, 4);

    public ReverberationFurnaceTileEntity() {
        super(ReverberationFurnaceMultiblock.INSTANCE, 0, true, IGTileTypes.REV_FURNACE.get());
        this.inventory = NonNullList.withSize(6, ItemStack.EMPTY);
        burntime[0] = 0;
        burntime[1] = 0;
        gasTank = new FluidTank(1000) {
            protected void onContentsChanged() {
                ReverberationFurnaceTileEntity.this.sendSyncPacket(2);
            }
        };

        this.tankCap = LazyOptional.of(() -> gasTank);

        recipe_1_progress = 0;
        recipe_1_time = 100;
        recipe_1 = null;

        recipe_2_progress = 0;
        recipe_2_time = 100;
        recipe_2 = null;

        this.insertionHandler1 = this.registerConstantCap(new IEInventoryHandler(1, this.master(), INPUT_SLOT1, true, false){
            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
            {
                return insertionHandlerImpl(slot+4, stack, simulate);
            }
        });

        this.insertionHandler2 = this.registerConstantCap(new IEInventoryHandler(1, this.master(),INPUT_SLOT2, true, false){
            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
            {
                return insertionHandlerImpl(slot+5, stack, simulate);
            }
        });

        this.extractionHandler1 =  this.registerConstantCap(new IEInventoryHandler(1, this.master(), OUTPUT_SLOT1, false, true));
        this.extractionHandler2 =  this.registerConstantCap(new IEInventoryHandler(1, this.master(), OUTPUT_SLOT2, false, true));

    }
  public ItemStack insertionHandlerImpl(int slot, ItemStack stack, boolean simulate) {
        ReverberationFurnaceTileEntity master = (ReverberationFurnaceTileEntity) master(); //Need to manually tell the inserter to insert to Master tile only
        if (!stack.isEmpty()) {
            if (!master.isStackValid(slot, stack)) {
                return stack;
            } else {
                int offsetSlot = slot;
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

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && gasOutputs.contains(posInMultiblock) && facing == Direction.UP) {
            ReverberationFurnaceTileEntity master = master();
            if(master != null)
                return master.tankCap.cast();

            return LazyOptional.empty();
        }
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            ReverberationFurnaceTileEntity master = (ReverberationFurnaceTileEntity) this.master();
            if (master == null) {
                return LazyOptional.empty();
            }

            if (output1Pos.equals(this.posInMultiblock))
            {
                return master.extractionHandler1.cast();
            }

            if (output2Pos.equals(this.posInMultiblock))
            {
                return master.extractionHandler2.cast();
            }

            if (input1Pos.equals(this.posInMultiblock)) {
                return master.insertionHandler1.cast();
            }
            if (input2Pos.equals(this.posInMultiblock)) {
                return master.insertionHandler2.cast();
            }

        }
        if(capability == CapabilityReverberationProgress.ReverberationProgress){
            return progress.cast();
        }
        return super.getCapability(capability, facing);
    }

    private static List<AxisAlignedBB> getShape(BlockPos posInMultiblock) {
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();
        if (bX < 3) {
            if (bY == 2) {
                if (bZ == 0) {
                    if (bX == 0) {
                        return Arrays.asList(new AxisAlignedBB(0.125, 0.0, 0.25, 1.0, 0.5, 1.0));
                    }
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.25, 1.0, 0.5, 1.0));
                }
                if (bZ == 2 || bZ == 3) {
                    if (bX == 0) {
                        return Arrays.asList(new AxisAlignedBB(0.125, 0.0, 0.0, 1.0, 0.5, 1.0));
                    }
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));

                }
                if (bZ == 5) {
                    if (bX == 0) {
                        return Arrays.asList(new AxisAlignedBB(0.125, 0.0, 0.0, 1.0, 0.5, 0.75));
                    }
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 0.75));

                }
            }
            if (bY < 2) {
                if (bZ == 0) {
                    if (bX == 0) {
                        return Arrays.asList(new AxisAlignedBB(0.125, 0.0, 0.25, 1.0, 1.0, 1.0));
                    }
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.25, 1.0, 1.0, 1.0));

                }
                if (bZ == 5) {
                    if (bX == 0) {
                        return Arrays.asList(new AxisAlignedBB(0.125, 0.0, 0.0, 1.0, 1.0, 0.75));
                    }
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.75));
                }

                if (bX == 0) {
                    if (bY == 0 && (bZ == 1 || bZ == 4)) {
                        return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
                    }
                    return Arrays.asList(new AxisAlignedBB(0.125, 0.0, 0.0, 1.0, 1.0, 1.0));
                }

            }
        }
        if (bY >= 6 && bY < 10) {
            if (bX == 3) {
                return Arrays.asList(new AxisAlignedBB(0.5, 0.0, 0.0, 1.0, 1.0, 1.0));
            }
            if (bX == 5) {
                return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 0.5, 1.0, 1.0));
            }
            if (bX == 4) {
                if (bZ == 5 || bZ == 2) {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.5));
                }
                if (bZ == 0 || bZ == 3) {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.5, 1.0, 1.0, 1.0));

                }
            }
        }
        return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
    }

    public boolean canUseGui(PlayerEntity player) {
        return this.formed;
    }

    public IEBlockInterfaces.IInteractionObjectIE getGuiMaster() {
        return (IEBlockInterfaces.IInteractionObjectIE) this.master();
    }

    @Override
    public VoxelShape getBlockBounds(ISelectionContext ctx) {
        return SHAPES.get(this.posInMultiblock, Pair.of(getFacing(), getIsMirrored()));
    }

    @Nullable
    @Override
    protected ReverberationRecipe getRecipeForId(ResourceLocation id) {
        return ReverberationRecipe.recipes.get(id);
    }

    @Override
    public Set<BlockPos> getEnergyPos() {
        return null;
    }

    @Override
    public Set<BlockPos> getRedstonePos() {
        return redStonePos;
    }
    private int recipe_1_progress;
    private ReverberationRecipe recipe_1;

    private int recipe_2_progress;

    private int recipe_2_time;
    private int recipe_1_time;


    private LazyOptional<IProgress> progress = LazyOptional.of(this::createProgress);

    private IProgress createProgress () {
        return new RevProgressHandler(0, 0);
    }

    private ReverberationRecipe recipe_2;
    @Override
    public void tick() {
        ReverberationFurnaceTileEntity master = (ReverberationFurnaceTileEntity) this.master();
        assert master != null;
        assert master.getInventory() != null;
        assert world != null;

        checkForNeedlessTicking();

        if (world.isRemote || isDummy())
            return;

        super.tick();
        boolean update = false;

        if (recipe_1 == null && master.hasRecipe(INPUT_SLOT1)) {
            recipe_1 = master.getRecipe(INPUT_SLOT1);
            recipe_1_time = recipe_1.getTime();
            update = true;
        }

        if (recipe_2 == null && master.hasRecipe(INPUT_SLOT2)) {
            recipe_2 = master.getRecipe(INPUT_SLOT2);
            recipe_2_time = recipe_2.getTime();
            update = true;
        }

        if(recipe_1 != null){
            ItemStack output_1 = recipe_1.getRecipeOutput().copy();
            int recipeTime = recipe_1.getTime();
            float wasteMult = recipe_1.getWasteMultipler();

            if(recipe_1_progress >= recipeTime){
                int old_count = master.getInventory().get(OUTPUT_SLOT1).getCount();
                output_1.grow(old_count);
                master.getInventory().set(OUTPUT_SLOT1, output_1);

                if (master.gasTank.getFluidAmount() < master.gasTank.getCapacity()) {
                    master.gasTank.fill(new FluidStack(GasEnum.SulphurDioxide.getFluid(FluidPattern.gas), Math.round(50 * wasteMult)), IFluidHandler.FluidAction.EXECUTE);
                }

                master.getInventory().get(INPUT_SLOT1).shrink(recipe_1.getInput().getCount());
                recipe_1_progress = 0;
                progress.ifPresent(p -> ((RevProgressHandler)p).setLeftProgress(0));

                update = true;
            } else {
                if(isBurning(FUEL_SLOT1)) {
                    recipe_1_progress++;
                    float r1_prog = ((float) recipe_1_progress) / ((float) recipe_1_time) * 100;
                    progress.ifPresent(p -> ((RevProgressHandler)p).setLeftProgress(r1_prog));
                    burntime[0]--;
                } else {
                    if(hasFuel(FUEL_SLOT1)){
                        burntime[0] = fuelMap.get(master.getInventory().get(FUEL_SLOT1).getItem()); //refuel
                        master().getInventory().get(FUEL_SLOT1).shrink(1);
                    } else {
                        if(recipe_1_progress > 0){
                            recipe_1_progress--;
                            float r1_prog = ((float) recipe_1_progress) / ((float) recipe_1_time) * 100;
                            progress.ifPresent(p -> ((RevProgressHandler)p).setLeftProgress(r1_prog));
                        }
                    }
                }
            }


            if(master.getInventory().get(INPUT_SLOT1).isEmpty()){
                update = true;
                recipe_1 = null;
                recipe_1_progress = 0;
                progress.ifPresent(p -> ((RevProgressHandler)p).setLeftProgress(0));
            }
        }

        if(recipe_2 != null){
            ItemStack output_2 = recipe_2.getRecipeOutput().copy();
            int recipeTime = recipe_2.getTime();
            float wasteMult = recipe_2.getWasteMultipler();

            if(recipe_2_progress >= recipeTime){
                int old_count = master.getInventory().get(OUTPUT_SLOT2).getCount();
                output_2.grow(old_count);
                master.getInventory().set(OUTPUT_SLOT2, output_2);
                if (master.gasTank.getFluidAmount() < master.gasTank.getCapacity()) {
                    master.gasTank.fill(new FluidStack(GasEnum.SulphurDioxide.getFluid(FluidPattern.gas), Math.round(50 * wasteMult)), IFluidHandler.FluidAction.EXECUTE);
                }
                master.getInventory().get(INPUT_SLOT2).shrink(recipe_2.getInput().getCount());
                recipe_2_progress = 0;
                float r2_prog = ((float) recipe_2_progress) / ((float) recipe_2_time) * 100;
                progress.ifPresent(p -> ((RevProgressHandler)p).setRightProgress(r2_prog));

                update = true;
            } else {
                if(isBurning(FUEL_SLOT2)) {
                    recipe_2_progress++;
                    float r2_prog = ((float) recipe_2_progress) / ((float) recipe_2_time) * 100;
                    progress.ifPresent(p -> ((RevProgressHandler)p).setRightProgress(r2_prog));
                    burntime[1]--;
                } else {
                    if(hasFuel(FUEL_SLOT2)){
                        burntime[1] = fuelMap.get(master.getInventory().get(FUEL_SLOT2).getItem()); //refuel
                        master().getInventory().get(FUEL_SLOT2).shrink(1);
                    } else {
                        if(recipe_2_progress > 0){
                            recipe_2_progress--;
                            float r2_prog = ((float) recipe_2_progress) / ((float) recipe_2_time);
                            progress.ifPresent(p -> ((RevProgressHandler)p).setRightProgress(r2_prog));
                        }
                    }
                }
            }

            if(master.getInventory().get(INPUT_SLOT2).isEmpty()){
                recipe_2 = null;
                recipe_2_progress = 0;
                progress.ifPresent(p -> ((RevProgressHandler)p).setRightProgress(0));
                update = true;
            }
        }

        if (master.gasTank.getFluidAmount() > 0 && gasOutputs.contains(posInMultiblock)) {
            FluidStack out = Utils.copyFluidStackWithAmount(master.gasTank.getFluid(), Math.min(master.gasTank.getFluidAmount(), 80), false);
            Direction fw = Direction.UP;
            BlockPos outputPos2 = new BlockPos(1, 11, 4);
            update |= FluidUtil.getFluidHandler(this.world, outputPos2, fw).map((output) -> {
                int accepted = output.fill(out, IFluidHandler.FluidAction.SIMULATE);

                if (accepted > 0) {
                    int drained = output.fill(Utils.copyFluidStackWithAmount(out, Math.min(out.getAmount(), accepted), false),
                            IFluidHandler.FluidAction.EXECUTE);
                    master.gasTank.drain(drained, IFluidHandler.FluidAction.EXECUTE);
                    return true;
                } else {
                    return false;
                }
            }).orElse(false);
        }

        if (master.gasTank.getFluidAmount() > 0 && gasOutputs.contains(posInMultiblock)) {
            FluidStack out = Utils.copyFluidStackWithAmount(master.gasTank.getFluid(), Math.min(master.gasTank.getFluidAmount(), 80), false);
            Direction fw = Direction.UP;
            BlockPos outputPos1 = new BlockPos(4, 11, 4);
            update |= FluidUtil.getFluidHandler(this.world, outputPos1, fw).map((output) -> {
                int accepted = output.fill(out, IFluidHandler.FluidAction.SIMULATE);

                if (accepted > 0) {
                    int drained = output.fill(Utils.copyFluidStackWithAmount(out, Math.min(out.getAmount(), accepted), false),
                            IFluidHandler.FluidAction.EXECUTE);
                    master.gasTank.drain(drained, IFluidHandler.FluidAction.EXECUTE);
                    return true;
                } else {
                    return false;
                }
            }).orElse(false);
        }

        if (update) {
            this.markDirty();
            this.markContainingBlockForUpdate(null);
        }
    }

    public float getLeftProgress() {
        return this.getCapability(CapabilityReverberationProgress.ReverberationProgress).map(IProgress::getLeftProgress).orElse(0f);
    }

    public float getRightProgress() {
        return this.getCapability(CapabilityReverberationProgress.ReverberationProgress).map(IProgress::getRightProgress).orElse(0f);
    }

    public boolean hasRecipe(int input_slot) {
        return getRecipe(input_slot) != null;
    }

    private ReverberationRecipe getRecipe(int input_slot) {
        ItemStack inputItem = inventory.get(input_slot);
        return ReverberationRecipe.findRecipe(inputItem);
    }

    @Override
    public void readCustomNBT(CompoundNBT nbt, boolean descPacket) {
        super.readCustomNBT(nbt, descPacket);
        burntime[0] = nbt.getInt("burntime_1");
        burntime[1] = nbt.getInt("burntime_2");
        gasTank.readFromNBT(nbt.getCompound("tank"));

        if(!descPacket) {
            inventory = Utils.readInventory(nbt.getList("inventory", 10), 6);
        }
    }

    @Override
    public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
        super.writeCustomNBT(nbt, descPacket);
        nbt.putInt("burntime_1", burntime[0]);
        nbt.putInt("burntime_2", burntime[1]);
        CompoundNBT tankTag = this.gasTank.writeToNBT(new CompoundNBT());
        nbt.put("tank", tankTag);

        if(!descPacket){
            nbt.put("inventory", Utils.writeInventory(inventory));
        }

    }

    @Nullable
    @Override
    public IFluidTank[] getInternalTanks() {
        return new IFluidTank[]{gasTank};
    }

    @Nullable
    @Override
    public ReverberationRecipe findRecipeForInsertion(ItemStack itemStack) {
        return ReverberationRecipe.findRecipe(itemStack);
    }

    @Nullable
    @Override
    public int[] getOutputSlots() {
        return new int[]{OUTPUT_SLOT1, OUTPUT_SLOT2};
    }

    @Nullable
    @Override
    public int[] getOutputTanks() {
        return new int[]{0};
    }

    @Override
    public boolean additionalCanProcessCheck(MultiblockProcess multiblockProcess) {
        if (multiblockProcess.recipe instanceof ReverberationRecipe) {
            ReverberationRecipe r = (ReverberationRecipe) multiblockProcess.recipe;
            //WTF? -> (processQueue.get(r.getSlotOffset()).recipe.getId().equals(multiblockProcess.recipe.getId()) &&
            return isBurning(r.getSlotOffset());
        }
        return false;
    }

    @Override
    public void doProcessOutput(ItemStack itemStack) {

    }

    @Override
    public void doProcessFluidOutput(FluidStack fluidStack) {

    }

    @Override
    public void onProcessFinish(MultiblockProcess multiblockProcess) {

    }

    @Override
    public int getMaxProcessPerTick() {
        return 2;
    }

    @Override
    public int getProcessQueueMaxLength() {
        return 0;
    }

    @Override
    public float getMinProcessDistance(MultiblockProcess multiblockProcess) {
        return 0;
    }

    @Override

    public void remove() {
        super.remove();
        progress.invalidate();
        insertionHandler1.invalidate();
        insertionHandler2.invalidate();
        tankCap.invalidate();
    }
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
    }
    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        read(pkt.getNbtCompound());
    }

    @SuppressWarnings("unchecked")
    private void read(CompoundNBT nbtCompound) {
        progress.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(nbtCompound));
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        CompoundNBT progressTag = nbt.getCompound("progress");
        progress.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(progressTag));
        return super.write(nbt);
    }

    @Override
    public boolean isInWorldProcessingMachine() {
        return true;
    }

    public boolean isBurning(int slot) {
        ReverberationFurnaceTileEntity master = (ReverberationFurnaceTileEntity) this.master();
        assert master != null;
        if(master.burntime.length > slot) {
            return master.burntime[slot] > 0;
        } else {
            return false;
        }
    }

    public boolean hasFuel(int slot) {
        ReverberationFurnaceTileEntity master = (ReverberationFurnaceTileEntity) this.master();
        assert master != null;
        return fuelMap.containsKey(master.inventory.get(slot).getItem());
    }

    @Nonnull
    @Override
    protected IFluidTank[] getAccessibleFluidTanks(Direction direction) {
        if(gasOutputs.contains(posInMultiblock) && (direction == null || direction == Direction.UP)) {
            return new IFluidTank[]{ gasTank };
        }

        return new FluidTank[0];
    }



    @Override
    protected boolean canFillTankFrom(int i, Direction direction, FluidStack fluidStack) {
        return false;
    }

    @Override
    protected boolean canDrainTankFrom(int i, Direction side) {
        return gasOutputs.contains(posInMultiblock) && (side == null || side == Direction.UP);
    }

    @Nonnull
    @Override
    public int[] getCurrentProcessesStep() {
        return new int[0];
    }

    @Nonnull
    @Override
    public int[] getCurrentProcessesMax() {
        return new int[0];
    }

    @Nullable
    @Override
    public NonNullList<ItemStack> getInventory() {
        return inventory;
    }

    @Override
    public boolean isStackValid(int i, ItemStack itemStack) {
        if (i == INPUT_SLOT1 || i == INPUT_SLOT2)
        {
            return (ReverberationRecipe.findRecipe(itemStack) != null);
        }
        if (i == FUEL_SLOT1 || i == FUEL_SLOT2)
        {
            return fuelMap.containsKey(itemStack.getItem());

        }
        return false;
    }

    @Override
    public int getSlotLimit(int i) {
        return 64;
    }

    @Override
    public void doGraphicalUpdates() {

    }

    @Nonnull
    @Override
    public TileEntityType<?> getType() {
        return IGTileTypes.REV_FURNACE.get();
    }

    protected void sendSyncPacket(int type) {
        CompoundNBT nbt = new CompoundNBT();
        if (type == 0) {
            nbt.putInt("energy", this.energyStorage.getEnergyStored());
        } else if (type == 2) {
            nbt.put("tank", this.gasTank.writeToNBT(new CompoundNBT()));
        }

        ImmersiveEngineering.packetHandler.send(PacketDistributor.TRACKING_CHUNK.with(() -> {
            return this.world.getChunkAt(this.pos);
        }), new MessageTileSync(this, nbt));
    }

    public void receiveMessageFromServer(CompoundNBT message) {
        if (message.contains("energy", 3)) {
            this.energyStorage.setEnergy(message.getInt("energy"));
        }

        if (message.contains("tank", 10)) {
            this.gasTank.readFromNBT(message.getCompound("tank"));
        }
    }
}