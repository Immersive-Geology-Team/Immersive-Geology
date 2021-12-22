package com.igteam.immersive_geology.common.block.tileentity;

import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import com.google.common.collect.ImmutableSet;
import com.igteam.immersive_geology.api.crafting.recipes.recipe.CrystalRecipe;
import com.igteam.immersive_geology.common.multiblocks.CrystallizerMultiblock;
import com.igteam.immersive_geology.common.multiblocks.RotaryKilnMultiblock;
import com.igteam.immersive_geology.core.registration.IGTileTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

//Sorry to IE for using their internal classes, we should have used an API, and we'll maybe fix it later.
//I swear's I'll use the API after Alpha release ~Muddykat
public class RotaryKilnTileEntity extends PoweredMultiblockTileEntity<RotaryKilnTileEntity, CrystalRecipe> implements IEBlockInterfaces.IBlockOverlayText, IEBlockInterfaces.IPlayerInteraction, IBlockBounds, IIEInventory {

    public RotaryKilnTileEntity() {
        super(RotaryKilnMultiblock.INSTANCE, 42000, true, IGTileTypes.ROTARYKILN.get());
    }

    private static final CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES =
            CachedShapesWithTransform.createForMultiblock(RotaryKilnTileEntity::getShape);

    @Nonnull
    @Override
    public VoxelShape getBlockBounds( ISelectionContext iSelectionContext) {
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
        return null;
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
        return new IFluidTank[0];
    }

    @Override
    public CrystalRecipe findRecipeForInsertion(ItemStack itemStack) {
        return null;
    }

    @Override
    public int[] getOutputSlots() {
        return new int[0];
    }

    @Override
    public int[] getOutputTanks() {
        return new int[0];
    }

    @Override
    public boolean additionalCanProcessCheck(MultiblockProcess<CrystalRecipe> multiblockProcess) {
        return false;
    }

    @Override
    public void doProcessOutput(ItemStack itemStack) {

    }

    @Override
    public void doProcessFluidOutput(FluidStack fluidStack) {

    }

    @Override
    public void onProcessFinish(MultiblockProcess<CrystalRecipe> multiblockProcess) {

    }

    @Override
    public int getMaxProcessPerTick() {
        return 0;
    }

    @Override
    public int getProcessQueueMaxLength() {
        return 0;
    }

    @Override
    public float getMinProcessDistance(MultiblockProcess<CrystalRecipe> multiblockProcess) {
        return 0;
    }

    @Override
    public boolean isInWorldProcessingMachine() {
        return false;
    }

    @Nonnull
    @Override
    protected IFluidTank[] getAccessibleFluidTanks(Direction direction) {
        return new IFluidTank[0];
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
    public NonNullList<ItemStack> getInventory() {
        return null;
    }

    @Override
    public boolean isStackValid(int i, ItemStack itemStack) {
        return false;
    }

    @Override
    public int getSlotLimit(int i) {
        return 0;
    }

    @Override
    public void doGraphicalUpdates() {

    }

    private static List<AxisAlignedBB> getShape(BlockPos posInMultiblock){
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        if (bY == 0)
        {
            if (bZ == 0 || bZ == 2) {
                if (bX % 2 == 0) {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));
                }
                if (bX == 7 && bZ == 2)
                {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
                            new AxisAlignedBB(0.0, 0.5, 0.5, 1.0, 1.0, 0.0));

                }
                if (bX == 7 && bZ == 0)
                {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
                            new AxisAlignedBB(0.0, 0.5, 0.5, 1.0, 1.0, 1.0));
                }
            }
        }
        if (bY == 2) {
            if (bZ ==1) {
                if (bX == 7){
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));
                }
                if (bX==2)        {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.75, 1.0));
                }
                if (bX==3)        {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.625, 1.0));
                }
                if (bX==4)        {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0));
                }
                if (bX==5)        {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.375, 1.0));
                }

            }
            if (bZ ==0 && bX == 7)
            {
                return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.5, 1.0, 0.5, 1.0));
            }
            if (bZ ==2 && bX == 7)
            {
                return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 0.5));
            }
        }
        if (bY ==1) {
            if (bX == 7)
            {
                if (bZ == 0)
                {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.5, 1.0, 1.0, 1.0));
                }
                if (bZ == 2)
                {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.5));
                }
            }
            if (bX ==5){
                if (bZ == 2)
                {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 1.0, 1.0, 1.0, 0.5));
                }
                if (bZ == 0)
                {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 0.9375),
                            new AxisAlignedBB(0.0, 0.5, 0.9375, 1.0, 1.0, 0.5));
                }
            }

            if (bX ==3){
                if (bZ == 0)
                {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.9375));
                }
                if (bZ == 2)
                {
                    return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0625, 1.0, 0.5, 1.0),
                            new AxisAlignedBB(0.0, 0.5, 0.5, 1.0, 1.0, 0.0625));
                }
            }

            if (bX == 0)
            {
                return Arrays.asList(new AxisAlignedBB(0.0, 0.5, 0.0, 1.0, 1.0, 1.0),
                        new AxisAlignedBB(0.5, 0.5, 0.0, 1.0, 0.0, 1.0));
            }
        }

        return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
    }
}
