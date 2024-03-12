package com.igteam.immersivegeology.common.blocks.multiblocks;

import blusunrize.immersiveengineering.api.multiblocks.BlockMatcher;
import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.List;
import java.util.function.Consumer;

public abstract class IGTemplateMultiblock extends TemplateMultiblock {
    private final MultiblockRegistration<?> logic;
    public IGTemplateMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size, MultiblockRegistration<?> logic){
        super(loc, masterFromOrigin, triggerFromOrigin, size);
        this.logic = logic;
    }

    @Override
    protected void replaceStructureBlock(StructureTemplate.StructureBlockInfo info, Level world, BlockPos actualPos, boolean mirrored, Direction clickDirection, Vec3i offsetFromMaster){
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
}
