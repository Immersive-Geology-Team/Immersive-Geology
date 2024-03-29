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
import igteam.api.processing.recipe.HydrojetRecipe;
import igteam.immersive_geology.ImmersiveGeology;
import igteam.immersive_geology.common.multiblocks.HydroJetCutterMultiblock;
import igteam.immersive_geology.core.registration.IGTileTypes;
import net.minecraft.client.particle.BubbleParticle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

//HydroJetCutterTileEntity - Used in IGTileTypes when linking it to it's BlockReference
// This tile entity is dependent on the HydroJetRecipe which is defined in
public class HydroJetCutterTileEntity extends PoweredMultiblockTileEntity<HydroJetCutterTileEntity, HydrojetRecipe> implements IEBlockInterfaces.IPlayerInteraction, IBlockBounds, IIEInventory {
    private static final CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES =
            CachedShapesWithTransform.createForMultiblock(HydroJetCutterTileEntity::getShape);
    private static final Set<BlockPos> inputPrimary = ImmutableSet.of(
            new BlockPos(0, 0, 0)
    );
    private static final Set<BlockPos> inputItem = ImmutableSet.of(
            new BlockPos(1, 1, 2)
    );
    protected final int INPUT_SLOT = 0;
    protected final int OUTPUT_SLOT = 1;
    public FluidTank[] tanks = new FluidTank[]{
            new FluidTank(12 * FluidAttributes.BUCKET_VOLUME)
    };
    public NonNullList<ItemStack> inventory;
    public float progress = 0;
    //Used In IGTileType - Dependent on HydroJetCutterMultiblock and HydroJetRecipe
    Logger log = ImmersiveGeology.getNewLogger();
    HydrojetRecipe currentRecipe;
    private LazyOptional<IFluidHandler> insertionFluidHandler;
    private LazyOptional<IItemHandler> insertionHandler;

    public HydroJetCutterTileEntity() {
        super(HydroJetCutterMultiblock.INSTANCE, 2000, true, IGTileTypes.HYDROJET.get());
        this.inventory = NonNullList.withSize(2, ItemStack.EMPTY);
        insertionFluidHandler = LazyOptional.of(() -> tanks[0]);
        insertionHandler = this.registerConstantCap(new PoweredMultiblockTileEntity.MultiblockInventoryHandler_DirectProcessing(this));
    }

