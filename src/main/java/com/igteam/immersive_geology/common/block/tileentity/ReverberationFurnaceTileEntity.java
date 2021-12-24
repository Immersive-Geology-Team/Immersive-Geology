package com.igteam.immersive_geology.common.block.tileentity;

import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartTileEntity;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import blusunrize.immersiveengineering.common.util.inventory.MultiFluidTank;
import com.google.common.collect.ImmutableSet;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.crafting.recipes.recipe.ReverberationRecipe;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.fluid.FluidEnum;
import com.igteam.immersive_geology.common.multiblocks.ReverberationFurnaceMultiblock;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
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
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

//Sorry to IE for using their internal classes, we should have used an API, and we'll maybe fix it later.
public class ReverberationFurnaceTileEntity extends PoweredMultiblockTileEntity<ReverberationFurnaceTileEntity, ReverberationRecipe> implements IIEInventory, IEBlockInterfaces.IActiveState, IEBlockInterfaces.IBlockOverlayText, IEBlockInterfaces.IPlayerInteraction, IEBlockInterfaces.IInteractionObjectIE, IEBlockInterfaces.IProcessTile, IBlockBounds {
    private static CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES = CachedShapesWithTransform.createForMultiblock(ReverberationFurnaceTileEntity::getShape);

    private Logger log = ImmersiveGeology.getNewLogger();

    protected FluidTank gasTank;
    protected NonNullList<ItemStack> inventory;
    public static HashMap<Item, Integer> fuelMap = new HashMap<>();

    private int burntime[] = new int[2];
    private int maxBurntime = 100;

    public int FUEL_SLOT1 = 0, FUEL_SLOT2 = 1;
    public int OUTPUT_SLOT1 = 2, OUTPUT_SLOT2 = 3;
    public int INPUT_SLOT1 = 4, INPUT_SLOT2 = 5;

