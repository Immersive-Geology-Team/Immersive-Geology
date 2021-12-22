package com.igteam.immersive_geology.common.block.tileentity;

import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartTileEntity;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import blusunrize.immersiveengineering.common.util.inventory.MultiFluidTank;
import com.igteam.immersive_geology.api.crafting.recipes.recipe.ReverberationRecipe;
import com.igteam.immersive_geology.common.multiblocks.ReverberationFurnaceMultiblock;
import com.igteam.immersive_geology.core.registration.IGTileTypes;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.TileEntityType;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

//Sorry to IE for using their internal classes, we should have used an API, and we'll maybe fix it later.
public class ReverberationFurnaceTileEntity extends PoweredMultiblockTileEntity<ReverberationFurnaceTileEntity, ReverberationRecipe> implements IIEInventory, IEBlockInterfaces.IActiveState, IEBlockInterfaces.IInteractionObjectIE, IEBlockInterfaces.IProcessTile, IBlockBounds {
    private static CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES = CachedShapesWithTransform.createForMultiblock(ReverberationFurnaceTileEntity::getShape);

    protected FluidTank gasTank = new FluidTank(1000);
    protected NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
    public static HashMap<Item, Integer> fuelMap = new HashMap<>();

    private float burntime = 0;
    private float maxBurntime = 100;

    public int FUEL_SLOT = 0;
    public int OUTPUT_SLOT1 = 1, OUTPUT_SLOT2 = 2;
    public int INPUT_SLOT1 = 3, INPUT_SLOT2 = 4;

    public ReverberationFurnaceTileEntity() {
        super(ReverberationFurnaceMultiblock.INSTANCE,0,false, IGTileTypes.REV_FURNACE.get());
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

    @Nullable
    @Override
    protected ReverberationRecipe getRecipeForId(ResourceLocation id) {
        return ReverberationRecipe.recipes.get(id);
    }

    @Override
    public Set<BlockPos> getEnergyPos() {
        return null;
    }

    @Override
    public void tick() {
        checkForNeedlessTicking();

        if(world.isRemote||isDummy())
            return;

        ReverberationFurnaceTileEntity master = (ReverberationFurnaceTileEntity) this.master();
        assert master != null;

        if(formed){
            if(!processQueue.isEmpty()) {
                if (isBurning()) {
                    burntime--;
                } else {
                    if (hasFuel()) {
                        burntime = maxBurntime;
                        getInventory().get(FUEL_SLOT).shrink(1);
                    }
                }
            }

            for(int offset = 0; offset < 2; offset++) {
                ItemStack inputItem = inventory.get(INPUT_SLOT1 + offset);
                ReverberationRecipe recipe = ReverberationRecipe.findRecipe(inputItem);
                if(recipe != null) {
                    MultiblockProcessInMachine<ReverberationRecipe> process = new MultiblockProcessInMachine<ReverberationRecipe>(recipe, INPUT_SLOT1 + offset);

                    if(master.addProcessToQueue(process, true, true))
                    {
                        master.addProcessToQueue(process, false, true);
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public IFluidTank[] getInternalTanks() {
        return new IFluidTank[0];
    }

    @Nullable
    @Override
    public ReverberationRecipe findRecipeForInsertion(ItemStack itemStack) {
        return ReverberationRecipe.findRecipe(itemStack);
    }

    @Nullable
    @Override
    public int[] getOutputSlots() {
        return new int[]{OUTPUT_SLOT1, OUTPUT_SLOT2};
    }

    @Nullable
    @Override
    public int[] getOutputTanks() {
        return new int[0];
    }

    @Override
    public boolean additionalCanProcessCheck(MultiblockProcess multiblockProcess) {
        return isBurning();
    }

    @Override
    public void doProcessOutput(ItemStack itemStack) {

    }

    @Override
    public void doProcessFluidOutput(FluidStack fluidStack) {

    }

    @Override
    public void onProcessFinish(MultiblockProcess multiblockProcess) {

    }

    @Override
    public int getMaxProcessPerTick() {
        return 2;
    }

    @Override
    public int getProcessQueueMaxLength() {
        return 2;
    }

    @Override
    public float getMinProcessDistance(MultiblockProcess multiblockProcess) {
        return 0;
    }

    @Override
    public boolean isInWorldProcessingMachine() {
        return true;
    }

    public boolean isBurning(){
        return burntime > 0;
    }

    public boolean hasFuel(){
        return fuelMap.containsKey(inventory.get(FUEL_SLOT).getItem());
    }

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

    @Nonnull
    @Override
    protected IFluidTank[] getAccessibleFluidTanks(Direction direction) {
        return new IFluidTank[]{gasTank};
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
    public int[] getCurrentProcessesStep() {
        return new int[0];
    }

    @Override
    public int[] getCurrentProcessesMax() {
        return new int[0];
    }

    @Nullable
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
        return 64;
    }

    @Override
    public void doGraphicalUpdates() {

    }

    @Override
    public TileEntityType<?> getType() {
        return IGTileTypes.REV_FURNACE.get();
    }
}