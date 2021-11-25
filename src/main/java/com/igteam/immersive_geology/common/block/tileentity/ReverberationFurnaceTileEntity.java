package com.igteam.immersive_geology.common.block.tileentity;

import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.api.utils.DirectionalBlockPos;
import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.client.utils.TextUtils;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartTileEntity;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockTileEntity;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.IEInventoryHandler;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import com.google.common.collect.ImmutableSet;
import com.igteam.immersive_geology.api.crafting.recipes.recipe.VatRecipe;
import com.igteam.immersive_geology.common.multiblocks.ChemicalVatMultiblock;
import com.igteam.immersive_geology.core.registration.IGTileTypes;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

//Sorry to IE for using their internal classes, we should have used an API, and we'll maybe fix it later.
public class ReverberationFurnaceTileEntity extends MultiblockPartTileEntity<ReverberationFurnaceTileEntity> implements IEBlockInterfaces.IPlayerInteraction, IBlockBounds, IIEInventory {

    public NonNullList<ItemStack> inventory;
    private LazyOptional<IItemHandler> insertionHandler;

    public ReverberationFurnaceTileEntity(){
        super(ChemicalVatMultiblock.INSTANCE, IGTileTypes.REV_FURNACE.get(),true);
    }

    @Override
    public void readCustomNBT(CompoundNBT nbt, boolean descPacket)
    {
        super.readCustomNBT(nbt, descPacket);
    }

    @Override
    public void writeCustomNBT(CompoundNBT nbt, boolean descPacket)
    {
        super.writeCustomNBT(nbt, descPacket);
    }


    private static final CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES =
            CachedShapesWithTransform.createForMultiblock(ReverberationFurnaceTileEntity::getShape);

    @Override
    public VoxelShape getBlockBounds(@Nullable ISelectionContext ctx)
    {
        return getShape(SHAPES);
    }


    @Override
    public Set<BlockPos> getRedstonePos()
    {
        return ImmutableSet.of(
                new BlockPos(3, 1, 2)
        );
    }

    @Override
    public NonNullList<ItemStack> getInventory()
    {
        return this.inventory;
    }

    @Override
    public boolean isStackValid(int slot, ItemStack stack)
    {
        return true;
    }

    @Override
    public int getSlotLimit(int i) {
        return 8;
    }

    private static final BlockPos outputOffset = new BlockPos(1, 0, 2);
    private static final Set<BlockPos> inputPrimary = ImmutableSet.of(
            new BlockPos(3, 0, 0)
    );

    private static final Set<BlockPos> inputSecondary = ImmutableSet.of(
            new BlockPos(3, 0, 1)
    );

    @Override
    protected IFluidTank[] getAccessibleFluidTanks(Direction side)
    {
        return null;
    }

    @Override
    protected boolean canFillTankFrom(int iTank, Direction side, FluidStack resource)
    {
        return false;
    }

    @Override
    protected boolean canDrainTankFrom(int iTank, Direction side)
    {
        return false;
    }

   @Override
    public void doGraphicalUpdates()
    {
        this.markDirty();
        this.markContainingBlockForUpdate(null);
    }

    //Direct Copy from IP's Pumpjack, this will need to be changed.
    private static List<AxisAlignedBB> getShape(BlockPos posInMultiblock){
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        //Empty space
        if (bX == 0 && bZ == 0)
        {
            if (bY == 2 || bY == 3)
            {
                return new ArrayList<>();
            }
            if (bY == 1)
            {
                return Arrays.asList(new AxisAlignedBB(0.1875, 0.0, 0.0, 1.0, 1.0, 1.0));
            }
        }
        return Arrays.asList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
    }

    @Override
    public boolean interact(Direction direction, PlayerEntity playerEntity, Hand hand, ItemStack itemStack, float v, float v1, float v2) {
        return false;
    }

    @Override
    public void tick() {

    }
}