    public ReverberationFurnaceTileEntity() {
        super(ReverberationFurnaceMultiblock.INSTANCE,0,true, IGTileTypes.REV_FURNACE.get());
        this.inventory = NonNullList.withSize(6, ItemStack.EMPTY);
        burntime[0] = 0;
        burntime[1] = 0;
        gasTank = new FluidTank(1000);
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

    private static final Set<BlockPos> redStonePos = ImmutableSet.of(
            new BlockPos(1, 0, 0)
    );

    @Override
    public Set<BlockPos> getRedstonePos() {
        return redStonePos;
    }

    @Override
    public void tick() {
        ReverberationFurnaceTileEntity master = (ReverberationFurnaceTileEntity) this.master();
        assert master != null;

        checkForNeedlessTicking();

        if(world.isRemote || isDummy())
            return;

        super.tick();

        if(formed){
            for(int offset = 0; offset < 2; offset++) {
                if(!isDummy()) {
                    if (master.isBurning(FUEL_SLOT1 + offset)) {
                        master.burntime[offset] = master.burntime[offset] - 1;
                    } else if (hasFuel(FUEL_SLOT1 + offset)) {
                        master.burntime[offset] += fuelMap.get(master.getInventory().get(FUEL_SLOT1 + offset).getItem());
                        master.getInventory().get(FUEL_SLOT1 + offset).shrink(1);
                    }
                }

                ItemStack inputItem = master.inventory.get(INPUT_SLOT1 + offset);
                ReverberationRecipe recipe = ReverberationRecipe.findRecipe(inputItem);
                if(recipe != null) {
                    recipe.setSlotOffset(offset);
                    MultiblockProcessInMachine<ReverberationRecipe> process = new MultiblockProcessInMachine<ReverberationRecipe>(recipe, INPUT_SLOT1 + offset);
                    process.setInputAmounts(recipe.input.getCount());

                    if(master.addProcessToQueue(process, true, false))
                    {
                        master.addProcessToQueue(process, false, false);
                    }
                }
            }
        }
    }

    @Override
    public void readCustomNBT(CompoundNBT nbt, boolean descPacket)
    {
        super.readCustomNBT(nbt, descPacket);
        inventory = Utils.readInventory(nbt.getList("inventory", 10), 6);
    }

    @Override
    public void writeCustomNBT(CompoundNBT nbt, boolean descPacket)
    {
        super.writeCustomNBT(nbt, descPacket);
        nbt.put("inventory", Utils.writeInventory(inventory));
    }

    @Nullable
    @Override
    public IFluidTank[] getInternalTanks() {
        return new IFluidTank[]{gasTank};
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
        return new int[]{0};
    }

    @Override
    public boolean additionalCanProcessCheck(MultiblockProcess multiblockProcess) {
        if (multiblockProcess.recipe instanceof ReverberationRecipe) {
            ReverberationRecipe r = (ReverberationRecipe) multiblockProcess.recipe;
            return (processQueue.get(r.getSlotOffset()).recipe.getId().equals(multiblockProcess.recipe.getId()) && isBurning(r.getSlotOffset()));
        }
        return false;
    }

    @Override
    public void doProcessOutput(ItemStack itemStack) {

    }

    @Override
    public void doProcessFluidOutput(FluidStack fluidStack) {

    }

    @Override
    public void onProcessFinish(MultiblockProcess multiblockProcess) {
        if(multiblockProcess.recipe instanceof ReverberationRecipe){
            ReverberationRecipe r = (ReverberationRecipe) multiblockProcess.recipe;
            int slotOffset = r.getSlotOffset();
            ReverberationFurnaceTileEntity master = this.master();
            if(master.getInventory() != null && !master.getInventory().isEmpty()) {
                master.getInventory().get(INPUT_SLOT1 + slotOffset).shrink(r.input.getCount());
            }
            if(gasTank.getFluidAmount() < gasTank.getCapacity()){
                gasTank.fill(new FluidStack(IGRegistrationHolder.getFluidByMaterial(MaterialEnum.Sulfuric.getMaterial(),false), Math.round(50 * r.getWasteMultipler())), IFluidHandler.FluidAction.EXECUTE);
            }
        }
    }

    @Override
    public int getMaxProcessPerTick() {
        return 1;
    }

    @Override
    public int getProcessQueueMaxLength() {
        return 256;
    }

    @Override
    public float getMinProcessDistance(MultiblockProcess multiblockProcess) {
        return 0;
    }

    @Override
    public boolean isInWorldProcessingMachine() {
        return true;
    }

    public boolean isBurning(int slot){
        ReverberationFurnaceTileEntity master = (ReverberationFurnaceTileEntity) this.master();
        return master.burntime[slot] > 0;
    }

    public boolean hasFuel(int slot){
        ReverberationFurnaceTileEntity master = (ReverberationFurnaceTileEntity) this.master();
        return fuelMap.containsKey(master.inventory.get(slot).getItem());
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

    private static final BlockPos gasOutputs = new BlockPos(1, 12, 1);

    @Override
    protected boolean canDrainTankFrom(int i, Direction side) {
        return gasOutputs.equals(posInMultiblock)&&(side==null||side==getFacing().getOpposite()); //TODO this seems to always be true? for some reason? ~Muddykat
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
        return true;
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

    @Nullable
    @Override
    public ITextComponent[] getOverlayText(PlayerEntity playerEntity, RayTraceResult rayTraceResult, boolean b) {
        ReverberationFurnaceTileEntity master = (ReverberationFurnaceTileEntity) this.master();

        ArrayList<StringTextComponent> info = new ArrayList<>();
        for(int offset = 0; offset < 2; offset++) {
            String FuelName = master.getInventory().get(FUEL_SLOT1 + offset).getDisplayName().getString();
            String InputName = master.getInventory().get(INPUT_SLOT1 + offset).getDisplayName().getString();
            String OutputName = master.getInventory().get(OUTPUT_SLOT1 + offset).getDisplayName().getString();

            StringTextComponent FuelNames = new StringTextComponent("Fuel Slot[" + offset + "]: " + FuelName + " x" + master.getInventory().get(FUEL_SLOT1 + offset).getCount());
            StringTextComponent InputNames = new StringTextComponent("Input Slot[" + offset + "]: " + InputName + " x" +  master.getInventory().get(INPUT_SLOT1 + offset).getCount());
            StringTextComponent OutputNames = new StringTextComponent("Output Slot[" + offset + "]: " + OutputName + " x" +  master.getInventory().get(OUTPUT_SLOT1 + offset).getCount());

            info.add(FuelNames);
            info.add(InputNames);
            info.add(OutputNames);
        }

        String TankOutputName  = master.getInternalTanks()[0].getFluid().getDisplayName().getString();

        info.add(new StringTextComponent("Gas Output: " + TankOutputName + " x" + master.getInternalTanks()[0].getFluidAmount()));
        info.add(new StringTextComponent("Burn Time[1]: " + master.burntime[FUEL_SLOT1]));
        info.add(new StringTextComponent("Burn Time[2]: " + master.burntime[FUEL_SLOT2]));

        return info.toArray(new ITextComponent[info.size()]);
    }

    @Override
    public boolean useNixieFont(PlayerEntity playerEntity, RayTraceResult rayTraceResult) {
        return false;
    }

    @Override
    public boolean interact(Direction direction, PlayerEntity playerEntity, Hand hand, ItemStack itemStack, float v, float v1, float v2) {
        ReverberationFurnaceTileEntity master = (ReverberationFurnaceTileEntity) this.master();
        if(master != null) {
            if (fuelMap.containsKey(itemStack.getItem())) {
                if (master.getInventory().get(FUEL_SLOT1).isEmpty()) {
                    master.inventory.set(FUEL_SLOT1, itemStack.copy());
                    itemStack.shrink(itemStack.getCount());
                    return true;
                }
            }

            if (master.getInventory().get(INPUT_SLOT1).isEmpty()) {
                master.inventory.set(INPUT_SLOT1, itemStack.copy());
                itemStack.shrink(itemStack.getCount());
                return true;
            }

            if(itemStack.isEmpty()){
                if(!master.getInventory().get(master.OUTPUT_SLOT1).isEmpty()){
                    playerEntity.setHeldItem(hand, master.getInventory().get(master.OUTPUT_SLOT1).copy());
                    master.getInventory().set(master.OUTPUT_SLOT1, ItemStack.EMPTY);
                }
            }
        }

        return false;
    }
}