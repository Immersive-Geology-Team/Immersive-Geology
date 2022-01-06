package com.igteam.immersive_geology.core.proxy;

import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.StoneEnum;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerProxy implements Proxy {
    private final Logger logger = Logger.getLogger(ServerProxy.class.getName());

    @Override
    public void onSetup(FMLCommonSetupEvent event) {

    }

    @Override
    public void onFinishSetup(FMLLoadCompleteEvent event) {
    }

    @Override
    public void onClientSetup(FMLClientSetupEvent event) {
    }
}
