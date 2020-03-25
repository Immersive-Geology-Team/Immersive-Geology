package com.igteam.immersivegeology.common.blocks;

import blusunrize.immersiveengineering.common.items.HammerItem;
import blusunrize.immersiveengineering.common.items.WirecutterItem;
import com.google.common.base.Preconditions;
import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.common.IGContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IGBaseBlock extends Block implements IIGBlock
{
    protected static IProperty[] tempProperties;

    public final String name;
    public final IProperty[] additionalProperties;
    boolean isHidden;
    boolean hasFlavour;
    protected List<BlockRenderLayer> renderLayers = Collections.singletonList(BlockRenderLayer.SOLID);
    //TODO wtf is variable opacity?
    protected int lightOpacity;
    protected PushReaction mobilityFlag = PushReaction.NORMAL;
    protected boolean canHammerHarvest;
    protected boolean canCutterHarvest;
    protected boolean notNormalBlock;

    public IGBaseBlock(String name, Block.Properties blockProps, @Nullable Class<? extends BlockItem> itemBlock, IProperty... additionalProperties)
    {
        super(setTempProperties(blockProps, additionalProperties));
        this.name = name;

        this.additionalProperties = Arrays.copyOf(tempProperties, tempProperties.length);
        this.setDefaultState(getInitDefaultState());
        ResourceLocation registryName = createRegistryName();
        setRegistryName(registryName);

        IGContent.registeredIGBlocks.add(this);
        if(itemBlock!=null)
        {
            try
            {
                Item item = itemBlock.getConstructor(Block.class, Item.Properties.class)
                        .newInstance(this, new Item.Properties().group(ImmersiveGeology.itemGroup));
                item.setRegistryName(registryName);
                IGContent.registeredIGItems.add(item);
            } catch(Exception e)
            {
                //TODO e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        lightOpacity = 15;
    }

    //TODO do we still need this hackyness?
    protected static Block.Properties setTempProperties(Properties blockProps, Object[] additionalProperties)
    {
        List<IProperty<?>> propList = new ArrayList<>();
        for(Object o : additionalProperties)
        {
            if(o instanceof IProperty)
                propList.add((IProperty<?>)o);
            if(o instanceof IProperty[])
                propList.addAll(Arrays.asList(((IProperty<?>[])o)));
        }
        tempProperties = propList.toArray(new IProperty[0]);
        return blockProps.variableOpacity();
    }

    public IGBaseBlock setHidden(boolean shouldHide)
    {
        isHidden = shouldHide;
        return this;
    }

    public boolean isHidden()
    {
        return isHidden;
    }

    public IGBaseBlock setHasFlavour(boolean shouldHave)
    {
        hasFlavour = shouldHave;
        return this;
    }

    @Override
    public String getNameForFlavour()
    {
        return name;
    }

    @Override
    public boolean hasFlavour()
    {
        return hasFlavour;
    }

    public IGBaseBlock setBlockLayer(BlockRenderLayer... layer)
    {
        Preconditions.checkArgument(layer.length > 0);
        this.renderLayers = Arrays.asList(layer);
        return this;
    }

    @Override
    public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer)
    {
        return renderLayers.contains(layer);
    }

    public IGBaseBlock setLightOpacity(int opacity)
    {
        lightOpacity = opacity;
        return this;
    }

    @Override
    public BlockRenderLayer getRenderLayer()
    {
        //TODO This is currently mostly a marker for culling, the actual layer is determined by canRenderInLayer
        return notNormalBlock?BlockRenderLayer.CUTOUT: BlockRenderLayer.SOLID;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getOpacity(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        if(notNormalBlock)
            return 0;
            //TODO this sometimes locks up when generating IE blocks as part of worldgen
        else if(state.isOpaqueCube(worldIn, pos))
            return lightOpacity;
        else
            return state.propagatesSkylightDown(worldIn, pos)?0: 1;
    }

    public IGBaseBlock setMobility(PushReaction flag)
    {
        mobilityFlag = flag;
        return this;
    }

    @Override
    @SuppressWarnings("deprecation")
    public PushReaction getPushReaction(BlockState state)
    {
        return mobilityFlag;
    }

    public IGBaseBlock setNotNormalBlock()
    {
        notNormalBlock = true;
        return this;
    }

    /* //TODO See what this is supposed to do, getAmbientOcclusionLightValue doesn't appear to be an actual existing function
    @Override
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader world, BlockPos pos)
    {
        return notNormalBlock?1: super.getAmbientOcclusionLightValue(state, world, pos);
    } */

    @Override
    public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_)
    {
        return notNormalBlock||super.propagatesSkylightDown(p_200123_1_, p_200123_2_, p_200123_3_);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean causesSuffocation(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return !notNormalBlock;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isNormalCube(BlockState state, IBlockReader world, BlockPos pos)
    {
        return !notNormalBlock;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        super.fillStateContainer(builder);
        builder.add(tempProperties);
    }

    protected BlockState getInitDefaultState()
    {
        return this.stateContainer.getBaseState();
    }

    @SuppressWarnings("unchecked")
    protected <V extends Comparable<V>> BlockState applyProperty(BlockState in, IProperty<V> prop, Object val)
    {
        return in.with(prop, (V)val);
    }

    public void onIGBlockPlacedBy(BlockItemUseContext context, BlockState state)
    {

    }

    public boolean canIGBlockBePlaced(BlockState newState, BlockItemUseContext context)
    {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items)
    {
        items.add(new ItemStack(this, 1));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int eventID, int eventParam)
    {
        if(worldIn.isRemote&&eventID==255)
        {
            worldIn.notifyBlockUpdate(pos, state, state, 3);
            return true;
        }
        return super.eventReceived(state, worldIn, pos, eventID, eventParam);
    }

    public IGBaseBlock setHammerHarvest()
    {
        canHammerHarvest = true;
        return this;
    }

    public IGBaseBlock setWireHarvest()
    {
        canCutterHarvest = true;
        return this;
    }

    public boolean allowHammerHarvest(BlockState blockState)
    {
        return canHammerHarvest;
    }

    public boolean allowWirecutterHarvest(BlockState blockState)
    {
        return canCutterHarvest;
    }

    @Override
    public boolean isToolEffective(BlockState state, ToolType tool)
    {
        if(allowHammerHarvest(state)&&tool== HammerItem.HAMMER_TOOL)
            return true;
        if(allowWirecutterHarvest(state)&&tool== WirecutterItem.CUTTER_TOOL)
            return true;
        return super.isToolEffective(state, tool);
    }

    public ResourceLocation createRegistryName()
    {
        return new ResourceLocation(ImmersiveGeology.MODID, name);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
                                    BlockRayTraceResult hit)
    {
        ItemStack activeStack = player.getHeldItem(hand);
        if(activeStack.getToolTypes().contains(HammerItem.HAMMER_TOOL))
            return hammerUseSide(hit.getFace(), player, world, pos, hit);
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

    public boolean hammerUseSide(Direction side, PlayerEntity player, World w, BlockPos pos, BlockRayTraceResult hit)
    {
        return false;
    }

    public abstract static class IELadderBlock extends IGBaseBlock
    {
        public IELadderBlock(String name, Block.Properties material,
                             Class<? extends IGBlockItem> itemBlock, IProperty... additionalProperties)
        {
            super(name, material, itemBlock, additionalProperties);
        }

        @Override
        public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity)
        {
            return true;
        }
    }
}