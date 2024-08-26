package com.igteam.immersivegeology.common.block.multiblocks;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityDummy;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockPartBlock;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import blusunrize.immersiveengineering.common.util.IELogger;
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


public abstract class IGTemplateMultiblock  extends IETemplateMultiblock{
    private final MultiblockRegistration<?> logic;

    public IGTemplateMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size, MultiblockRegistration<?> logic){
        super(loc, masterFromOrigin, triggerFromOrigin, size, logic);
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
    public Block getBaseBlock(){
        return getBlock();
    }
}
