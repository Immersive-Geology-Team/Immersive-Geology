package com.igteam.immersive_geology.core.proxy;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.client.IGClientRenderHandler;
import com.igteam.immersive_geology.client.menu.CreativeMenuHandler;
import com.igteam.immersive_geology.common.configuration.helper.ConfigurationHelper;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.material.GeologyMaterial;
import com.igteam.immersive_geology.core.material.helper.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class ClientProxy extends CommonProxy {

    @Override
    public void onCommonSetup(FMLCommonSetupEvent event) {
        super.onCommonSetup(event);
    }

    @Override
    public void onFinishSetup(FMLLoadCompleteEvent event) {

    }

    @Override
    public void onClientSetup(FMLClientSetupEvent event) {
        IGClientRenderHandler.register();
        IGClientRenderHandler.init(event);
        MinecraftForge.EVENT_BUS.register(new CreativeMenuHandler());
        supplyMaterialTint();
    }


    private void supplyMaterialTint(){
        Minecraft minecraft = Minecraft.getInstance();
        for(MaterialInterface<?> i : ImmersiveGeology.getGeologyMaterials()) {
            GeologyMaterial base = i.instance();
            HashMap<IFlagType<?>, Boolean> colorCheckMap = new HashMap<>();
            for (IFlagType<?> pattern : IFlagType.getAllRegistryFlags()) {
                colorCheckMap.put(pattern, true);
                if (base.getFlags().contains(pattern)) {
                    ResourceLocation test = new ResourceLocation(IGLib.MODID, "textures/" + (pattern instanceof ItemCategoryFlags ? "item" : "block") + "/colored/" + base.getName() + "/" + pattern.getName() + ".png");
                    if (pattern.equals(BlockCategoryFlags.SLAB)) //crutch for sheetmetal slabs
                    {
                        test =  new ResourceLocation(IGLib.MODID, "textures/" + (pattern instanceof ItemCategoryFlags ? "item" : "block") + "/colored/" + base.getName() + "/" + BlockCategoryFlags.SHEETMETAL_BLOCK.getName() + ".png");
                    }
                    try {
                        boolean check = minecraft.getResourceManager().hasResource(test);
                        colorCheckMap.put(pattern, !check);
                    } catch (Exception ignored) {}
                }
            }

            base.initializeColorTint(colorCheckMap::get);
        }
    }
}
