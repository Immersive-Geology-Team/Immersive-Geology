package com.igteam.immersivegeology.common.block.multiblock.crystallizer;

import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockBlockEntity;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.util.orientation.RelativeBlockFace;
import com.google.common.collect.ImmutableSet;
import com.igteam.immersivegeology.common.block.helper.IGCommonTickableTile;
import com.igteam.immersivegeology.api.crafting.CrystallizerRecipe;
import com.igteam.immersivegeology.common.block.multiblock.AABBUtils;
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
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CrystallizerTileEntity extends PoweredMultiblockBlockEntity<CrystallizerTileEntity, CrystallizerRecipe> implements IGCommonTickableTile, IEBlockInterfaces.IBlockBounds {
    public CrystallizerTileEntity(BlockEntityType<? extends CrystallizerTileEntity> type, BlockPos pWorldPosition, BlockState pBlockState) {
        super(CrystallizerMultiblock.INSTANCE, 12000, true, type, pWorldPosition, pBlockState);
    }

    @Nullable
    @Override
    protected CrystallizerRecipe getRecipeForId(Level level, ResourceLocation resourceLocation) {
        return null;
    }

    @Override
    public Set<MultiblockFace> getEnergyPos() {
        return ImmutableSet.of(new MultiblockFace(1,2,1, RelativeBlockFace.UP));
    }

    @Override
    public Set<BlockPos> getRedstonePos() {
        return ImmutableSet.of(new BlockPos(2,1,1));
    }

    @Nullable
    @Override
    public IFluidTank[] getInternalTanks() {
        return new IFluidTank[0];
    }

    @Nullable
    @Override
    public CrystallizerRecipe findRecipeForInsertion(ItemStack itemStack) {
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
    public boolean additionalCanProcessCheck(MultiblockProcess<CrystallizerRecipe> multiblockProcess) {
        return false;
    }

    @Override
    public void doProcessOutput(ItemStack itemStack) {

    }

    @Override
    public void doProcessFluidOutput(FluidStack fluidStack) {

    }

    @Override
    public void onProcessFinish(MultiblockProcess<CrystallizerRecipe> multiblockProcess) {

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
    public float getMinProcessDistance(MultiblockProcess<CrystallizerRecipe> multiblockProcess) {
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

    private static final CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES = CachedShapesWithTransform.createForMultiblock(CrystallizerTileEntity::getShape);
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
