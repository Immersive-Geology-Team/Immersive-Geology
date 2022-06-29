package com.igteam.immersive_geology.common.block.tileentity;

import blusunrize.immersiveengineering.api.fluid.FluidUtils;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.api.utils.DirectionalBlockPos;
import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.client.utils.TextUtils;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import blusunrize.immersiveengineering.common.util.Utils;
import com.google.common.collect.ImmutableSet;
import igteam.immersive_geology.processing.recipe.SeparatorRecipe;
import com.igteam.immersive_geology.common.multiblocks.GravitySeparatorMultiblock;
import com.igteam.immersive_geology.core.registration.IGTileTypes;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
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

import javax.annotation.Nonnull;
import java.util.*;

//Sorry to IE for using their internal classes, we should have used an API, and we'll maybe fix it later.
public class GravitySeparatorTileEntity extends PoweredMultiblockTileEntity<GravitySeparatorTileEntity, SeparatorRecipe> implements IEBlockInterfaces.IBlockOverlayText, IEBlockInterfaces.IPlayerInteraction, IBlockBounds {

    public static final Set<BlockPos> Redstone_IN = ImmutableSet.of(new BlockPos(1, 6, 2));

    /** Input Fluid Tank<br> */
    public static final int TANK_INPUT = 0;

    public final FluidTank tank = new FluidTank(16 * FluidAttributes.BUCKET_VOLUME);
    private final List<CapabilityReference<IFluidHandler>> fluidNeighbors;

    public GravitySeparatorTileEntity() {
        super(GravitySeparatorMultiblock.INSTANCE, 0, false, IGTileTypes.GRAVITY.get());
        this.fluidNeighbors = new ArrayList();
        this.fluidNeighbors.add(CapabilityReference.forNeighbor(this, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.UP));

    }


