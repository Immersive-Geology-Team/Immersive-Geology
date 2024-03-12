package com.igteam.immersivegeology.core;

import com.igteam.immersivegeology.core.lib.IGLib;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@Mod.EventBusSubscriber(modid = IGLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IGConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // private static variables defined here, use the BUILDER

    static final ModConfigSpec SPEC = BUILDER.build();

    // public static values defined here, then initialized onLoad.

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {

    }
}
