package com.igteam.immersive_geology.common.block.tileentity;

import blusunrize.immersiveengineering.api.IEEnums;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import blusunrize.immersiveengineering.common.util.Utils;
import com.google.common.collect.ImmutableSet;
import com.igteam.immersive_geology.api.crafting.recipes.SeparatorRecipe;
import com.igteam.immersive_geology.common.crafting.Serializers;
import com.igteam.immersive_geology.common.multiblocks.GravitySeparatorMultiblock;
import com.igteam.immersive_geology.core.registration.IGTileTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class GravitySeparatorTileEntity extends PoweredMultiblockTileEntity<GravitySeparatorTileEntity, SeparatorRecipe> implements IBlockBounds {

    public static final Set<BlockPos> Energy_IN = ImmutableSet.of(new BlockPos(3, 1, 0));
    public static final Set<BlockPos> Redstone_IN = ImmutableSet.of(new BlockPos(1, 0, 1));

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
    public boolean isRSDisabled() {
        return true;
    }

    private boolean isInInput(boolean allowMiddleLayer) {
        return true;
    }

    @Override
    public Set<BlockPos> getRedstonePos() {
        return Redstone_IN;
    }

    @Override
    public Set<BlockPos> getEnergyPos() {
        return Energy_IN;
    }

    @Override
    public boolean isInWorldProcessingMachine() {
        return true;
    }

    @Override
    public void onEntityCollision(World world, Entity entity) {
        // Actual intersection with the input box is checked later
        boolean bpos = isInInput(true);
        if (bpos && !world.isRemote && entity.isAlive() && !isRSDisabled()) {
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
                if (recipe == null)
                    return;
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
    public void doProcessOutput(ItemStack output) {
    }

    @Override
    public void doProcessFluidOutput(FluidStack output) {
    }

    @Override
    public void onProcessFinish(MultiblockProcess<SeparatorRecipe> process) {
    }

    @Override
    public boolean additionalCanProcessCheck(MultiblockProcess<SeparatorRecipe> process) {
        return false;
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
        return 1;
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
        return new int[]{1};
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
        return null;
    }

    @Nonnull
    @Override
    protected IFluidTank[] getAccessibleFluidTanks(Direction side) {
        return new IFluidTank[0];
    }

    @Override
    protected boolean canFillTankFrom(int iTank, Direction side, FluidStack resource) {
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
