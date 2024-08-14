package com.igteam.immersivegeology;


import com.igteam.immersivegeology.client.IGClientRenderHandler;
import com.igteam.immersivegeology.client.menu.CreativeMenuHandler;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.proxy.ClientProxy;
import com.igteam.immersivegeology.core.proxy.CommonProxy;
import com.igteam.immersivegeology.core.registration.IGContent;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import com.igteam.immersivegeology.core.registration.IGMultiblocks;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

@Mod(IGLib.MODID)
public class ImmersiveGeology {

    public static final CommonProxy proxy = makeProxy();
    private static CommonProxy makeProxy(){
        return FMLLoader.getDist() == Dist.CLIENT ? new ClientProxy() : new CommonProxy();
    }

    public ImmersiveGeology(IEventBus modEventBus){
        IGLib.IG_LOGGER.info("Immersive Geology Starting");
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::clientSetup);

        IGRegistrationHolder.addRegistersToEventBus(modEventBus);

        IGContent.modContruction(modEventBus);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(new CreativeMenuHandler());
        IGClientRenderHandler.register();
        IGClientRenderHandler.init(event);
        supplyMaterialTint();
    }

    private void supplyMaterialTint(){
        Minecraft minecraft = Minecraft.getInstance();
        for(MaterialInterface<?> i : IGLib.getGeologyMaterials()) {
            GeologyMaterial base = i.instance();
            HashMap<IFlagType<?>, Boolean> colorCheckMap = new HashMap<>();
            for (IFlagType<?> pattern : IFlagType.getAllRegistryFlags()) {
                colorCheckMap.put(pattern, true);
                if (base.getFlags().contains(pattern)) {
                    ResourceLocation test = getResourceLocationTest(pattern, base);
                    try {
                        boolean check = minecraft.getResourceManager().getResource(test).isPresent();
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

    public void setup(final FMLCommonSetupEvent event){
        proxy.setup();
        proxy.preInit();

        // Pre init for IG Content?

        proxy.preInitEnd();
        IGContent.initialize(event);

        proxy.init();

        proxy.postInit();
    }

}
