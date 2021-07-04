package com.igteam.immersive_geology.core.config;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.core.lib.IGLib;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

public class IGConfigurationHandler {

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = IGLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Client {

        public static final ForgeConfigSpec ALL;

        static {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
            ALL = builder.build();
        }

        @SubscribeEvent
        public static void onConfigChange(ModConfig.ModConfigEvent ev){

        }
    }

    @Mod.EventBusSubscriber(modid = IGLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Common {
        public static ForgeConfigSpec ALL;

        protected static MaterialConfigSetup MATERIALS;

        static {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
            MATERIALS = new MaterialConfigSetup(builder);
            ALL = builder.build();
        }

        @SubscribeEvent
        public static void onCommonReload(ModConfig.ModConfigEvent ev){

        }
    }

    @Mod.EventBusSubscriber(modid = IGLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Server {
        public static final ForgeConfigSpec ALL;

        static {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

            ALL = builder.build();
        }
    }


    public static class MaterialConfigSetup{
        public MaterialConfigSetup(ForgeConfigSpec.Builder builder){
            builder.push("Ore Generation").comment("Ore Generation Configuration - START");
            for(MaterialEnum container : MaterialEnum.worldMaterials()) {
                Material material = (Material) container.getMaterial();
                ImmersiveGeology.getNewLogger().info("Rarity Config Set: " + material.getRarity().name());
                builder.push(material.getName());
                switch (material.getRarity()) {
                    case COMMON:
                        material.setConfiguration(new IGOreConfig(builder, material.getName(), 8, 1, 90, 6));
                        break;
                    case UNCOMMON:
                        material.setConfiguration(new IGOreConfig(builder, material.getName(), 5, 1, 70, 4));
                        break;
                    case RARE:
                        material.setConfiguration(new IGOreConfig(builder, material.getName(), 4, 1, 50, 3));
                        break;
                    case EPIC:
                        material.setConfiguration(new IGOreConfig(builder, material.getName(), 3, 1, 30, 2));
                        break;
                    default:
                        ImmersiveGeology.getNewLogger().info("Null Rarity for material " + container.name() + " setting as default Common Rarity");
                        material.setConfiguration(new IGOreConfig(builder, material.getName(), 8, 1, 90, 6));
                        break;
                }
                builder.pop();
            }
            builder.pop();
        }
    }

}