    private static List<AxisAlignedBB> getShape(BlockPos posInMultiblock) {
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        if (bX == 1 && bZ != 1) {
            if (bY == 1) {
                return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.125, 1.0));
            }
        }
        if (bX == 0 && bZ == 0) {
            if (bY == 1) {
                return Arrays.asList(new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9325, 1.0, 0.9325));
            }
            if (bY == 0) {
                return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
                        new AxisAlignedBB(0.0625, 0.5, 0.0625, 0.9325, 1.0, 0.9325)
                );
            }
        }
        if (bX == 0 && bZ == 1 && bY == 0) {
            return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));
        }
        if (bX == 1 && bZ == 1 && bY == 1) {
            return Arrays.asList(
                    new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.125, 1.0),
                    new AxisAlignedBB(0.0, 0.0, 0.25, 1.0, 1.0, 0.75)
            );

        }
        return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
    }

    @Override
    public boolean isEnergyPos() {
        return getEnergyPos().contains(this.posInMultiblock);
    }

    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {

        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && inputPrimary.contains(posInMultiblock)) {
            Direction d = this.getIsMirrored() ? this.getFacing().rotateY() : this.getFacing().rotateYCCW();
            if (facing == d) {
                HydroJetCutterTileEntity master = master();
                if (master != null) {
                    return master.insertionFluidHandler.cast();
                }
            }

            return LazyOptional.empty();
        }
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && inputItem.contains(posInMultiblock)){
            HydroJetCutterTileEntity master = master();
            if (master != null){
                return master.insertionHandler.cast();
            }
            return LazyOptional.empty();
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public void readCustomNBT(CompoundNBT nbt, boolean descPacket) {
        super.readCustomNBT(nbt, descPacket);
        progress = nbt.getFloat("progress");
        inventory = Utils.readInventory(nbt.getList("inventory", 10), 2);
    }

    @Override
    public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
        super.writeCustomNBT(nbt, descPacket);
        nbt.putFloat("progress", progress);
        nbt.put("inventory", Utils.writeInventory(inventory));
    }

    @Nullable
    @Override
    protected HydrojetRecipe getRecipeForId(ResourceLocation id) {
        return HydrojetRecipe.recipes.get(id);
    }

    @Override
    public void tick() {
        super.tick();
        assert this.world != null;

        if (!this.world.isRemote && !this.isDummy() && !this.isRSDisabled()) {
            if (!processQueue.isEmpty() && energyStorage.getEnergyStored() > 0) {
                getInternalTanks()[0].drain(1, IFluidHandler.FluidAction.EXECUTE);
            }
        }

        if(this == master()) {
            BlockPos particlePos = getPos();
            switch(getFacing()){
                case NORTH:
                    particlePos.add(2f, 1.5f,0f);
                    if(getIsMirrored()){
                        particlePos.add(-4,0, 0);
                    }
                    break;
                case SOUTH:
                    if(getIsMirrored()){
                        particlePos.add(-4,0, 0);
                    }
                    break;
                case WEST:
                    particlePos.add(4.5, -4.875f,0f);
                    if(getIsMirrored()){
                        particlePos.add(0.5,4, 0);
                    }
                    break;
                case EAST:
                    particlePos.add(-3.875, -1.75f,0f);
                    if(getIsMirrored()){
                        particlePos.add(4,-0.5, 0);
                    }
                    break;
            }



//            for (int i = 0; i < 360; i++) {
//                if (i % 230 == 0) {
//                    this.world.addParticle(ParticleTypes.BUBBLE_POP.getType(), 2f + particlePos.getX() - this.currentArmPosition, particlePos.getY() + 1f, particlePos.getZ() + this.headCurrentPosition, Math.cos(i) * 0.25d, 0.1d, Math.sin(i) * 0.25d);
//                }
//            }
//            this.world.addParticle(ParticleTypes.BUBBLE.getType(), 2f + particlePos.getX() - this.currentArmPosition, particlePos.getY() +1f, particlePos.getZ() + this.headCurrentPosition, Math.cos(1) * 0.25d, 0.1d, Math.sin(1) * 0.25d);
//            this.world.addParticle(ParticleTypes.FALLING_WATER.getType(), 2f + particlePos.getX() - this.currentArmPosition, particlePos.getY()+1f, particlePos.getZ() + this.headCurrentPosition, Math.cos(1) * 0.25d, 0.1d, Math.sin(1) * 0.25d);
        }
    }

    @Override
    public VoxelShape getBlockBounds(@Nullable ISelectionContext ctx) {
        return getShape(SHAPES);
    }

    @Override
    public Set<BlockPos> getEnergyPos() {
        return ImmutableSet.of(
                new BlockPos(0, 1, 2)
        );
    }

    @Override
    public Set<BlockPos> getRedstonePos() {
        return ImmutableSet.of(
                new BlockPos(1, 0, 1)
        );
    }

    @Override
    public void onEntityCollision(World world, Entity entity) {
        HydroJetCutterTileEntity master = this.master();
        if (master != null) {
            Vector3d center = Vector3d.copyCentered(master.getPos());
            AxisAlignedBB inputLocation = new AxisAlignedBB(center.x + 0.5, center.y + 0.5, center.z + 1.5, center.x + 1.5, center.y + 1.5, center.z + 2.5);
            if (!entity.getBoundingBox().intersects(inputLocation)) {
                log.warn("Item Not in correct Boundaries");
                //return;
            }
            if (entity instanceof ItemEntity && !((ItemEntity) entity).getItem().isEmpty()) {
                ItemStack stack = ((ItemEntity) entity).getItem();
                if (master.getInventory().get(INPUT_SLOT).isEmpty()) {
                    if (stack.isEmpty())
                        return;
                    HydrojetRecipe recipe = HydrojetRecipe.findRecipe(stack, Objects.requireNonNull(master.getInternalTanks())[0].getFluid());
                    if (recipe == null) {
                        return;
                    }
                    ItemStack displayStack = recipe.getDisplayStack(stack);

                    MultiblockProcessInWorld<HydrojetRecipe> process = new MultiblockProcessInWorld<HydrojetRecipe>(recipe, .5f, Utils.createNonNullItemStackListFromItemStack(displayStack));
                    if (master.addProcessToQueue(process, true, true)) {
                        master.addProcessToQueue(process, false, true);
                        stack.shrink(displayStack.getCount());
                        if (stack.getCount() <= 0)
                            entity.remove();
                    } else {
                        log.warn("Failed to process Item");
                    }
                }
            }
        }
    }

    @Override
    public boolean isInWorldProcessingMachine() {
        return true;
    }

    @Override
    public boolean additionalCanProcessCheck(MultiblockProcess<HydrojetRecipe> process) {
        return getInternalTanks()[0].getFluidAmount() > process.recipe.getFluidInputs().get(0).getAmount();
    }

    @Override
    public void doProcessFluidOutput(FluidStack output) {

    }
    private DirectionalBlockPos getOutputPos() {
        return new DirectionalBlockPos(this.getBlockPosForPos(new BlockPos(1, 1, 0)).offset(this.getFacing(), 1), this.getFacing());
    }

    //TODO set the position of this to the correct output area.
    private CapabilityReference<IItemHandler> outputCap = CapabilityReference.forTileEntityAt(this,
            this::getOutputPos, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

    @Override
    public void doProcessOutput(ItemStack output) {
        log.warn("Outputting");
        output = Utils.insertStackIntoInventory(this.outputCap, output, false);
        if (!output.isEmpty()) {
            Direction fw = this.getFacing().getOpposite();
            Direction shift_1 = this.getIsMirrored() ? this.getFacing().rotateYCCW() : this.getFacing().rotateY();

            Utils.dropStackAtPos(world, new DirectionalBlockPos(pos.offset(shift_1, 1).up().offset(fw, -1), fw).getPosition(), output, fw.getOpposite());
            master().getInventory().get(OUTPUT_SLOT).shrink(output.getCount());
        }
    }

    @Override
    public void onProcessFinish(MultiblockProcess<HydrojetRecipe> process) {
        if (master() != null) {
            doProcessOutput(process.recipe.getRecipeOutput());
            doGraphicalUpdates();
            markDirty();
        }
    }

    @Override
    public float getMinProcessDistance(MultiblockProcess<HydrojetRecipe> process) {
        return 0.95f;
    } // min amount of progress needed before new items can be inserted! (seperation between items) (doesn't apply to same item types...

    @Override
    public int getMaxProcessPerTick() {
        return 1; // Number of Parallel items that can run at once!
    }

    @Override
    public int getProcessQueueMaxLength() {
        return 64;
    }

    @Nullable
    @Override
    public NonNullList<ItemStack> getInventory() {
        return inventory;
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
    public IFluidTank[] getInternalTanks() {
        HydroJetCutterTileEntity master = this.master();
        return master.tanks;
    }

    @Nullable
    @Override
    public HydrojetRecipe findRecipeForInsertion(ItemStack itemStack) {
        return HydrojetRecipe.findRecipe(itemStack, Objects.requireNonNull(Objects.requireNonNull(master()).getInternalTanks())[0].getFluid());
    }

    @Override
    protected IFluidTank[] getAccessibleFluidTanks(Direction side) {
        return master().tanks;
    }

    @Override
    protected boolean canFillTankFrom(int iTank, Direction side, FluidStack resource) {
        if (inputPrimary.contains(posInMultiblock) && (side == null || side.getAxis() == getFacing().rotateYCCW().getAxis())) {
            HydroJetCutterTileEntity master = this.master();
            if (master == null || (master.tanks[0].getFluidAmount() >= master.tanks[0].getCapacity()))
                return false;
            return true;
        }
        return false;
    }

    @Override
    protected boolean canDrainTankFrom(int iTank, Direction side) {
        return false;
    }

    @Override
    public void doGraphicalUpdates() {
        this.markDirty();
        this.markContainingBlockForUpdate(null);
    }

    @Override
    public int[] getOutputSlots() {
        return new int[]{OUTPUT_SLOT};
    }

    @Override
    public int[] getOutputTanks() {
        return new int[]{0};
    }

    @Override
    public boolean interact(Direction direction, PlayerEntity playerEntity, Hand hand, ItemStack itemStack, float v, float v1, float v2) {
        return false;
    }

    // Animation information should go below this line!
    public ItemStack item = ItemStack.EMPTY;
    public ItemStack itemPile = ItemStack.EMPTY;
    public PositionEnum currentState = PositionEnum.ONE;

    public float machineProgress = 0f;
    public float currentArmPosition = 2.125f;
    public float newArmPosition = 2.125f;
    public float headCurrentPosition = -0.03125f;
    public float headNewPosition = -0.03125f;
    public enum PositionEnum {
        ZERO(0.30f, 2.125f, AnimationPart.ARM){ //16% of the progress is spent keeping arm in starting position when looping
            @Override
            public PositionEnum advance() {
                return ONE;
            }
        },
        ONE(0.52f, 1.8125f, AnimationPart.ARM){ //16% of progress is spent moving arm to this position
            @Override
            public PositionEnum advance() {
                return TWO;
            }
        },
        TWO(0.68f, -0.1875f, AnimationPart.HEAD){ //16% of progress is spent moving head to position
            @Override
            public PositionEnum advance() {
                return THREE;
            }
        },
        THREE(0.74f, 0.125f, AnimationPart.HEAD){ //16% of progress is spent moving head to position
            @Override
            public PositionEnum advance() {
                return FOUR;
            }
        },
        FOUR(0.80f, -0.03125f, AnimationPart.HEAD){ //16% of progress is spent moving head to position
            @Override
            public PositionEnum advance() {
                return FIVE;
            }
        },
        FIVE(0.9f, 1.5f, AnimationPart.ARM){ // 20% of progress is spent moving head to position (this should be the starting position of the head)
            @Override
            public PositionEnum advance() {
                return SIX;
            }
        },
        SIX(1f, 2.125f, AnimationPart.ARM){
            @Override
            public PositionEnum advance() {
                return ZERO;
            }
        };

        public final float breakPercent;
        public final float position;

        public final AnimationPart component;

        PositionEnum(float bp, float position, AnimationPart component){
            this.breakPercent = bp;
            this.position = position;
            this.component = component;
        }

        public abstract PositionEnum advance();
    }

    public enum AnimationPart {
        HEAD,
        ARM;
    }
}
