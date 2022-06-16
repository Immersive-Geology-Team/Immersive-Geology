package com.igteam.immersive_geology.core.config;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.core.lib.IGLib;
import igteam.immersive_geology.config.IGOreConfig;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.helper.APIMaterials;
import igteam.immersive_geology.materials.helper.MaterialInterface;
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
            builder.push("Multiblock Configuration -- NOT Implemented at the moment");
            //TODO Implement these Configs into the TileEntity so that it works!
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
            for(MaterialInterface<?> container : APIMaterials.generatedMaterials()) {
                    ImmersiveGeology.getNewLogger().info("Generation Config setup for: " + container.getName());
                    switch (container.get().getRarity()) {
                        case COMMON:
                            container.get().setGenerationConfiguration(new IGOreConfig(builder, container.getName(), 80, 140, 1, 90, 2, 50)); // 0.015% Chance of successful Spawn
                            break;
                        case UNCOMMON:
                            container.get().setGenerationConfiguration(new IGOreConfig(builder, container.getName(), 60, 120, 1, 70, 2, 40));
                            break;
                        case RARE:
                            container.get().setGenerationConfiguration(new IGOreConfig(builder, container.getName(), 40,100, 1, 50, 2, 25));
                            break;
                        case EPIC:
                            container.get().setGenerationConfiguration(new IGOreConfig(builder, container.getName(), 30, 80,1, 30, 1, 25)); // 0.005% Chance of successful Spawn
                            break;
                        default:
                            ImmersiveGeology.getNewLogger().error("Null Rarity for material " + container.getName() + " setting as default Backup Rarity");
                            container.get().setGenerationConfiguration(new IGOreConfig(builder, container.getName(), 50, 80, 1, 90, 1, 20));
                            break;
                    }
            }
            builder.pop();
        }
    }

}
