package com.igteam.immersivegeology.common.block.multiblock.chemicalvat;

import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockBlockEntity;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import com.google.common.collect.ImmutableSet;
import com.igteam.immersivegeology.api.crafting.ChemicalVatRecipe;
import com.igteam.immersivegeology.common.block.helper.IGCommonTickableTile;
import com.igteam.immersivegeology.common.block.multiblock.AABBUtils;
import com.igteam.immersivegeology.common.block.multiblock.coredrill.CoreDrillTileEntity;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChemicalVatTileEntity extends PoweredMultiblockBlockEntity<ChemicalVatTileEntity, ChemicalVatRecipe> implements IGCommonTickableTile, IEBlockInterfaces.IBlockBounds{

    public ChemicalVatTileEntity(BlockEntityType<? extends ChemicalVatTileEntity> type, BlockPos worldPos, BlockState blockState)
    {
        super(ChemicalVatMultiblock.INSTANCE, 0, true, type, worldPos, blockState);
    }

    @Nullable
    @Override
    protected ChemicalVatRecipe getRecipeForId(Level level, ResourceLocation resourceLocation) {
        return null;
    }

    @Override
    public Set<MultiblockFace> getEnergyPos() {
        return Set.of();
    }

    @Override
    public Set<BlockPos> getRedstonePos() {
        return ImmutableSet.of(
                new BlockPos(3, 1, 2)
        );
    }

    @Nullable
    @Override
    public IFluidTank[] getInternalTanks() {
        return new IFluidTank[0];
    }

    @Nullable
    @Override
    public ChemicalVatRecipe findRecipeForInsertion(ItemStack itemStack) {
        return null;
    }

    @Nullable
    @Override
    public int[] getOutputSlots() {
        return new int[0];
    }

    @Nullable
    @Override
    public int[] getOutputTanks() {
        return new int[0];
    }

    @Override
    public boolean additionalCanProcessCheck(MultiblockProcess<ChemicalVatRecipe> multiblockProcess) {
        return false;
    }

    @Override
    public void doProcessOutput(ItemStack itemStack) {

    }

    @Override
    public void doProcessFluidOutput(FluidStack fluidStack) {

    }

    @Override
    public void onProcessFinish(MultiblockProcess<ChemicalVatRecipe> multiblockProcess) {

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
    public float getMinProcessDistance(MultiblockProcess<ChemicalVatRecipe> multiblockProcess) {
        return 0;
    }

    @Override
    public boolean isInWorldProcessingMachine() {
        return false;
    }

    @Nullable
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

    @NotNull
    @Override
    public VoxelShape getBlockBounds(@Nullable CollisionContext collisionContext) {
        return SHAPES.get(this.posInMultiblock, Pair.of(getFacing(), getIsMirrored()));
    }

    private static final CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES = CachedShapesWithTransform.createForMultiblock(ChemicalVatTileEntity::getShape);
    private static List<AABB> getShape(BlockPos posInMultiblock){
        final int x = posInMultiblock.getX();
        final int y = posInMultiblock.getY();
        final int z = posInMultiblock.getZ();

        List<AABB> main = new ArrayList<>();

        // Use default cube shape for now.
        main.add(AABBUtils.FULL);
        return main;
    }

    @Override
    public void tickClient() {

    }
}
