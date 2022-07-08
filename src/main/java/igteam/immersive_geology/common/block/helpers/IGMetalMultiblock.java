package igteam.immersive_geology.common.block.helpers;

import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.BlockItemIE;
import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartTileEntity;
import blusunrize.immersiveengineering.common.blocks.metal.MetalMultiblockBlock;
import igteam.immersive_geology.common.item.IGGenericBlockItem;
import igteam.immersive_geology.core.lib.IGLib;
import igteam.immersive_geology.core.registration.IGRegistrationHolder;
import igteam.api.IGApi;
import igteam.api.block.IGBlockType;
import igteam.api.materials.MetalEnum;
import igteam.api.main.IGRegistryProvider;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.BlockPattern;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.materials.pattern.MaterialPattern;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.function.Supplier;

public class IGMetalMultiblock<T extends MultiblockPartTileEntity<T>> extends MetalMultiblockBlock<T> implements IGBlockType {

    protected final IGGenericBlockItem itemBlock;

    public IGMetalMultiblock(String name, Supplier<TileEntityType<T>> te){
        super(name, te);

        if(!FMLLoader.isProduction()) { // apparently this fixes the concurrent exception and it works for IP so hopefully this works for us. ~Muddykat and thanks again Twisted for your code!
            try {
                IEContent.registeredIEBlocks.remove(this);
                Iterator<Item> it = IEContent.registeredIEItems.iterator();
                while (it.hasNext()) {
                    Item item = it.next();
                    if (item instanceof BlockItemIE && ((BlockItemIE) item).getBlock() == this) {
                        it.remove();
                        break;
                    }
                }
            } catch (ConcurrentModificationException exception) {
                IGApi.getNewLogger().error("Concurrent Modification Error - Essentially you'll need to restart, this issue is erratic and it's due to IG using IE internal Classes. Don't bother Immersive Engineering about this. (sorry Blu, I'll fix it sometime in the future ~Muddykat)");
                IGApi.getNewLogger().error(exception.getMessage());
                throw exception;
            }
        }


        this.itemBlock = new IGGenericBlockItem(this, getMachineMaterial(), ItemPattern.block_item);
        this.itemBlock.useDefaultNamingConvention();
        this.itemBlock.setRegistryName(new ResourceLocation(IGLib.MODID, ItemPattern.block_item.getName() + "_" + getHolderKey()));

        IGRegistryProvider.IG_BLOCK_REGISTRY.putIfAbsent(IGRegistrationHolder.getRegistryKey(this), this);
        IGRegistryProvider.IG_ITEM_REGISTRY.putIfAbsent(new ResourceLocation(IGLib.MODID, ItemPattern.block_item.getName() + "_" + getHolderKey()), itemBlock);
    }

    @Override
    public Item asItem() {
        return this.itemBlock;
    }

    @Override
    public int getColourForIGBlock(int pass) {
        return 0;
    }

    @Override
    public Collection<MaterialInterface> getMaterials() {
        return Collections.singleton(getMachineMaterial());
    }

    public MaterialInterface<?> getMachineMaterial(){
        return MetalEnum.Iron;
    }

    @Override
    public MaterialPattern getPattern() {
        return BlockPattern.machine;
    }

    @Override
    public String getHolderKey() {
        StringBuilder data = new StringBuilder();

        data.append("_").append(getMachineMaterial().getName());

        return getPattern() + data.toString() + "_" + name;
    }

    @Override
    public ResourceLocation createRegistryName(){
        return IGRegistrationHolder.getRegistryKey(this);
    }
    @Override
    public Block getBlock() {
        return this;
    }
}