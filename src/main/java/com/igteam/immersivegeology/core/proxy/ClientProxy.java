package com.igteam.immersivegeology.core.proxy;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.lib.manual.ManualEntry;
import blusunrize.lib.manual.ManualInstance;
import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.client.IGClientRenderHandler;
import com.igteam.immersivegeology.client.menu.CreativeMenuHandler;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
        setupManualEntries();
    }

    private void supplyMaterialTint(){
        Minecraft minecraft = Minecraft.getInstance();
        for(MaterialInterface<?> i : ImmersiveGeology.getGeologyMaterials()) {
            GeologyMaterial base = i.instance();
            HashMap<IFlagType<?>, Boolean> colorCheckMap = new HashMap<>();
            for (IFlagType<?> pattern : IFlagType.getAllRegistryFlags()) {
                colorCheckMap.put(pattern, true);
                if (base.getFlags().contains(pattern)) {
                    ResourceLocation test = getResourceLocationTest(pattern, base);
                    try {
                        boolean check = minecraft.getResourceManager().hasResource(test);
                        colorCheckMap.put(pattern, !check);
                    } catch (Exception ignored) {}
                }
            }

            base.initializeColorTint(colorCheckMap::get);
        }
    }

    @NotNull
    private static ResourceLocation getResourceLocationTest(IFlagType<?> pattern, GeologyMaterial base) {
        ResourceLocation test = new ResourceLocation(IGLib.MODID, "textures/" + (pattern instanceof ItemCategoryFlags ? "item" : "block") + "/colored/" + base.getName() + "/" + pattern.getName() + ".png");
        if (pattern.equals(BlockCategoryFlags.SLAB)) //crutch for sheetmetal slabs
        {
            test =  new ResourceLocation(IGLib.MODID, "textures/" + (pattern instanceof ItemCategoryFlags ? "item" : "block") + "/colored/" + base.getName() + "/" + BlockCategoryFlags.SHEETMETAL_BLOCK.getName() + ".png");
        }
        return test;
    }


    public void setupManualEntries(){
        coredrill(new ResourceLocation(IGLib.MODID, "coredrill"), 0);
    }

    private static void coredrill(ResourceLocation location, int priority){
        ManualInstance man = ManualHelper.getManual();

        ManualEntry.ManualEntryBuilder builder = new ManualEntry.ManualEntryBuilder(man);
        builder.readFromFile(location);

        man.addEntry(man.getRoot().getOrCreateSubnode(new ResourceLocation(IGLib.MODID)), builder.create(), priority);
    }
}
