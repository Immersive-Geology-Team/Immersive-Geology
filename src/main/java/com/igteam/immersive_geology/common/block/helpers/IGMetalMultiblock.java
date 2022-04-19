package com.igteam.immersive_geology.common.block.helpers;

import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.BlockItemIE;
import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartTileEntity;
import blusunrize.immersiveengineering.common.blocks.metal.MetalMultiblockBlock;
import com.igteam.immersive_geology.common.item.IGGenericBlockItem;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import igteam.immersive_geology.block.IGBlockType;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.main.IGRegistryProvider;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.materials.pattern.MiscPattern;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Supplier;

public class IGMetalMultiblock<T extends MultiblockPartTileEntity<T>> extends MetalMultiblockBlock<T> implements IGBlockType {

    protected final IGGenericBlockItem itemBlock;
    public IGMetalMultiblock(String name, Supplier<TileEntityType<T>> te){
        super(name, te);

        IEContent.registeredIEBlocks.remove(this);
        Iterator<Item> it = IEContent.registeredIEItems.iterator();
        while(it.hasNext()){
            Item item = it.next();
            if(item instanceof BlockItemIE && ((BlockItemIE) item).getBlock() == this){
                it.remove();
                break;
            }
        }

        this.itemBlock = new IGGenericBlockItem(this, MetalEnum.Iron, ItemPattern.block_item);
        this.itemBlock.setRegistryName(new ResourceLocation(IGLib.MODID, ItemPattern.block_item.getName() + "_" + getHolderKey()));

        IGRegistryProvider.IG_BLOCK_REGISTRY.putIfAbsent(IGRegistrationHolder.getRegistryKey(this), this);
        IGRegistryProvider.IG_ITEM_REGISTRY.putIfAbsent(new ResourceLocation(IGLib.MODID, ItemPattern.block_item.getName() + "_" + getHolderKey()), itemBlock);
    }

    @Override
    public Item asItem() {
        return this.itemBlock;
    }

    @Override
    public ResourceLocation createRegistryName(){
        return IGRegistrationHolder.getRegistryKey(this);
    }

    @Override
    public int getColourForIGBlock(int pass) {
        return 0;
    }

    @Override
    public Collection<MaterialInterface> getMaterials() {
        return Collections.singleton(MetalEnum.Iron);
    }

    @Override
    public MaterialPattern getPattern() {
        return MiscPattern.machine;
    }

    @Override
    public String getHolderKey() {
        StringBuilder data = new StringBuilder();

        data.append("_").append(MetalEnum.Iron.getName());

        return getPattern() + data.toString() + "_" + name;
    }

    @Override
    public Block getBlock() {
        return this;
    }
}
