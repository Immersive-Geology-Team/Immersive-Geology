package com.igteam.immersivegeology.common.block.multiblocks;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks.MultiblockManualData;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityDummy;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockPartBlock;
import blusunrize.immersiveengineering.client.utils.BasicClientProperties;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import blusunrize.immersiveengineering.common.util.IELogger;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;


public abstract class IGTemplateMultiblock extends TemplateMultiblock
{
    private final MultiblockRegistration<?> logic;

    public IGTemplateMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size, MultiblockRegistration<?> logic){
        super(loc, masterFromOrigin, triggerFromOrigin, size);
        this.logic = logic;
    }

    @Override
    protected void replaceStructureBlock(StructureTemplate.StructureBlockInfo info, Level world, BlockPos actualPos, boolean mirrored, Direction clickDirection, Vec3i offsetFromMaster){
        BlockState newState = ((MultiblockPartBlock<?>)this.logic.block().get()).defaultBlockState();
        newState = (BlockState)newState.setValue(IEProperties.MULTIBLOCKSLAVE, !offsetFromMaster.equals(Vec3i.ZERO));
        if (newState.hasProperty(IEProperties.MIRRORED)) {
            newState = (BlockState)newState.setValue(IEProperties.MIRRORED, mirrored);
        }

        if (newState.hasProperty(IEProperties.FACING_HORIZONTAL)) {
            newState = (BlockState)newState.setValue(IEProperties.FACING_HORIZONTAL, clickDirection.getOpposite());
        }

        BlockState oldState = world.getBlockState(actualPos);
        world.setBlock(actualPos, newState, 0);
        BlockEntity curr = world.getBlockEntity(actualPos);
        if (curr instanceof MultiblockBlockEntityDummy<?> dummy) {
            dummy.getHelper().setPositionInMB(info.pos());
        } else if (!(curr instanceof MultiblockBlockEntityMaster)) {
            IELogger.logger.error("Expected MB TE at {} during placement", actualPos);
        }

        LevelChunk chunk = world.getChunkAt(actualPos);
        world.markAndNotifyBlock(actualPos, chunk, oldState, newState, 3, 512);
    }

    public ResourceLocation getBlockName(){
        return this.logic.id();
    }

    @Override
    public Component getDisplayName(){
        return this.logic.block().get().getName();
    }

    @Override
    public Block getBlock(){
        return this.logic.block().get();
    }

    /**
     * @deprecated Replaced by {@link #getBlock()}
     * @return
     */
    @Deprecated
    public Block getBaseBlock(){
        return getBlock();
    }

    public Vec3i getSize(@Nullable Level world) {
        return this.size;
    }

    @Nonnull
    public TemplateMultiblock.TemplateData getTemplate(@Nonnull Level world) {
        TemplateMultiblock.TemplateData result = super.getTemplate(world);
        Vec3i resultSize = result.template().getSize();
        Preconditions.checkState(resultSize.equals(this.size), "Wrong template size for multiblock %s, template size: %s", this.getTemplateLocation(), resultSize);
        return result;
    }

    protected void prepareBlockForDisassembly(Level world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof IMultiblockBE<?> multiblockBE) {
            multiblockBE.getHelper().markDisassembling();
        } else if (be != null) {
            IELogger.logger.error("Expected multiblock TE at {}, got {}", pos, be);
        }

    }
}
