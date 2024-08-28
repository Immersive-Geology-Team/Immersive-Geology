package com.igteam.immersivegeology.core.proxy;

import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import com.igteam.immersivegeology.client.renderer.multiblocks.CoreDrillRenderer;
import com.igteam.immersivegeology.common.block.multiblocks.logic.CoreDrillLogic.State;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import java.util.function.Supplier;


public class ClientProxy extends CommonProxy {
    @Override
    public void setup(){
        //TODO
    }

    @Override
    public void completed(FMLLoadCompleteEvent event){
        // TODO IE Manual Entries Here
    }

    @Override
    public void preInit(){
        //TODO
    }

    @Override
    public void preInitEnd(){
        //TODO
    }

    @Override
    public void init(){
        //TODO
    }

}
