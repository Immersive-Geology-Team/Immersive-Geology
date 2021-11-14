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

        public static final MultiblockConfigSetup MULTIBLOCK;

        static {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
            MULTIBLOCK = new MultiblockConfigSetup(builder);
            ALL = builder.build();
        }
    }

    public static class MultiblockConfigSetup{
        public final ForgeConfigSpec.ConfigValue<Double> chemicalVat_energyModifier;
        public final ForgeConfigSpec.ConfigValue<Double> chemicalVat_timeModifier;

        public final ForgeConfigSpec.ConfigValue<Double> electrolizer_energyModifier;
        public final ForgeConfigSpec.ConfigValue<Double> electrolizer_timeModifier;

        MultiblockConfigSetup(ForgeConfigSpec.Builder builder){
            builder.push("Multiblock Configuration");

            chemicalVat_energyModifier = builder
                    .comment("A modifier to apply to the energy costs of every Chemical Vat recipe, default=1")
                    .define("distillationTower_energyModifier", Double.valueOf(1.0));

            chemicalVat_timeModifier = builder
                    .comment("A modifier to apply to the time of every Chemical Vat recipe. Can't be lower than 1, default=1")
                    .define("distillationTower_timeModifier", Double.valueOf(1.0));

            electrolizer_energyModifier = builder
                    .comment("A modifier to apply to the energy costs of every Electrolizer recipe, default=1")
                    .define("distillationTower_energyModifier", Double.valueOf(1.0));

            electrolizer_timeModifier = builder
                    .comment("A modifier to apply to the time of every Electrolizer recipe. Can't be lower than 1, default=1")
                    .define("distillationTower_timeModifier", Double.valueOf(1.0));

            builder.pop();
        }

    }

    public static class MaterialConfigSetup{
        public MaterialConfigSetup(ForgeConfigSpec.Builder builder){
            builder.push("Ore Generation").comment("Ore Generation Configuration - START");
            for(MaterialEnum container : MaterialEnum.worldMaterials()) {
                Material material = (Material) container.getMaterial();
                ImmersiveGeology.getNewLogger().info("Generation Config setup for: " + material.getName());
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
                        ImmersiveGeology.getNewLogger().error("Null Rarity for material " + container.name() + " setting as default Common Rarity");
                        material.setConfiguration(new IGOreConfig(builder, material.getName(), 8, 1, 90, 6));
                        break;
                }
                builder.pop();
            }
            builder.pop();
        }
    }

}
