package igteam.immersive_geology.common.block.tileentity;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.client.IModelOffsetProvider;
import blusunrize.immersiveengineering.client.models.IOBJModelCallback;
import blusunrize.immersiveengineering.common.blocks.IEBaseTileEntity;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.util.CachedRecipe;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import igteam.api.main.IGMultiblockProvider;
import igteam.api.processing.recipe.BloomeryRecipe;
import igteam.immersive_geology.core.registration.IGTileTypes;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.Property;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.function.Supplier;

/**
 * Another Apology to Immersive Engineering, Using their internal Classes isn't a good thing to do... But it does make some things easier.
 */
public class BloomeryTileEntity extends IEBaseTileEntity implements ITickableTileEntity, IEBlockInterfaces.IStateBasedDirectional, IEBlockInterfaces.IBlockBounds, IEBlockInterfaces.IHasDummyBlocks,
        IIEInventory, IEBlockInterfaces.IInteractionObjectIE, IOBJModelCallback<BlockState>, IModelOffsetProvider {

    public int dummy = 0;

    public boolean formed = false;

    private NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);

    protected static final int inputSlot = 0, outputSlot = 1, fuelSlot = 2;

    protected float progress = 0;
    private float currentBurnTime = 0;

    public final Supplier<BloomeryRecipe> cachedRecipe = CachedRecipe.cached(
            BloomeryRecipe::findRecipe, () -> inventory.get(inputSlot)
    );

    public BloomeryTileEntity() {
        super(IGTileTypes.BLOOMERY.get());
    }

    @Override
    public void readCustomNBT(CompoundNBT nbt, boolean descPacket) {
        dummy = nbt.getInt("dummy");
        inventory = Utils.readInventory(nbt.getList("inventory", 10), 3);
        progress = nbt.getFloat("progress");
        currentBurnTime = nbt.getFloat("burnTime");
        renderBB = null;
    }

    @Override
    public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
        nbt.putInt("dummy", dummy);
        nbt.put("inventory", Utils.writeInventory(inventory));
        nbt.putFloat("progress", progress);
        nbt.putFloat("burnTime", currentBurnTime);
    }

    @Override
    public BlockPos getModelOffset(BlockState blockState, Vector3i vector3i) {
        return new BlockPos(0, dummy, 0);
    }

    @Nonnull
    @Override
    public VoxelShape getBlockBounds(ISelectionContext iSelectionContext) {
        return isDummy() ? VoxelShapes.create(0,0,0,1,0.75,1): VoxelShapes.fullCube();
    }

    @Override
    public Property<Direction> getFacingProperty() {
        return IEProperties.FACING_HORIZONTAL;
    }

    @Override
    public PlacementLimitation getFacingLimitation() {
        return PlacementLimitation.HORIZONTAL;
    }

    @Override
    public void placeDummies(BlockItemUseContext ctx, BlockState state)
    {
        state = state.with(IEProperties.MULTIBLOCKSLAVE, true);
        for(int i = 1; i <= 1; i++)
        {
            world.setBlockState(pos.up(i), state);
            ((BloomeryTileEntity)world.getTileEntity(pos.up(i))).dummy = i;
            ((BloomeryTileEntity)world.getTileEntity(pos.up(i))).setFacing(getFacing());
        }
    }

    @Override
    public void setFacing(@Nonnull Direction facing) {
        BlockPos lowest = pos.down(dummy);
        for(int i = 0; i < 3; ++i)
        {
            BlockPos pos = lowest.up(i);
            BlockState state = getWorldNonnull().getBlockState(pos);
            if(state.getBlock()== IGMultiblockProvider.bloomery)
                getWorldNonnull().setBlockState(pos, state.with(getFacingProperty(), facing));
        }
    }

    @Override
    public void breakDummies(BlockPos blockPos, BlockState blockState) {
        tempMasterTE = master();
        for(int i = 0; i <= 1; i++)
        {
            BlockPos p = getPos().down(dummy).up(i);
            assert world != null;
            if(world.getTileEntity(p) instanceof BloomeryTileEntity)
                world.removeBlock(p, false);
        }
    }

    @Override
    public IEBlockInterfaces.IGeneralMultiblock master() {
        if(!isDummy())
            return this;
        if(tempMasterTE instanceof BloomeryTileEntity)
            return (BloomeryTileEntity) tempMasterTE;
        BlockPos masterPos = getPos().down(dummy);
        TileEntity te = Utils.getExistingTileEntity(world, masterPos);
        return te instanceof BloomeryTileEntity ? (BloomeryTileEntity) te : null;
    }

    @Override
    public boolean isDummy()
    {
        return dummy!=0;
    }


    public IEBlockInterfaces.IInteractionObjectIE getGuiMaster() {
        return (IEBlockInterfaces.IInteractionObjectIE) this.master();
    }

    @Override
    public NonNullList<ItemStack> getInventory() {
        return inventory;
    }

    @Override
    public boolean isStackValid(int i, ItemStack itemStack) {
        return false;
    }

    @Override
    public int getSlotLimit(int i) {
        return inputSlot == i ? 1 : 64;
    }

    @Override
    public void doGraphicalUpdates() {

    }

    AxisAlignedBB renderBB;

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        if(renderBB==null)
            renderBB = new AxisAlignedBB(0, 0, 0, 1, 2, 1).offset(pos);
        return renderBB;
    }

    public boolean isBurning() {
        return this.currentBurnTime > 0;
    }

    public static HashMap<Item, Integer> fuelMap = new HashMap<>();

    public boolean hasFuelAvailable(){
        return fuelMap.containsKey(inventory.get(fuelSlot).getItem());
    }

    @Override
    public void tick() {
        checkForNeedlessTicking();
        if(isDummy()) return;
        boolean burning = isBurning();
        boolean dirty = false;
        if (burning) this.currentBurnTime--;
        BloomeryRecipe recipe = cachedRecipe.get();
        if(recipe != null){
            ItemStack input = inventory.get(inputSlot);
            if(recipe.matches(input)) {
                if(!burning)
                {
                    dirty = burnFuelAsNeeded();;
                }
                if(isBurning() && inventory.get(outputSlot).getCount() < 64) {
                    progress++;
                    if (progress >= recipe.getTime()) {
                        ItemStack outputSlotItem = inventory.get(outputSlot);
                        if (outputSlotItem.isEmpty()) {
                            inventory.set(outputSlot, recipe.getRecipeOutput());
                            progress = 0;
                            inventory.get(inputSlot).shrink(recipe.getRecipeInput().getCount());
                        } else {
                            if (outputSlotItem.isItemEqual(recipe.getRecipeOutput())) {
                                inventory.get(outputSlot).grow(recipe.getRecipeOutput().getCount());
                                progress = 0;
                                inventory.get(inputSlot).shrink(recipe.getRecipeInput().getCount());
                            }
                        }
                        dirty = true;
                    }
                } else {
                    if(progress > 0) progress -= 0.5;
                }
            } else {
                if(progress > 0) progress -= 0.5;
            }
        } else {
            progress = 0;
        }
        if (burning != this.isBurning()) {
            this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(AbstractFurnaceBlock.LIT, Boolean.valueOf(this.isBurning())), 3);
            dirty = true;
        }

        if (dirty) {
            this.markDirty();
        }
    }

    private boolean burnFuelAsNeeded() {
        if(hasFuelAvailable()) {
            currentBurnTime += fuelMap.get(inventory.get(fuelSlot).getItem());
            inventory.get(fuelSlot).shrink(1);
            return true;
        }
        return false;
    }

    @Override
    public boolean canHammerRotate(Direction side, Vector3d hit, LivingEntity entity)
    {
        return true;
    }

    public void setProgress(int value)
    {
        progress = value;
    }

    public int getProgress()
    {
        return (int)progress;
    }

    @Override
    public boolean canRotate(Direction axis)
    {
        return true;
    }

    public boolean canUseGui(PlayerEntity player) {
        return true;
    }

}
