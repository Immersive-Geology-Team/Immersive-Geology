package com.igteam.immersive_geology.common.block.tileentity;

import blusunrize.immersiveengineering.api.IEEnums;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import blusunrize.immersiveengineering.common.util.Utils;
import com.google.common.collect.ImmutableSet;
import com.igteam.immersive_geology.api.crafting.recipes.SeparatorRecipe;
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
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class GravitySeparatorTileEntity extends PoweredMultiblockTileEntity<GravitySeparatorTileEntity, SeparatorRecipe> implements IBlockBounds {

    public static final Set<BlockPos> Energy_IN = ImmutableSet.of(new BlockPos(3, 1, 0));

    public GravitySeparatorTileEntity(){
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

    private boolean isInInput(boolean allowMiddleLayer)
    {
        if(posInMultiblock.getY()==2||(allowMiddleLayer&&posInMultiblock.getY()==1))
            return posInMultiblock.getX() > 0&&posInMultiblock.getX() < 4;
        return false;
    }

    @Override
    public Set<BlockPos> getEnergyPos(){
        return Energy_IN;
    }

    @Override
    public boolean isInWorldProcessingMachine(){
        return true;
    }

    @Override
    public void onEntityCollision(World world, Entity entity)
    {
        // Actual intersection with the input box is checked later
        boolean bpos = isInInput(true);
        if(bpos&&!world.isRemote&&entity.isAlive()&&!isRSDisabled())
        {
            GravitySeparatorTileEntity master = master();
            if(master==null)
                return;
            Vector3d center = Vector3d.copyCentered(master.getPos()).add(0, 0.25, 0);
            AxisAlignedBB separatorInternal = new AxisAlignedBB(center.x-1.0625, center.y, center.z-1.0625, center.x+1.0625, center.y+1.25, center.z+1.0625);
            if(!entity.getBoundingBox().intersects(separatorInternal))
                return;
            if(entity instanceof ItemEntity &&!((ItemEntity)entity).getItem().isEmpty())
            {
                ItemStack stack = ((ItemEntity)entity).getItem();
                if(stack.isEmpty())
                    return;
                SeparatorRecipe recipe = (SeparatorRecipe) master.findRecipeForInsertion(stack);
                if(recipe==null)
                    return;
                ItemStack displayStack = recipe.getDisplayStack(stack);
                MultiblockProcess<SeparatorRecipe> process = new MultiblockProcessInWorld<SeparatorRecipe>(recipe, .5f, Utils.createNonNullItemStackListFromItemStack(displayStack));
                if(master.addProcessToQueue(process, true, true))
                {
                    master.addProcessToQueue(process, false, true);
                    stack.shrink(displayStack.getCount());
                    if(stack.getCount() <= 0)
                        entity.remove();
                }
            }
        }
    }

    @Override
    public void doProcessOutput(ItemStack output){
    }

    @Override
    public void doProcessFluidOutput(FluidStack output){
    }

    @Override
    public void onProcessFinish(MultiblockProcess<SeparatorRecipe> process){
    }

    @Override
    public boolean additionalCanProcessCheck(MultiblockProcess<SeparatorRecipe> process){
        return false;
    }

    @Override
    public float getMinProcessDistance(MultiblockProcess<SeparatorRecipe> process){
        return 0;
    }

    @Override
    public int getMaxProcessPerTick(){
        return 1;
    }

    @Override
    public int getProcessQueueMaxLength(){
        return 1;
    }

    @Override
    public boolean isStackValid(int slot, ItemStack stack){
        return true;
    }

    @Override
    public int getSlotLimit(int slot){
        return 64;
    }

    @Override
    public int[] getOutputSlots(){
        return null;
    }

    @Override
    public int[] getOutputTanks(){
        return new int[]{1};
    }

    @Override
    public void doGraphicalUpdates(int slot){
        this.markDirty();
        this.markContainingBlockForUpdate(null);
    }

    @Override
    public SeparatorRecipe findRecipeForInsertion(ItemStack inserting){
        return SeparatorRecipe.findRecipe(inserting);
    }

    @Override
    protected SeparatorRecipe getRecipeForId(ResourceLocation id){
        return null;
    }

    @Override
    public NonNullList<ItemStack> getInventory(){
        return null;
    }

    @Override
    public IFluidTank[] getInternalTanks(){
        return null;
    }

    @Nonnull
    @Override
    protected IFluidTank[] getAccessibleFluidTanks(Direction side) {
        return new IFluidTank[0];
    }

    @Override
    protected boolean canFillTankFrom(int iTank, Direction side, FluidStack resource){
        return false;
    }

    @Override
    protected boolean canDrainTankFrom(int iTank, Direction side){
        return false;
    }

    private static CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES = CachedShapesWithTransform.createForMultiblock(GravitySeparatorTileEntity::getShape);

    @Override
    public VoxelShape getBlockBounds(ISelectionContext ctx){
        return SHAPES.get(this.posInMultiblock, Pair.of(getFacing(), getIsMirrored()));
    }

    @Nonnull
    @Override
    public VoxelShape getCollisionShape(ISelectionContext ctx) {
        return null;
    }

    @Nonnull
    @Override
    public VoxelShape getSelectionShape(ISelectionContext ctx) {
        return null;
    }

    //Direct Copy from IP's Pumpjack, this will need to be changed.
    private static List<AxisAlignedBB> getShape(BlockPos posInMultiblock){
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
    }
}
