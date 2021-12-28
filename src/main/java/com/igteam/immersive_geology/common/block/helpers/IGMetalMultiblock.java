package com.igteam.immersive_geology.common.block.helpers;

import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.BlockItemIE;
import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartTileEntity;
import blusunrize.immersiveengineering.common.blocks.metal.MetalMultiblockBlock;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.common.item.IGBlockItem;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.item.Item;
import net.minecraft.state.Property;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;

import java.util.Iterator;
import java.util.function.Supplier;

public class IGMetalMultiblock<T extends MultiblockPartTileEntity<T>> extends MetalMultiblockBlock<T> implements IGBlockType {

    protected final Item itemBlock;
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

        String holder_name = IGRegistrationHolder.getRegistryKey(MaterialEnum.Steel.getMaterial(), MaterialUseType.MACHINE).toLowerCase();
        IGRegistrationHolder.registeredIGBlocks.put(holder_name + "_" + name, this);

        this.itemBlock = new IGBlockItem(this, this,  MaterialUseType.MACHINE.getSubgroup(), MaterialEnum.Steel.getMaterial());
        itemBlock.setRegistryName(holder_name.toLowerCase() + "_" + name);
        IGRegistrationHolder.registeredIGItems.put(holder_name + "_" + name, itemBlock);
    }

    @Override
    public Item asItem() {
        return itemBlock;
    }

    @Override
    public ResourceLocation createRegistryName(){
        return new ResourceLocation(IGLib.MODID, name);
    }

    @Override
    public String getHolderName() {
        return IGRegistrationHolder.getRegistryKey(MaterialEnum.Steel.getMaterial(), MaterialUseType.MACHINE);
    }

    @Override
    public MaterialUseType getBlockUseType() {
        return MaterialUseType.MACHINE;
    }

    @Override
    public Material getMaterial(BlockMaterialType type) {
        return MaterialEnum.Steel.getMaterial();
    }

    @Override
    public MaterialUseType getDropUseType() {
        return MaterialUseType.MACHINE;
    }

    @Override
    public float maxDrops() {
        return 0;
    }

    @Override
    public float minDrops() {
        return 0;
    }
}
