package igteam.immersive_geology.common.block.tileentity;

import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.api.utils.DirectionalBlockPos;
import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import com.google.common.collect.ImmutableSet;
import igteam.api.IGApi;
import igteam.immersive_geology.common.multiblocks.CrystallizerMultiblock;
import igteam.api.processing.recipe.CrystalRecipe;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

//Sorry to IE for using their internal classes, we should have used an API, and we'll maybe fix it later.
//I swear's I'll use the API after Alpha release ~Muddykat
public class CrystallizerTileEntity extends PoweredMultiblockTileEntity<CrystallizerTileEntity, CrystalRecipe> implements IEBlockInterfaces.IBlockOverlayText, IEBlockInterfaces.IPlayerInteraction, IBlockBounds, IIEInventory {

    private static final CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES =
            CachedShapesWithTransform.createForMultiblock(CrystallizerTileEntity::getShape);
    public float activeTicks;
    public FluidTank inputFluidTank = new FluidTank(12 * FluidAttributes.BUCKET_VOLUME);
    public BlockPos inputOffset = new BlockPos(0, 1, 0);
    public BlockPos outputOffset = new BlockPos(1, 0, 2);

    public NonNullList<ItemStack> inventory;
   // private final LazyOptional<IItemHandler> extractionHandler;

    public CrystallizerTileEntity() {
        super(CrystallizerMultiblock.INSTANCE, 24000, true, IGTileTypes.CRYSTALLIZER.get());
        activeTicks = 0;
        this.inventory = NonNullList.withSize(1, ItemStack.EMPTY);
        //this.extractionHandler = this.registerConstantCap(new IEInventoryHandler(1, this.master(), 0, false, true));

    }

    private static List<AxisAlignedBB> getShape(BlockPos posInMultiblock) {
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        if (bY == 0) {

            if (bX == 0 && bZ == 0) {
                return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
                        new AxisAlignedBB(0.0625, 0.5, 0.0625, 0.3175, 1.0, 0.3175));
            }

            if (bX == 0 && bZ == 2) {
                return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
                        new AxisAlignedBB(0.0625, 0.5, 0.9375, 0.3175, 1.0, 0.6825));
            }

