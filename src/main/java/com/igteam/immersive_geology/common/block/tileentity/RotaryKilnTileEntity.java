package com.igteam.immersive_geology.common.block.tileentity;

import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.IEInventoryHandler;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import com.google.common.collect.ImmutableSet;
import com.igteam.immersive_geology.api.crafting.recipes.recipe.CalcinationRecipe;
import com.igteam.immersive_geology.common.multiblocks.RotaryKilnMultiblock;
import com.igteam.immersive_geology.core.registration.IGTileTypes;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

//Sorry to IE for using their internal classes, we should have used an API, and we'll maybe fix it later.
//I swear's I'll use the API after Alpha release ~Muddykat
public class RotaryKilnTileEntity extends PoweredMultiblockTileEntity<RotaryKilnTileEntity, CalcinationRecipe> implements IEBlockInterfaces.IBlockOverlayText, IEBlockInterfaces.IPlayerInteraction, IBlockBounds, IIEInventory {


    private static final CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES =
            CachedShapesWithTransform.createForMultiblock(RotaryKilnTileEntity::getShape);
    protected final int inputSlot = 0;
    protected final int outputSlot = 1;
    private final LazyOptional<IItemHandler> insertionHandler;
    private final LazyOptional<IItemHandler> extractionHandler;

    public BlockPos inputOffset = new BlockPos(0, 2, 1);
    public BlockPos outputOffset = new BlockPos(7, 0, 1);
    public float activeTicks;
    public NonNullList<ItemStack> inventory;

