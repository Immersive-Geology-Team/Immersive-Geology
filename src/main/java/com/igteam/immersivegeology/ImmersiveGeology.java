package com.igteam.immersivegeology;

import com.igteam.immersivegeology.client.IGClientRenderHandler;
import com.igteam.immersivegeology.client.IGOverlayHandler;
import com.igteam.immersivegeology.client.menu.CreativeMenuHandler;

import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.common.config.IGClientConfig;
import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.event.IGCommonForgeEvents;
import com.igteam.immersivegeology.common.network.IGPacketHandler;
import com.igteam.immersivegeology.common.world.IGWorldSubscription;
import com.igteam.immersivegeology.core.ClientProxy;
import com.igteam.immersivegeology.core.CommonProxy;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.registration.IGContent;
import com.igteam.immersivegeology.core.registration.IGRecipeSerializers;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

@Mod(IGLib.MODID)
public class ImmersiveGeology {

    public static CommonProxy proxy = Util.make(() ->
    {
        if(FMLLoader.getDist().isClient()) return new ClientProxy();
        return new CommonProxy();
    });

    public ImmersiveGeology() {
        IEventBus modEventBus =  FMLJavaModLoadingContext.get().getModEventBus();
        IGLib.IG_LOGGER.info("======== Starting Immersive Geology ========");
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::clientSetup);
        IGLib.IG_LOGGER.info("- Recipe Serializer Registration");
        IGRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);

        IGLib.IG_LOGGER.info("- World Event Handler Registration");
        MinecraftForge.EVENT_BUS.register(new IGWorldSubscription());

        IGLib.IG_LOGGER.info("- Client Configuration Registration");
        ModLoadingContext.get().registerConfig(Type.CLIENT, IGClientConfig.CONFIG_SPEC);

        IGLib.IG_LOGGER.info("- Server Configuration Registration");
        ModLoadingContext.get().registerConfig(Type.SERVER, IGServerConfig.CONFIG_SPEC);

        IGRegistrationHolder.addRegistersToEventBus(modEventBus);
        IGLib.IG_LOGGER.info("- Network Packet Handler Registration");
        IGPacketHandler.initialize();

        proxy.modConstruction();
    }

    private void clientSetup(FMLClientSetupEvent event) {
        IGLib.IG_LOGGER.info("- Custom Creative Menu Registration");
        MinecraftForge.EVENT_BUS.register(new CreativeMenuHandler());
        IGLib.IG_LOGGER.info("- Custom Multiblock Overlay Registration");
        MinecraftForge.EVENT_BUS.register(new IGOverlayHandler());

        IGLib.IG_LOGGER.info("- Client Render Handler Registration");
        IGClientRenderHandler.register();
        IGClientRenderHandler.init(event);

        IGLib.IG_LOGGER.info("- Color Tint Registration");
        supplyMaterialTint();

        IGLib.IG_LOGGER.info("- Container And Screen Registration");
        IGContent.registerContainersAndScreens();

        IGLib.IG_LOGGER.info("- Custom IE Manual Entry Registration");
        IGContent.initializeManualEntries();
    }

    private void supplyMaterialTint(){
        Minecraft minecraft = Minecraft.getInstance();

        // Define the BiPredicate to check if a material has a specific flag
        BiPredicate<GeologyMaterial, IFlagType<?>> needsColorCheck = (material, flagType) ->
                material.getFlags().contains(flagType);

        // Define the BiFunction to determine if a resource is present for a given flagType and material
        BiFunction<IFlagType<?>, GeologyMaterial, Boolean> resourceExists = (flagType, material) -> {
            ResourceLocation testLocation = getResourceLocationTest(flagType, material);
            try {
                return minecraft.getResourceManager().getResource(testLocation).isPresent();
            } catch (Exception e) {
                return false;
            }
        };

        for (MaterialInterface<?> materialInterface : IGLib.getGeologyMaterials()) {
            GeologyMaterial base = materialInterface.instance();
            HashMap<IFlagType<?>, Boolean> colorCheckMap = new HashMap<>();

            for (IFlagType<?> flagType : IFlagType.getAllRegistryFlags()) {
                // Apply the BiPredicate to check if the flagType needs color checking
                if (needsColorCheck.test(base, flagType)) {
                    // Use the BiFunction to see if the resource exists and update colorCheckMap accordingly
                    colorCheckMap.put(flagType, !resourceExists.apply(flagType, base));
                } else {
                    colorCheckMap.put(flagType, true);
                }
            }

            // Define a BiPredicate<IFlagType<?>, Integer> to handle the color tint check
            BiPredicate<IFlagType<?>, Integer> colorTintPredicate = (flagType, tintIndex) -> {
                // Return the value from colorCheckMap based on the flagType
                return colorCheckMap.getOrDefault(flagType, true);
            };

            // Initialize color tint using the BiPredicate with Integer parameter
            base.initializeColorTint(colorTintPredicate);
        }
    }

    @NotNull
    private static ResourceLocation getResourceLocationTest(IFlagType<?> pattern, GeologyMaterial base) {
        ResourceLocation test = new ResourceLocation(IGLib.MODID, "textures/" + (pattern instanceof ItemCategoryFlags ? "item" : "block") + "/colored/" + base.getName() + "/" + pattern.getName() + ".png");
        if (pattern.equals(BlockCategoryFlags.STAIRS))
        {
            test =  new ResourceLocation(IGLib.MODID, "textures/" + (pattern instanceof ItemCategoryFlags ? "item" : "block") + "/colored/" + base.getName() + "/" + BlockCategoryFlags.STORAGE_BLOCK.getName() + ".png");
        }

        if (pattern.equals(BlockCategoryFlags.SLAB))
        {
            test =  new ResourceLocation(IGLib.MODID, "textures/" + (pattern instanceof ItemCategoryFlags ? "item" : "block") + "/colored/" + base.getName() + "/" + BlockCategoryFlags.STORAGE_BLOCK.getName() + ".png");
        }

        if (pattern.equals(BlockCategoryFlags.SHEETMETAL_SLAB))
        {
            test =  new ResourceLocation(IGLib.MODID, "textures/" + (pattern instanceof ItemCategoryFlags ? "item" : "block") + "/colored/" + base.getName() + "/" + BlockCategoryFlags.SHEETMETAL_BLOCK.getName() + ".png");
        }

        if (pattern.equals(BlockCategoryFlags.SHEETMETAL_STAIRS))
        {
            test =  new ResourceLocation(IGLib.MODID, "textures/" + (pattern instanceof ItemCategoryFlags ? "item" : "block") + "/colored/" + base.getName() + "/" + BlockCategoryFlags.SHEETMETAL_BLOCK.getName() + ".png");
        }

        if(pattern.equals(ItemCategoryFlags.NORMAL_ORE) || pattern.equals(ItemCategoryFlags.RICH_ORE) || pattern.equals(ItemCategoryFlags.POOR_ORE))
        {
            OreRichness richness = pattern.equals(ItemCategoryFlags.NORMAL_ORE) ? OreRichness.NORMAL : (pattern.equals(ItemCategoryFlags.RICH_ORE) ? OreRichness.RICH : OreRichness.POOR);
            test = new ResourceLocation(IGLib.MODID, "textures/item/colored/raw_ore/"+base.getName().toLowerCase()+"/"+richness.getSanitizedName() + ".png");
        }
        return test;
    }

    public void setup(final FMLCommonSetupEvent event)
    {
        IGRegistrationHolder.buildMaterialRecipes();
        IGLib.IG_LOGGER.info("- Event Handler Registration");
        MinecraftForge.EVENT_BUS.register(new IGCommonForgeEvents());
    }

}