            if (bX == 2 && bZ == 2) {
                return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
                        new AxisAlignedBB(0.9375, 0.5, 0.9375, 0.6825, 1.0, 0.6825));
            }

            if (bX == 2 && bZ == 0) {
                return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
                        new AxisAlignedBB(0.9375, 0.5, 0.0625, 0.6825, 1.0, 0.3175));
            }
            return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));
        }

        if (bY == 1) {
            if (bX == 0 && bZ == 0) {
                return Arrays.asList(new AxisAlignedBB(0.0625, 0.0, 0.0625, 1, 1.0, 1));
            }

            if (bX == 0 && bZ == 2) {
                return Arrays.asList(
                        new AxisAlignedBB(0.0625, 0.0, 0.9375, 1, 1.0, 0));
            }

            if (bX == 2 && bZ == 2) {
                return Arrays.asList(new AxisAlignedBB(0.9375, 0.0, 0.9375, 0, 1.0, 0));
            }

            if (bX == 2 && bZ == 0) {
                return Arrays.asList(new AxisAlignedBB(0.9375, 0.0, 0.0625, 0, 1.0, 1));
            }
        }
        if (bY == 2) {
            if (bX == 0 && bZ == 0) {
                return Arrays.asList(new AxisAlignedBB(0.0625, 0.0, 0.0625, 1, 0.25, 1));
            }

            if (bX == 0 && bZ == 2) {
                return Arrays.asList(
                        new AxisAlignedBB(0.0625, 0.0, 0.9375, 1, 0.25, 0));
            }

            if (bX == 2 && bZ == 2) {
                return Arrays.asList(new AxisAlignedBB(0.9375, 0.0, 0.9375, 0, 0.25, 0));
            }

            if (bX == 2 && bZ == 0) {
                return Arrays.asList(new AxisAlignedBB(0.9375, 0.0, 0.0625, 0, 0.25, 1));
            }
            if (bX == 1 && bZ == 1) {
                return Arrays.asList(new AxisAlignedBB(0.125, 0.0, 0.125, 0.875, 0.75, 0.875),
                        new AxisAlignedBB(0.25, 0.75, 0.25, 0.75, 1.0, 0.75));
            }
            return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.25, 1.0));

        }

        return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
    }

    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        /*if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (outputOffset.equals(this.posInMultiblock)) {
                return this.extractionHandler.cast();
            }
        }*/
        return super.getCapability(capability, facing);

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
    protected CrystalRecipe getRecipeForId(ResourceLocation resourceLocation) {
        return CrystalRecipe.recipes.get(resourceLocation);
    }

    @Override
    public Set<BlockPos> getEnergyPos() {
        return ImmutableSet.of(
                new BlockPos(1, 2, 1)
        );
    }

    @Override
    public Set<BlockPos> getRedstonePos() {
        return ImmutableSet.of(
                new BlockPos(2, 1, 1)
        );
    }

    @Override
    public IFluidTank[] getInternalTanks() {
        CrystallizerTileEntity master = this.master();

        return new IFluidTank[]{master.inputFluidTank};
    }

    @Override
    public CrystalRecipe findRecipeForInsertion(ItemStack itemStack) {
        return null;
    }

    //We've only output slot, so magic constant
    @Override
    public int[] getOutputSlots() {
        return new int[]{0};
    }

    @Override
    public void tick() {
        super.tick();
        activeTicks++;
        activeTicks = activeTicks % 360;

        if (world.isRemote || isDummy())
            return;
        CrystallizerTileEntity master = this.master();
        boolean update = false;
        if (master.energyStorage.getEnergyStored() > 0 && master.processQueue.size() < master.getProcessQueueMaxLength()) {
            FluidStack fluid = master.inputFluidTank.getFluid();
            IGApi.getNewLogger().warn("Finding Recipe with Fluid: " + fluid.getDisplayName());
            CrystalRecipe recipe = CrystalRecipe.findRecipe(fluid);
            if (recipe != null) {
                MultiblockProcessInMachine<CrystalRecipe> process = new MultiblockProcessInMachine<>(recipe, new int[0])
                        .setInputTanks(0);
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
    public void readCustomNBT(CompoundNBT nbt, boolean descPacket) {
        super.readCustomNBT(nbt, descPacket);
        inputFluidTank.readFromNBT(nbt.getCompound("tank_input"));
        inventory = Utils.readInventory(nbt.getList("inventory", 10), 1);
    }

    @Override
    public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
        super.writeCustomNBT(nbt, descPacket);
        nbt.put("tank_input", inputFluidTank.writeToNBT(new CompoundNBT()));
        nbt.put("inventory", Utils.writeInventory(inventory));
    }

    //We've no such thing
    @Override
    public int[] getOutputTanks() {
        return new int[0];
    }

    @Override
    public boolean additionalCanProcessCheck(MultiblockProcess<CrystalRecipe> multiblockProcess) {
        return true;
    }
    private final CapabilityReference<IItemHandler> output = CapabilityReference.forTileEntityAt(this,
            () -> new DirectionalBlockPos(pos.offset(this.getFacing().getOpposite(), 2)
                    .offset(this.getIsMirrored() ? this.getFacing().rotateY() : this.getFacing().rotateYCCW(), 1)
                    .offset(Direction.UP, 1)
                    , getFacing().getOpposite()),
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

    @Override
    public void doProcessOutput(ItemStack itemStack) {
        itemStack = Utils.insertStackIntoInventory(this.output, itemStack, false);
        if (!itemStack.isEmpty()) {
            Direction fw = this.getFacing().getOpposite();
            Utils.dropStackAtPos(world, new DirectionalBlockPos(pos.offset(fw, 2).
                    offset(this.getIsMirrored() ? fw.rotateYCCW(): fw.rotateY(),1).offset(Direction.UP, 1), fw)
                    .getPosition(), itemStack, fw);
            master().getInventory().get(0).shrink(itemStack.getCount());
        }
    }

    @Override
    public void doProcessFluidOutput(FluidStack fluidStack) {

    }

    @Override
    public void onProcessFinish(MultiblockProcess<CrystalRecipe> multiblockProcess) {
        CrystallizerTileEntity master = this.master();
        master.inputFluidTank.drain( multiblockProcess.recipe.getInputFluid().getAmount(), IFluidHandler.FluidAction.EXECUTE);
        doProcessOutput(multiblockProcess.recipe.getRecipeOutput());
    }

    @Override
    public int getMaxProcessPerTick() {
        return 1;
    }

    @Override
    public int getProcessQueueMaxLength() {
        return 1;
    }

    @Override
    public float getMinProcessDistance(MultiblockProcess<CrystalRecipe> multiblockProcess) {
        return 0;
    }

    @Override
    public boolean isInWorldProcessingMachine() {
        return true;
    }

    @Nonnull
    @Override
    protected IFluidTank[] getAccessibleFluidTanks(Direction direction) {
        CrystallizerTileEntity master = this.master();
        if (master != null) {
            return new IFluidTank[]{master().inputFluidTank};
        }
        return new FluidTank[0];
    }

    @Override
    protected boolean canFillTankFrom(int i, Direction direction, FluidStack fluidStack) {
        if (inputOffset.equals(posInMultiblock)) {
            CrystallizerTileEntity master = this.master();
            return master != null && master.inputFluidTank.getFluidAmount() < master.inputFluidTank.getCapacity();
        }
        return false;
    }

    @Override
    protected boolean canDrainTankFrom(int i, Direction direction) {
        if (outputOffset.equals(posInMultiblock)) {
            CrystallizerTileEntity master = this.master();
            return master != null && master.inputFluidTank.getFluidAmount() < master.inputFluidTank.getCapacity();
        }
        return false;
    }

    @Override
    public NonNullList<ItemStack> getInventory() {
        CrystallizerTileEntity master = this.master();
        if (master != null) {
            return master.inventory;
        }
        return null;
    }

    @Override
    public boolean isStackValid(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public int getSlotLimit(int i) {
        return 64;
    }

    @Override
    public void doGraphicalUpdates() {

    }
}
