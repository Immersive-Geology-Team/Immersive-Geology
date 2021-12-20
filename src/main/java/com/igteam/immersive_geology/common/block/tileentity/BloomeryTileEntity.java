package com.igteam.immersive_geology.common.block.tileentity;

import blusunrize.immersiveengineering.api.IEEnums;
import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.client.IModelOffsetProvider;
import blusunrize.immersiveengineering.api.energy.immersiveflux.FluxStorage;
import blusunrize.immersiveengineering.client.models.IOBJModelCallback;
import blusunrize.immersiveengineering.common.blocks.IEBaseTileEntity;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.metal.ClocheTileEntity;
import blusunrize.immersiveengineering.common.config.IEServerConfig;
import blusunrize.immersiveengineering.common.util.CachedRecipe;
import blusunrize.immersiveengineering.common.util.EnergyHelper;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.crafting.recipes.recipe.BloomeryRecipe;
import com.igteam.immersive_geology.core.registration.IGMultiblockRegistrationHolder;
import com.igteam.immersive_geology.core.registration.IGTileTypes;
import javafx.scene.effect.Bloom;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.Property;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

/**
 * Another Apology to Immersive Engineering, Using their internal Classes isn't a good thing to do... But it does make some things easier.
 */
public class BloomeryTileEntity extends IEBaseTileEntity implements ITickableTileEntity, IEBlockInterfaces.IStateBasedDirectional, IEBlockInterfaces.IBlockBounds, IEBlockInterfaces.IHasDummyBlocks,
        IIEInventory, IEBlockInterfaces.IInteractionObjectIE, IOBJModelCallback<BlockState>, IModelOffsetProvider, IEBlockInterfaces.IBlockOverlayText, IEBlockInterfaces.IPlayerInteraction {

    public int dummy = 0;
    private NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);

    protected static final int inputSlot = 0, outputSlot = 1, fuelSlot = 2;

    protected float progress = 0;
    protected int maxProgress = 100;

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
        renderBB = null;
    }

    @Override
    public void writeCustomNBT(CompoundNBT nbt, boolean descPacket) {
        nbt.putInt("dummy", dummy);
        nbt.put("inventory", Utils.writeInventory(inventory));
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
    public void setFacing(Direction facing) {
        BlockPos lowest = pos.down(dummy);
        for(int i = 0; i < 3; ++i)
        {
            BlockPos pos = lowest.up(i);
            BlockState state = getWorldNonnull().getBlockState(pos);
            if(state.getBlock()== IGMultiblockRegistrationHolder.Multiblock.bloomery)
                getWorldNonnull().setBlockState(pos, state.with(getFacingProperty(), facing));
        }
    }

    @Override
    public void breakDummies(BlockPos blockPos, BlockState blockState) {
        tempMasterTE = master();
        for(int i = 0; i <= 1; i++)
        {
            BlockPos p = getPos().down(dummy).up(i);
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

    @Override
    public IEBlockInterfaces.IInteractionObjectIE getGuiMaster() {
        return null;
    }

    @Override
    public boolean canUseGui(PlayerEntity playerEntity) {
        return false;
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

    private float currentBurnTime = 0;

    public boolean canSmelt(){
        return currentBurnTime > 0;
    }

    public static HashMap<Item, Integer> fuelMap = new HashMap<>();

    public boolean hasFuelAvailable(){
        return fuelMap.containsKey(inventory.get(fuelSlot).getItem());
    }

    @Override
    public void tick() {
        checkForNeedlessTicking();
        if(isDummy()) return;
        BloomeryRecipe recipe = cachedRecipe.get();
        if(recipe != null){
            ItemStack input = inventory.get(inputSlot);
            if(recipe.matches(input)) {
                burnFuelAsNeeded();
                if(canSmelt()) {
                    if (progress >= maxProgress) {
                        ItemStack outputSlotItem = inventory.get(outputSlot);
                        if(outputSlotItem.isEmpty()) {
                            inventory.set(outputSlot, recipe.getRecipeOutput());
                            return;
                        } else {
                            if(outputSlotItem.isItemEqual(recipe.getRecipeOutput())){
                                inventory.get(outputSlot).grow(recipe.getRecipeOutput().getCount());
                                progress = 0;
                                inventory.get(inputSlot).shrink(recipe.getRecipeInput().getCount());
                            }
                        }
                    } else {
                        progress += (1 * recipe.getTime()); //how fast does it progress?
                        currentBurnTime--;
                    }
                } else {
                    if(progress > 0) progress *= 0.75;
                }
            } else {
                if(progress > 0) progress *= 0.75;
            }
        }
    }

    private void burnFuelAsNeeded() {
        if(currentBurnTime == 0 && hasFuelAvailable()) {
            currentBurnTime += fuelMap.get(inventory.get(fuelSlot).getItem());
            inventory.get(fuelSlot).shrink(1);
        }
    }

    @Override
    public boolean canHammerRotate(Direction side, Vector3d hit, LivingEntity entity)
    {
        return true;
    }

    @Override
    public boolean canRotate(Direction axis)
    {
        return true;
    }

    @Nullable
    @Override
    public ITextComponent[] getOverlayText(PlayerEntity playerEntity, RayTraceResult rayTraceResult, boolean b) {
        BloomeryTileEntity master = (BloomeryTileEntity) master();
        StringTextComponent inputString = new StringTextComponent("INPUT: " + master.inventory.get(inputSlot).getDisplayName().getString() + " x " + master.inventory.get(inputSlot).getCount());
        StringTextComponent outputString = new StringTextComponent("OUTPUT: " + master.inventory.get(outputSlot).getDisplayName().getString() + " x " + master.inventory.get(outputSlot).getCount());
        StringTextComponent fuelString = new StringTextComponent("FUEL: " + master.inventory.get(fuelSlot).getDisplayName().getString() + " x " + master.inventory.get(fuelSlot).getCount());
        StringTextComponent burnTime = new StringTextComponent("BURNTIME: " + master.currentBurnTime);
        StringTextComponent progressTime = new StringTextComponent("PROGRESS: " + master.progress + "/100");

        return new ITextComponent[]{inputString, outputString, fuelString, burnTime, progressTime};
    }

    @Override
    public boolean useNixieFont(PlayerEntity playerEntity, RayTraceResult rayTraceResult) {
        return false;
    }

    @Override
    public boolean interact(Direction direction, PlayerEntity playerEntity, Hand hand, ItemStack itemStack, float v, float v1, float v2) {
        BloomeryTileEntity master = (BloomeryTileEntity) master();
        if(itemStack.isEmpty()){
            if(!master.inventory.get(outputSlot).isEmpty()){
                playerEntity.setHeldItem(hand, master.inventory.get(outputSlot));
                master.inventory.set(outputSlot, ItemStack.EMPTY);
                return true;
            }
            if(!master.inventory.get(inputSlot).isEmpty()) {
                playerEntity.setHeldItem(hand, master.inventory.get(inputSlot));
                master.inventory.set(inputSlot, ItemStack.EMPTY);
                return true;
            }
        }
        if(master.fuelMap.containsKey(itemStack.getItem())){
            ItemStack fuelTest = master.inventory.get(fuelSlot);
            if(fuelTest.isEmpty()){
                master.inventory.set(fuelSlot, itemStack);
                playerEntity.setHeldItem(hand, ItemStack.EMPTY);
                return true;
            }
            if(fuelTest.equals(itemStack)){
                int diff = Math.abs(itemStack.getCount() - master.inventory.get(fuelSlot).getCount());
                master.inventory.get(fuelSlot).grow(diff);
                ItemStack handItem = playerEntity.getHeldItem(hand);
                handItem.shrink(diff);
                playerEntity.setHeldItem(hand, handItem);
                return true;
            }
        }
        if(master.inventory.get(inputSlot).isEmpty()){
            master.inventory.set(inputSlot, itemStack);
            playerEntity.setHeldItem(hand, ItemStack.EMPTY);
            return true;
        }
        return false;
    }
}
