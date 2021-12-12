package com.igteam.immersive_geology.common.block.tileentity;

import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartTileEntity;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import com.igteam.immersive_geology.common.multiblocks.ReverberationFurnaceMultiblock;
import com.igteam.immersive_geology.core.registration.IGTileTypes;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

//Sorry to IE for using their internal classes, we should have used an API, and we'll maybe fix it later.
public class ReverberationFurnaceTileEntity extends MultiblockPartTileEntity<ReverberationFurnaceTileEntity> implements IIEInventory, IEBlockInterfaces.IActiveState, IEBlockInterfaces.IInteractionObjectIE, IEBlockInterfaces.IProcessTile, IBlockBounds {
    NonNullList<ItemStack> inventory;
    public int process;
    public int processMax;
    public int burnTime;
    public int lastBurnTime;
    //public final ReverberationFurnaceTileEntity.FurnaceState guiState;
    //private final Supplier<AlloyRecipe> cachedModel;
    private static CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES = CachedShapesWithTransform.createForMultiblock(ReverberationFurnaceTileEntity::getShape);

    public ReverberationFurnaceTileEntity() {
        super(ReverberationFurnaceMultiblock.INSTANCE, IGTileTypes.REV_FURNACE.get(), true);
        this.inventory = NonNullList.withSize(4, ItemStack.EMPTY);
        this.process = 0;
        this.processMax = 0;
        this.burnTime = 0;
        this.lastBurnTime = 0;
        //this.guiState = new ReverberationFurnaceTileEntity.FurnaceState();
//        this.cachedModel = CachedRecipe.cached(AlloyRecipe::findRecipe, () -> {
//            return (ItemStack) this.inventory.get(0);
//        }, () -> {
//            return (ItemStack) this.inventory.get(1);
//        });
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

    public void tick() {
        /*
        this.checkForNeedlessTicking();
        if (!this.world.isRemote && this.formed && !this.isDummy()) {
            boolean activeBeforeTick = this.getIsActive();
            boolean activeAfterTick;
            if (this.burnTime <= 0) {
                if (activeBeforeTick) {
                    this.setActive(false);
                }
            } else {
                activeAfterTick = false;
                AlloyRecipe recipe;
                if (this.process > 0) {
                    if (!((ItemStack) this.inventory.get(0)).isEmpty() && !((ItemStack) this.inventory.get(1)).isEmpty()) {
                        recipe = this.getRecipe();
                        if (recipe != null && recipe.time != this.processMax) {
                            this.processMax = 0;
                            this.process = 0;
                            this.setActive(false);
                        } else {
                            --this.process;
                            activeAfterTick = true;
                            if (!activeBeforeTick) {
                                this.setActive(true);
                            }
                        }
                    } else {
                        this.process = 0;
                        this.processMax = 0;
                    }

                    this.markContainingBlockForUpdate((BlockState) null);
                }

                --this.burnTime;
                if (this.process <= 0) {
                    if (this.processMax > 0) {
                        recipe = this.getRecipe();
                        if (recipe != null) {
                            boolean flip = !recipe.input0.test((ItemStack) this.inventory.get(0));
                            Utils.modifyInvStackSize(this.inventory, flip ? 1 : 0, -recipe.input0.getCount());
                            Utils.modifyInvStackSize(this.inventory, flip ? 0 : 1, -recipe.input1.getCount());
                            if (!((ItemStack) this.inventory.get(3)).isEmpty()) {
                                ((ItemStack) this.inventory.get(3)).grow(recipe.output.copy().getCount());
                            } else {
                                this.inventory.set(3, recipe.output.copy());
                            }
                        }

                        this.processMax = 0;
                    }

                    recipe = this.getRecipe();
                    if (recipe != null) {
                        this.process = recipe.time;
                        if (!activeAfterTick) {
                            --this.process;
                        }

                        this.processMax = recipe.time;
                        this.setActive(true);
                    }
                }
            }

            int x;
            if (this.burnTime <= 10 && this.getRecipe() != null) {
                ItemStack fuel = (ItemStack) this.inventory.get(2);
                x = ForgeHooks.getBurnTime(fuel);
                if (x > 0) {
                    this.lastBurnTime = x;
                    this.burnTime += this.lastBurnTime;
                    Item itemFuel = fuel.getItem();
                    fuel.shrink(1);
                    if (fuel.isEmpty()) {
                        this.inventory.set(2, itemFuel.getContainerItem(fuel));
                    }

                    this.markContainingBlockForUpdate((BlockState) null);
                }
            }

            activeAfterTick = this.getIsActive();
            if (activeBeforeTick != activeAfterTick) {
                this.markDirty();

                for (x = 0; x < 2; ++x) {
                    for (int y = 0; y < 2; ++y) {
                        for (int z = 0; z < 2; ++z) {
                            BlockPos actualPos = this.getBlockPosForPos(new BlockPos(x, y, z));
                            TileEntity te = Utils.getExistingTileEntity(this.world, actualPos);
                            if (te instanceof AlloySmelterTileEntity) {
                                ((AlloySmelterTileEntity) te).setActive(activeAfterTick);
                            }
                        }
                    }
                }
            }
        }
         */
    }

//    @Nullable
//    public AlloyRecipe getRecipe() {
//        AlloyRecipe recipe = (AlloyRecipe) this.cachedModel.get();
//        if (recipe == null) {
//            return null;
//        } else {
//            ItemStack output = (ItemStack) this.inventory.get(3);
//            return !output.isEmpty() && (!ItemStack.areItemsEqual(output, recipe.output) || output.getCount() + recipe.output.getCount() > this.getSlotLimit(3)) ? null : recipe;
//        }
//    }

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

    public int[] getCurrentProcessesStep() {
        ReverberationFurnaceTileEntity master = (ReverberationFurnaceTileEntity) this.master();
        return master != this && master != null ? master.getCurrentProcessesStep() : new int[]{this.processMax - this.process};
    }

    public int[] getCurrentProcessesMax() {
        ReverberationFurnaceTileEntity master = (ReverberationFurnaceTileEntity) this.master();
        return master != this && master != null ? master.getCurrentProcessesMax() : new int[]{this.processMax};
    }

    public boolean receiveClientEvent(int id, int arg) {
        if (id == 0) {
            this.formed = arg == 1;
        }

        this.markDirty();
        this.markContainingBlockForUpdate((BlockState) null);
        return true;
    }

    public void readCustomNBT(CompoundNBT nbt, boolean descPacket) {
        super.readCustomNBT(nbt, descPacket);
        if (!descPacket) {
            ItemStackHelper.loadAllItems(nbt, this.inventory);
            this.process = nbt.getInt("process");
            this.processMax = nbt.getInt("processMax");
            this.burnTime = nbt.getInt("burnTime");
            this.lastBurnTime = nbt.getInt("lastBurnTime");
        }

    }

    @Override
    public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
        super.writeCustomNBT(nbt, descPacket);
        if (!descPacket) {
            nbt.putInt("process", this.process);
            nbt.putInt("processMax", this.processMax);
            nbt.putInt("burnTime", this.burnTime);
            nbt.putInt("lastBurnTime", this.lastBurnTime);
            ItemStackHelper.saveAllItems(nbt, this.inventory);
        }
    }

