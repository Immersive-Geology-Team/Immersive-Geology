package com.igteam.immersive_geology.core.proxy;

import blusunrize.immersiveengineering.client.IEDefaultColourHandlers;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.items.IEItemInterfaces;
import com.igteam.immersive_geology.client.menu.helper.CreativeMenuHandler;
import com.igteam.immersive_geology.client.render.RenderLayerHandler;
import com.igteam.immersive_geology.common.block.helpers.IGBlockType;
import com.igteam.immersive_geology.common.world.IGWorldGeneration;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientProxy extends ServerProxy {

    @Override
    public void onClientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new CreativeMenuHandler());
        RenderLayerHandler.init(event);
        registerItemColors();
        registerBlockColors();
    }

    private void registerItemColors(){
        for(Item item : IGRegistrationHolder.registeredIGItems.values()){
            if(item instanceof IEItemInterfaces.IColouredItem && ((IEItemInterfaces.IColouredItem) item).hasCustomItemColours()){
                Minecraft.getInstance().getItemColors().register(IEDefaultColourHandlers.INSTANCE, item);
            }
        }
    }

    private void registerBlockColors(){
        for(Block block : IGRegistrationHolder.registeredIGBlocks.values()){
            if(block instanceof IEBlockInterfaces.IColouredBlock && ((IEBlockInterfaces.IColouredBlock) block).hasCustomBlockColours()){
                Minecraft.getInstance().getBlockColors().register(IEDefaultColourHandlers.INSTANCE, block);
            }
        }
    }
}
