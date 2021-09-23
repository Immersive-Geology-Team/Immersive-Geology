package com.igteam.immersive_geology.common.block.tileentity;

import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.api.utils.DirectionalBlockPos;
import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import blusunrize.immersiveengineering.common.util.Utils;
import com.google.common.collect.ImmutableSet;
import com.igteam.immersive_geology.api.crafting.recipes.recipe.SeparatorRecipe;
import com.igteam.immersive_geology.common.multiblocks.GravitySeparatorMultiblock;
import com.igteam.immersive_geology.core.registration.IGTileTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class GravitySeparatorTileEntity extends PoweredMultiblockTileEntity<GravitySeparatorTileEntity, SeparatorRecipe> implements IBlockBounds {

    public static final Set<BlockPos> Fluid_OUT = ImmutableSet.of(new BlockPos(1,0,0), new BlockPos(1,0,2));
    public static final Set<BlockPos> Fluid_IN = ImmutableSet.of(new BlockPos(1, 6, 1));
    public static final Set<BlockPos> Redstone_IN = ImmutableSet.of(new BlockPos(1, 6, 2));

    /** Input Fluid Tank<br> */
    public static final int TANK_INPUT = 0;

    /** Output Fluid Tank<br> */
    public static final int TANK_OUTPUT = 1;

    public final FluidTank[] bufferTanks = {new FluidTank(16000), new FluidTank(16000)};

    public GravitySeparatorTileEntity() {
        super(GravitySeparatorMultiblock.INSTANCE, 0, false, IGTileTypes.GRAVITY.get());
    }

    @Override
    public TileEntityType<?> getType() {
        return IGTileTypes.GRAVITY.get();
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected boolean canFillTankFrom(int iTank, Direction side, FluidStack resource) {
        if (this.posInMultiblock.equals(Fluid_IN)) {
            if (side == null || side == getFacing()) {
                GravitySeparatorTileEntity master = master();
                if (master != null && master.bufferTanks[TANK_INPUT].getFluidAmount() < master.bufferTanks[TANK_INPUT].getCapacity()) {
                    if (!master.bufferTanks[TANK_INPUT].isEmpty()) {
                        return resource.isFluidEqual(master.bufferTanks[TANK_INPUT].getFluid());
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Set<BlockPos> getRedstonePos() {
        return Redstone_IN;
    }

    @Override
    public Set<BlockPos> getEnergyPos() {
        return null;
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
                MultiblockProcess<SeparatorRecipe> process = new MultiblockProcessInWorld<SeparatorRecipe>(recipe, .5f, Utils.createNonNullItemStackListFromItemStack(displayStack));
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
    public boolean shouldRenderAsActive()
    {
        return !processQueue.isEmpty();
    }

    private CapabilityReference<IItemHandler> output = CapabilityReference.forTileEntityAt(this,
            () -> new DirectionalBlockPos(getPos().add(0, 0, 0).offset(getFacing(), -2), getFacing()),
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
    }

    @Override
    public boolean additionalCanProcessCheck(MultiblockProcess<SeparatorRecipe> process) {
        return true;
    }

    @Override
    public float getMinProcessDistance(MultiblockProcess<SeparatorRecipe> process) {
        return 0;
    }

    @Override
    public int getMaxProcessPerTick() {
        return 1;
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
        return 64;
    }

    @Override
    public int[] getOutputSlots() {
        return null;
    }

    @Override
    public int[] getOutputTanks() {
        return new int[]{TANK_OUTPUT};
    }

    @Override
    public void doGraphicalUpdates(int slot) {
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
        return this.bufferTanks;
    }

    @Nonnull
    @Override
    protected IFluidTank[] getAccessibleFluidTanks(Direction side) {
        GravitySeparatorTileEntity master = master();
        if(master != null){
            if(this.posInMultiblock.equals(Fluid_IN)){
                if(side == null || side == Direction.UP){
                    return new IFluidTank[]{master.bufferTanks[TANK_INPUT]};
                }
            }

            if(this.posInMultiblock.equals(Fluid_OUT)){
                if(side == null || side == getFacing().getOpposite()){
                    return new IFluidTank[]{master.bufferTanks[TANK_OUTPUT]};
                }
            }
        }
        return new IFluidTank[0];
    }

    @Override
    protected boolean canDrainTankFrom(int iTank, Direction side) {
        if(this.posInMultiblock.equals(Fluid_OUT) && (side == null || side == getFacing())){
            GravitySeparatorTileEntity master = master();

            return master != null && master.bufferTanks[TANK_OUTPUT].getFluidAmount() > 0;
        }
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