    @Override
    public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
        super.writeCustomNBT(nbt, descPacket);
        nbt.put("tank_input", tank.writeToNBT(new CompoundNBT()));

    }

    @Override
    public void readCustomNBT(CompoundNBT nbt, boolean descPacket) {
        super.readCustomNBT(nbt, descPacket);
        tank.readFromNBT(nbt.getCompound("tank_input"));
    }

    @Override
    public TileEntityType<?> getType() {
        return IGTileTypes.GRAVITY.get();
    }

    @Override
    public void tick() {
        super.tick();
        GravitySeparatorTileEntity master = this.master();
        if(master != null) { //TODO FIX THIS SO IT ISN'T SHIT
            if (!master.processQueue.isEmpty()) {
                if (!master.tank.isEmpty()) {
                    master.tank.drain(1, IFluidHandler.FluidAction.EXECUTE);
                }
            }
        }
    }

    @Override
    public Set<BlockPos> getRedstonePos() {
        return Redstone_IN;
    }

    @Override
    public Set<BlockPos> getEnergyPos() {
        return ImmutableSet.of(new BlockPos(1, 3, 1));
    }

    @Override
    public boolean isInWorldProcessingMachine() {
        return true;
    }

    public boolean isInInput(){
            return true;
    }

    @Override
    public void onEntityCollision(World world, Entity entity) {
        // Actual intersection with the input box is checked later
        boolean bpos = isInInput();
        if (bpos && !world.isRemote && entity.isAlive()) {
            GravitySeparatorTileEntity master = master();
            if (master == null)
                return;
            Vector3d center = Vector3d.copyCentered(master.getPos());
            AxisAlignedBB separatorInternal = new AxisAlignedBB(center.x - 4, center.y, center.z - 4, center.x + 4, center.y + 10, center.z + 4);
            if (!entity.getBoundingBox().intersects(separatorInternal))
                return;
            if (entity instanceof ItemEntity && !((ItemEntity) entity).getItem().isEmpty()) {
                ItemStack stack = ((ItemEntity) entity).getItem();
                if (stack.isEmpty())
                    return;
                SeparatorRecipe recipe = master.findRecipeForInsertion(stack);
                if (recipe == null) {
                    return;
                }
                ItemStack displayStack = recipe.getDisplayStack(stack);
                MultiblockProcessInWorld<SeparatorRecipe> process = new MultiblockProcessInWorld<SeparatorRecipe>(recipe, .5f, Utils.createNonNullItemStackListFromItemStack(displayStack));
                if (master.addProcessToQueue(process, true, true)) {
                    master.addProcessToQueue(process, false, true);
                    stack.shrink(displayStack.getCount());
                    if (stack.getCount() <= 0)
                        entity.remove();
                }
            }
        }
    }

    @Override
    protected boolean shouldRenderAsActiveImpl() {
        return !processQueue.isEmpty();
    }

    private CapabilityReference<IItemHandler> output = CapabilityReference.forTileEntityAt(this,
            () -> new DirectionalBlockPos(getPos().add(0, 0, 0).offset(getFacing(), -2), getFacing()),
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

    private CapabilityReference<IItemHandler> waste = CapabilityReference.forTileEntityAt(this,
            () -> new DirectionalBlockPos(getPos().add(0, 0, 0).offset(getFacing(), 2), getFacing()),
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

    @Override
    public void doProcessOutput(ItemStack output) {
        output = Utils.insertStackIntoInventory(this.output, output, false);
        if(!output.isEmpty())
            Utils.dropStackAtPos(world, getPos().add(0, 0, 0).offset(getFacing(), -2), output, getFacing().getOpposite());
    }

    LazyOptional<IItemHandler> insertionHandler = registerConstantCap(
            new MultiblockInventoryHandler_DirectProcessing<>(this).setProcessStacking(true)
    );

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing)
    {
        if(capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY&& isInInput())
        {
            GravitySeparatorTileEntity master = master();
            if(master!=null)
                return master.insertionHandler.cast();
        }
        return super.getCapability(capability, facing);
    }

    public ITextComponent[] getOverlayText(PlayerEntity player, RayTraceResult mop, boolean hammer) {
        if (Utils.isFluidRelatedItemStack(player.getHeldItem(Hand.MAIN_HAND))) {
            GravitySeparatorTileEntity master = (GravitySeparatorTileEntity)this.master();
            FluidStack fs = master != null ? master.tank.getFluid() : this.tank.getFluid();
            return new ITextComponent[]{TextUtils.formatFluidStack(fs)};
        } else {
            return null;
        }
    }

    @Override
    public boolean useNixieFont(PlayerEntity playerEntity, RayTraceResult rayTraceResult) {
        return false;
    }


    public boolean interact(Direction side, PlayerEntity player, Hand hand, ItemStack heldItem, float hitX, float hitY, float hitZ) {
        GravitySeparatorTileEntity master = (GravitySeparatorTileEntity)this.master();
        if (master != null && FluidUtils.interactWithFluidHandler(player, hand, master.tank)) {
            this.updateMasterBlock((BlockState)null, true);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int[] getCurrentProcessesStep()
    {
        GravitySeparatorTileEntity master = master();
        if(master!=this&&master!=null)
            return master.getCurrentProcessesStep();
        int[] ia = new int[processQueue.size() > 0?1: 0];
        for(int i = 0; i < ia.length; i++)
            ia[i] = processQueue.get(i).processTick;
        return ia;
    }

    @Override
    public int[] getCurrentProcessesMax()
    {
        GravitySeparatorTileEntity master = master();
        if(master!=this&&master!=null)
            return master.getCurrentProcessesMax();
        int[] ia = new int[processQueue.size() > 0?1: 0];
        for(int i = 0; i < ia.length; i++)
            ia[i] = processQueue.get(i).maxTicks;
        return ia;
    }

    @Override
    public void doProcessFluidOutput(FluidStack output) {
    }

    @Override
    public void onProcessFinish(MultiblockProcess<SeparatorRecipe> process) {
        Ingredient waste = process.recipe.waste;
        Optional<ItemStack> optionalWaste = Arrays.stream(waste.getMatchingStacks()).findFirst();

        ItemStack itemWaste = optionalWaste.orElse(ItemStack.EMPTY);
        itemWaste = Utils.insertStackIntoInventory(this.waste, itemWaste, false);
        if(!itemWaste.isEmpty())
            Utils.dropStackAtPos(world, getPos().add(0, 0, 0).offset(getFacing(), 2), itemWaste, getFacing());
    }

    @Override
    public boolean additionalCanProcessCheck(MultiblockProcess<SeparatorRecipe> process) {
        return !tank.isEmpty();
    }

    @Override
    public float getMinProcessDistance(MultiblockProcess<SeparatorRecipe> process) {
        return (float) 0.05;
    } // min amount of progress needed before new items can be inserted! (seperation between items) (doesn't apply to same item types...

    @Override
    public int getMaxProcessPerTick() {
        return 64; // Number of Parallel items that can run at once!
    }

    @Override
    public int getProcessQueueMaxLength() {
        return 2048;
    }

    @Override
    public boolean isStackValid(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 3;
    }

    @Override
    public int[] getOutputSlots() {
        return null;
    }

    @Override
    public int[] getOutputTanks() {
        return new int[]{TANK_INPUT};
    }

    @Override
    public void doGraphicalUpdates() {
        this.markDirty();
        this.markContainingBlockForUpdate(null);
    }

    @Override
    public SeparatorRecipe findRecipeForInsertion(ItemStack inserting) {
        return SeparatorRecipe.findRecipe(inserting);
    }

    @Override
    protected SeparatorRecipe getRecipeForId(ResourceLocation id) {
        return SeparatorRecipe.recipes.get(id);
    }

    @Override
    public NonNullList<ItemStack> getInventory() {
        return null;
    }

    @Override
    public IFluidTank[] getInternalTanks() {
        return new IFluidTank[]{tank};
    }

    private static final BlockPos outputOffset = new BlockPos(1, 0, 0);
    private static final Set<BlockPos> inputOffset = ImmutableSet.of(
            new BlockPos(1, 6, 1)
    );

    @Nonnull
    @Override
    protected IFluidTank[] getAccessibleFluidTanks(Direction side) {
        GravitySeparatorTileEntity master = master();
        if(master != null){
            if(inputOffset.contains(posInMultiblock)){
                return new IFluidTank[]{master.tank};
            }
        }
        return new IFluidTank[0];
    }


    @Override
    protected boolean canFillTankFrom(int iTank, Direction side, FluidStack resource) {
        if(inputOffset.contains(posInMultiblock) && resource.getFluid().equals(Fluids.WATER)) {
            GravitySeparatorTileEntity master = this.master();
            if(master == null || master.tank.getFluidAmount() >= master.tank.getCapacity()){
                return false;
            }

            return true;
        }

        return false;
    }

    @Override
    protected boolean canDrainTankFrom(int iTank, Direction side) {
        return false;
    }

    private static CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES = CachedShapesWithTransform.createForMultiblock(GravitySeparatorTileEntity::getShape);

    @Override
    public VoxelShape getBlockBounds(ISelectionContext ctx) {
        return SHAPES.get(this.posInMultiblock, Pair.of(getFacing(), getIsMirrored()));
    }

    //Direct Copy from IP's Pumpjack, this will need to be changed.
    private static List<AxisAlignedBB> getShape(BlockPos posInMultiblock){
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();
        if (bY == 6)
        {
            if (bZ == 1 && bX == 2)
            {
                return Arrays.asList(new AxisAlignedBB(0.0, 0.75, 0.0, 0.5, 1.0, 1.0));

            }
            if (bZ == 2 && bX == 1)
            {
                return Arrays.asList(new AxisAlignedBB(0.0, 0.75, 0.0, 1.0, 1.0, 0.5));

            }
            if (bZ == 0 && bX == 1)
            {
                return Arrays.asList(new AxisAlignedBB(0.0, 0.75, 0.5, 1.0, 1.0, 1.0));

            }
            if (bZ == 1 && bX == 0)
            {
                return Arrays.asList(new AxisAlignedBB(0.5, 0.75, 0.0, 1.0, 1.0, 1.0));

            }
        }
        return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));

    }
}
