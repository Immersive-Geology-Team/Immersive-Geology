package com.igteam.immersive_geology.client.model;


import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = IGLib.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class IGModels {
    @SubscribeEvent
    public static void init(FMLConstructModEvent event){
        add(ModelChemicalVat.ID, new ModelChemicalVat());
        add(ModelGravitySeparator.ID, new ModelGravitySeparator());
    }
    private static final Map<String, IGModel> MODELS = new HashMap<>();

    public static void add(String id, IGModel model){
        if(MODELS.containsKey(id)){
            ImmersiveGeology.getNewLogger().error("Duplicate ID, \"{}\" already used by {}. Skipping.", id, MODELS.get(id).getClass());
        }else{
            ImmersiveGeology.getNewLogger().info("Adding new Modelpart: " + model.toString());
            model.init();
            MODELS.put(id, model);
        }
    }

    public static Supplier<IGModel> getSupplier(String id){
        return () -> MODELS.get(id);
    }

    public static Collection<IGModel> getModels(){
        return Collections.unmodifiableCollection(MODELS.values());
    }
}
