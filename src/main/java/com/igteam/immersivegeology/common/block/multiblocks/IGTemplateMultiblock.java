/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityDummy;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockPartBlock;
import blusunrize.immersiveengineering.common.register.IEItems;
import blusunrize.immersiveengineering.common.register.IEItems.Tools;
import blusunrize.immersiveengineering.common.util.IELogger;
import com.google.common.base.Preconditions;
import com.igteam.immersivegeology.common.block.helper.IGConfigurableMachine;
import com.igteam.immersivegeology.common.item.IGMBFormationItem;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public abstract class IGTemplateMultiblock extends TemplateMultiblock implements IGConfigurableMachine
{
    private final MultiblockRegistration<?> logic;
    private int formTime = 0;

    public IGTemplateMultiblock(ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size, MultiblockRegistration<?> logic){
        super(loc, masterFromOrigin, triggerFromOrigin, size);
        this.logic = logic;
    }

    @Override
    public boolean createStructure(Level world, BlockPos pos, Direction side, Player player)
    {
        if(player.getMainHandItem().getItem() instanceof IGMBFormationItem || canFormWithDefaultHammer())
        {
            return super.createStructure(world, pos, side, player);
        }
        if(IGMBFormationItem.confirmMBStructure(this, world, pos, side, player)) player.displayClientMessage(Component.translatable("immersivegeology.multiblock.formation.failed"), true);
        return false;
    }

    public boolean canFormWithDefaultHammer()
    {
        return false;
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

    @Override
    public int getDefaultBatchInput()
    {
        return 1;
    }

    @Override
    public int getDefaultBatchOutput()
    {
        return 1;
    };

    @Override
    public int getDefaultTime()
    {
        return 100;
    };

    @Override
    public int getDefaultEnergy()
    {
        return 100;
    };

    public ItemStack getFormationItem()
    {
        IGMBFormationItem stoneHammer = (IGMBFormationItem) StoneEnum.MCStone.getItem(ItemCategoryFlags.HAMMER);
        IGMBFormationItem bronzeHammer = (IGMBFormationItem) MetalEnum.Bronze.getItem(ItemCategoryFlags.HAMMER);
        IGMBFormationItem stainlessSteelHammer = (IGMBFormationItem) MetalEnum.StainlessSteel.getItem(ItemCategoryFlags.HAMMER);
        boolean canFormStone = stoneHammer.canFormMB(StoneEnum.MCStone, this);
        boolean canFormBronze = bronzeHammer.canFormMB(MetalEnum.Bronze, this);
        boolean canFormStainlessSteel = stainlessSteelHammer.canFormMB(MetalEnum.StainlessSteel, this);
        if(canFormStone) return StoneEnum.MCStone.getStack(ItemCategoryFlags.HAMMER);
        if(canFormBronze) return MetalEnum.Bronze.getStack(ItemCategoryFlags.HAMMER);
        if(canFormStainlessSteel) return MetalEnum.StainlessSteel.getStack(ItemCategoryFlags.HAMMER);
        return new ItemStack(Tools.HAMMER);
    }
}
