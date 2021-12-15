package com.igteam.immersive_geology.common.block.blocks;

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
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;

import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import net.minecraft.util.ResourceLocation;

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
        itemBlock.setRegistryName(holder_name.toLowerCase() + "_" + "bloomery");
    }

    @Override
    public ResourceLocation createRegistryName(){
        return new ResourceLocation(IGLib.MODID, holder_name + "_" + "bloomery");
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
}