    public NonNullList<ItemStack> getInventory() {
        return this.inventory;
    }

    public boolean isStackValid(int slot, ItemStack stack) {
        return slot == 0 || slot == 1 && FurnaceTileEntity.isFuel(stack);
    }

    public int getSlotLimit(int slot) {
        return 64;
    }

    public void doGraphicalUpdates() {
    }

    protected IFluidTank[] getAccessibleFluidTanks(Direction side) {
        return new FluidTank[0];
    }

    protected boolean canFillTankFrom(int iTank, Direction side, FluidStack resources) {
        return false;
    }

    protected boolean canDrainTankFrom(int iTank, Direction side) {
        return false;
    }

    private static int getBurnTime(ItemStack stack) {
        return stack.isEmpty() ? 0 : ForgeHooks.getBurnTime(stack);
    }

    public class FurnaceState implements IIntArray {
        public static final int LAST_BURN_TIME = 0;
        public static final int BURN_TIME = 1;
        public static final int PROCESS_MAX = 2;
        public static final int CURRENT_PROCESS = 3;

        public FurnaceState() {
        }

        public int getLastBurnTime() {
            return this.get(0);
        }

        public int getBurnTime() {
            return this.get(1);
        }

        public int getMaxProcess() {
            return this.get(2);
        }

        public int getProcess() {
            return this.get(3);
        }

        public int get(int index) {
            switch (index) {
                case 0:
                    return ReverberationFurnaceTileEntity.this.lastBurnTime;
                case 1:
                    return ReverberationFurnaceTileEntity.this.burnTime;
                case 2:
                    return ReverberationFurnaceTileEntity.this.processMax;
                case 3:
                    return ReverberationFurnaceTileEntity.this.process;
                default:
                    throw new IllegalArgumentException("Unknown index " + index);
            }
        }

        public void set(int index, int value) {
            switch (index) {
                case 0:
                    ReverberationFurnaceTileEntity.this.lastBurnTime = value;
                    break;
                case 1:
                    ReverberationFurnaceTileEntity.this.burnTime = value;
                    break;
                case 2:
                    ReverberationFurnaceTileEntity.this.processMax = value;
                    break;
                case 3:
                    ReverberationFurnaceTileEntity.this.process = value;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown index " + index);
            }

        }

        public int size() {
            return 4;
        }
    }
}