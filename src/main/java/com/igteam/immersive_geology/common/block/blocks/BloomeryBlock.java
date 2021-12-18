package com.igteam.immersive_geology.common.block.blocks;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.BlockItemIE;
import blusunrize.immersiveengineering.common.blocks.IETileProviderBlock;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.common.block.helpers.BlockMaterialType;
import com.igteam.immersive_geology.common.block.helpers.IGBlockType;
import com.igteam.immersive_geology.common.block.tileentity.BloomeryTileEntity;
import com.igteam.immersive_geology.common.item.IGBlockItem;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import com.igteam.immersive_geology.core.registration.IGTileTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntityType;

import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.function.Supplier;

public class BloomeryBlock extends IETileProviderBlock<BloomeryTileEntity> implements IGBlockType {

    protected final String holder_name;
    protected final Item itemBlock;
    public BloomeryBlock() {
        super("bloomery", () -> IGTileTypes.BLOOMERY.get(), Properties.create(Material.ROCK).sound(SoundType.STONE));
        ImmersiveGeology.getNewLogger().info("Setting up Bloomery");

        IEContent.registeredIEBlocks.remove(this);
        Iterator<Item> it = IEContent.registeredIEItems.iterator();
        while(it.hasNext()){
            Item item = it.next();
            if(item instanceof BlockItemIE && ((BlockItemIE) item).getBlock() == this){
                it.remove();
                break;
            }
        }

        holder_name = IGRegistrationHolder.getRegistryKey(MaterialEnum.Vanilla.getMaterial(), MaterialUseType.MACHINE).toLowerCase();
        IGRegistrationHolder.registeredIGBlocks.put(holder_name + "_" + "bloomery", this);

        this.itemBlock = new IGBlockItem(this, this,  MaterialUseType.MACHINE.getSubgroup(), MaterialEnum.Vanilla.getMaterial());
        itemBlock.setRegistryName(IGLib.MODID + ":" + holder_name + "_" + "bloomery");
        IGRegistrationHolder.registeredIGItems.put(holder_name + "_" + "bloomery", itemBlock);
    }

    @Override
    public ResourceLocation createRegistryName(){
        return new ResourceLocation(IGLib.MODID, IGRegistrationHolder.getRegistryKey(MaterialEnum.Vanilla.getMaterial(), MaterialUseType.MACHINE).toLowerCase() + "_" + "bloomery");
    }

    @Override
    public Item asItem() {
        return itemBlock;
    }

    @Override
    public String getHolderName() {
        return holder_name;
    }

    @Override
    public MaterialUseType getBlockUseType() {
        return MaterialUseType.MACHINE;
    }

    @Override
    public com.igteam.immersive_geology.api.materials.Material getMaterial(BlockMaterialType type) {
        return MaterialEnum.Vanilla.getMaterial();
    }

    @Override
    public MaterialUseType getDropUseType() {
        return MaterialUseType.MACHINE;
    }

    @Override
    public float maxDrops() {
        return 1;
    }

    @Override
    public float minDrops() {
        return 1;
    }

    @Override
    public boolean canIEBlockBePlaced(BlockState newState, BlockItemUseContext context)
    {
        BlockPos start = context.getPos();
        World w = context.getWorld();
        return areAllReplaceable(start, start.up(), context);
    }

    @Override
    public StateContainer<Block, BlockState> getStateContainer() {
        return super.getStateContainer();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(IEProperties.FACING_HORIZONTAL, IEProperties.MULTIBLOCKSLAVE);
    }

    @Override
    protected BlockState getInitDefaultState() {
        BlockState state = this.stateContainer.getBaseState();
        return state.with(IEProperties.FACING_HORIZONTAL, getDefaultFacing()).getBlockState();
    }
}