    public RotaryKilnTileEntity() {
        super(RotaryKilnMultiblock.INSTANCE, 42000, true, IGTileTypes.ROTARYKILN.get());
        activeTicks = 0;
        this.inventory = NonNullList.withSize(2, ItemStack.EMPTY);
        this.insertionHandler = this.registerConstantCap(new IEInventoryHandler(1, this.master(), 0, true, false) {
            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                RotaryKilnTileEntity master = (RotaryKilnTileEntity) master(); //Need to manually tell the inserter to insert to Master tile only
                if (!stack.isEmpty()) {
                    if (!master.isStackValid(slot, stack)) {
                        return stack;
                    } else {
                        int offsetSlot = inputSlot;
                        ItemStack currentStack = (ItemStack) master.getInventory().get(offsetSlot);
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
        this.extractionHandler = this.registerConstantCap(new IEInventoryHandler(1, this.master(), 1, false, true));

    }

    private static List<AxisAlignedBB> getShape(BlockPos posInMultiblock) {
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        if (bY == 0) {
            if (bZ == 0 || bZ == 2) {
                if (bX % 2 == 0) {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));
                }
                if (bX == 7 && bZ == 2) {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
                            new AxisAlignedBB(0.0, 0.5, 0.5, 1.0, 1.0, 0.0));

                }
                if (bX == 7 && bZ == 0) {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
                            new AxisAlignedBB(0.0, 0.5, 0.5, 1.0, 1.0, 1.0));
                }
            }
        }
        if (bY == 2) {
            if (bZ == 1) {
                if (bX == 7) {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));
                }
                if (bX == 2) {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.75, 1.0));
                }
                if (bX == 3) {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.625, 1.0));
                }
                if (bX == 4) {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));
                }
                if (bX == 5) {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.375, 1.0));
                }

            }
            if (bZ == 0 && bX == 7) {
                return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.5, 1.0, 0.5, 1.0));
            }
            if (bZ == 2 && bX == 7) {
                return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 0.5));
            }
        }
        if (bY == 1) {
            if (bX == 7) {
                if (bZ == 0) {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.5, 1.0, 1.0, 1.0));
                }
                if (bZ == 2) {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.5));
                }
            }
            if (bX == 5) {
                if (bZ == 2) {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 1.0, 1.0, 1.0, 0.5));
                }
                if (bZ == 0) {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 0.9375),
                            new AxisAlignedBB(0.0, 0.5, 0.9375, 1.0, 1.0, 0.5));
                }
            }

            if (bX == 3) {
                if (bZ == 0) {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.9375));
                }
                if (bZ == 2) {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0625, 1.0, 0.5, 1.0),
                            new AxisAlignedBB(0.0, 0.5, 0.5, 1.0, 1.0, 0.0625));
                }
            }

            if (bX == 0) {
                return Arrays.asList(new AxisAlignedBB(0.0, 0.5, 0.0, 1.0, 1.0, 1.0),
                        new AxisAlignedBB(0.5, 0.5, 0.0, 1.0, 0.0, 1.0));
            }
        }

        return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
    }

    @Nonnull
    @Override
    public VoxelShape getBlockBounds(ISelectionContext iSelectionContext) {
        return getShape(SHAPES);
    }

    @Override
    public ITextComponent[] getOverlayText(PlayerEntity playerEntity, RayTraceResult rayTraceResult, boolean b) {
        return new ITextComponent[0];
    }

    @Override
    public boolean useNixieFont(PlayerEntity playerEntity, RayTraceResult rayTraceResult) {
        return false;
    }

    @Override
    public boolean interact(Direction direction, PlayerEntity playerEntity, Hand hand, ItemStack itemStack, float v, float v1, float v2) {
        return false;
    }

    @Override
    protected CalcinationRecipe getRecipeForId(ResourceLocation resourceLocation) {
        return CalcinationRecipe.recipes.get(resourceLocation);
    }

    @Override
    public Set<BlockPos> getEnergyPos() {
        return ImmutableSet.of(
                new BlockPos(3, 1, 0)
        );
    }

    @Override
    public Set<BlockPos> getRedstonePos() {
        return ImmutableSet.of(
                new BlockPos(5, 1, 2)
        );
    }

    @Override
    public IFluidTank[] getInternalTanks() {
        return new IFluidTank[0];
    }

    @Override
    public CalcinationRecipe findRecipeForInsertion(ItemStack itemStack) {
        return CalcinationRecipe.findRecipe(itemStack);
    }

    @Override
    public int[] getOutputSlots() {
        return new int[]{outputSlot};
    }

    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            RotaryKilnTileEntity master = (RotaryKilnTileEntity) this.master();
            if (master == null) {
                return LazyOptional.empty();
            }
            if (inputOffset.equals(this.posInMultiblock) && facing == Direction.UP) {
                return this.insertionHandler.cast();
            }
            if (outputOffset.equals(this.posInMultiblock)) {
                return this.extractionHandler.cast();
            }
        }
        return super.getCapability(capability, facing);

    }

    @Override
    public int[] getOutputTanks() {
        return new int[0];
    }

    @Override
    public void tick() {
        activeTicks++;
        activeTicks = activeTicks % 360;

        if (world.isRemote || isDummy())
            return;
        super.tick();

        RotaryKilnTileEntity master = this.master();
        boolean update = false;
        if (master.energyStorage.getEnergyStored() > 0 && master.processQueue.size() < master.getProcessQueueMaxLength()) {
            CalcinationRecipe recipe = CalcinationRecipe.findRecipe(master.inventory.get(inputSlot));
            if (recipe != null) {
                MultiblockProcessInMachine<CalcinationRecipe> process = new MultiblockProcessInMachine<>(recipe, inputSlot);

                if (master.addProcessToQueue(process, true, true)) {

                    update = master.addProcessToQueue(process, false, true);
                }
            }
        }
        if (update) {
            this.markDirty();
            this.markContainingBlockForUpdate(null);
        }
    }

    @Override
    public boolean additionalCanProcessCheck(MultiblockProcess<CalcinationRecipe> multiblockProcess) {
        return this.master().getInventory().get(outputSlot).getCount() < this.getSlotLimit(outputSlot);
    }

    @Override
    public void doProcessOutput(ItemStack itemStack) {

    }

    @Override
    public void doProcessFluidOutput(FluidStack fluidStack) {

    }

    @Override
    public void onProcessFinish(MultiblockProcess<CalcinationRecipe> multiblockProcess) {
        RotaryKilnTileEntity master = (RotaryKilnTileEntity) master();

        if(!multiblockProcess.recipe.getItemInputs().isEmpty()) {
            int shrinkAmount = multiblockProcess.recipe.getItemInputs().get(0).getCount();
            master.getInventory().get(inputSlot).shrink(shrinkAmount);
        }
        doProcessOutput(multiblockProcess.recipe.getRecipeOutput());

    }

    @Override
    public int getMaxProcessPerTick() {
        return 1;
    }

    @Override
    public int getProcessQueueMaxLength() {
        return 2;
    }

    @Override
    public float getMinProcessDistance(MultiblockProcess<CalcinationRecipe> multiblockProcess) {
        return 1;
    }

    @Override
    public boolean isInWorldProcessingMachine() {
        return false;
    }

    @Nonnull
    @Override
    protected IFluidTank[] getAccessibleFluidTanks(Direction direction) {
        return new IFluidTank[0];
    }

    @Override
    protected boolean canFillTankFrom(int i, Direction direction, FluidStack fluidStack) {
        return false;
    }

    @Override
    protected boolean canDrainTankFrom(int i, Direction direction) {
        return false;
    }

    @Override
    public NonNullList<ItemStack> getInventory() {
        RotaryKilnTileEntity master = this.master();
        if (master != null) {
            return master.inventory;
        }
        return null;
    }

    @Override
    public boolean isStackValid(int i, ItemStack itemStack) {
        //fix later
        return true;
    }

    @Override
    public int getSlotLimit(int i) {
        return 64;
    }

    @Override
    public void doGraphicalUpdates() {

    }

    @Override
    public void readCustomNBT(CompoundNBT nbt, boolean descPacket) {
        super.readCustomNBT(nbt, descPacket);
        inventory = Utils.readInventory(nbt.getList("inventory", 10), 2);
    }

    @Override
    public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
        super.writeCustomNBT(nbt, descPacket);
        nbt.put("inventory", Utils.writeInventory(inventory));
    }
}
