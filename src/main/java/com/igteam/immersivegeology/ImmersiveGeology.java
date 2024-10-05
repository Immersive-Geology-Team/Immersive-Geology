package com.igteam.immersivegeology;

import com.igteam.immersivegeology.client.IGClientRenderHandler;
import com.igteam.immersivegeology.client.menu.CreativeMenuHandler;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.registration.IGContent;
import com.igteam.immersivegeology.core.registration.IGRecipeSerializers;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

@Mod(IGLib.MODID)
public class ImmersiveGeology {

    public ImmersiveGeology() {
        IEventBus modEventBus =  FMLJavaModLoadingContext.get().getModEventBus();
        IGLib.IG_LOGGER.info("Immersive Geology Starting");
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::clientSetup);
        IGRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);

        IGRegistrationHolder.addRegistersToEventBus(modEventBus);
        IGContent.modContruction(modEventBus);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new CreativeMenuHandler());
        IGClientRenderHandler.register();
        IGClientRenderHandler.init(event);
        supplyMaterialTint();
        IGContent.initializeManualEntries();
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
        if (pattern.equals(BlockCategoryFlags.STAIRS))
        {
            test =  new ResourceLocation(IGLib.MODID, "textures/" + (pattern instanceof ItemCategoryFlags ? "item" : "block") + "/colored/" + base.getName() + "/" + BlockCategoryFlags.STORAGE_BLOCK.getName() + ".png");
        }

        return test;
    }

    public void setup(final FMLCommonSetupEvent event)
    {
    }

}
