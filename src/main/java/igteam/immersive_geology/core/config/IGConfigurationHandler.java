package igteam.immersive_geology.core.config;

import com.google.common.collect.ImmutableList;
import igteam.api.materials.MineralEnum;
import igteam.api.materials.data.mineral.variants.MaterialMineralKaolinite;
import igteam.api.materials.pattern.BlockPattern;
import igteam.immersive_geology.ImmersiveGeology;
import igteam.immersive_geology.core.lib.IGLib;
import igteam.api.config.IGOreConfig;
import igteam.api.materials.helper.APIMaterials;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.helper.MaterialSourceWorld;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.FeatureSpread;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.SphereReplaceConfig;
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
        public MaterialConfigSetup(ForgeConfigSpec.Builder builder) {
            builder.push("Ore Generation").comment("Ore Generation Configuration - START");

            ImmersiveGeology.getNewLogger().info("Generation Config Setup");
            IGOreConfig clayGenerationConfig = new IGOreConfig(builder, MaterialSourceWorld.overworld, MineralEnum.Kaolinite.getName(), 30, 46, 30, 70, 3, 5000);

            for(MaterialInterface<?> container : APIMaterials.generatedMaterials()) {
                if(container.equals(MineralEnum.Kaolinite)){
                    container.instance().setGenerationConfiguration(clayGenerationConfig);
                } else {
                    int heightMod = container.getDimension().equals(MaterialSourceWorld.nether) ? 2 : 1;
                    switch (container.instance().getRarity()) {
                        case COMMON:
                            container.instance().setGenerationConfiguration(new IGOreConfig(builder, container.getDimension(), container.getName(), 6, 12, 1, Math.min(255, 140 * heightMod), 10 * heightMod, 9000)); // 0.0010% Chance of successful Spawn (x / 10000)
                            break;
                        case UNCOMMON:
                            container.instance().setGenerationConfiguration(new IGOreConfig(builder, container.getDimension(), container.getName(), 5, 10, 1, Math.min(255, 140 * heightMod), 8 * heightMod, 8000));
                            break;
                        case RARE:
                            container.instance().setGenerationConfiguration(new IGOreConfig(builder, container.getDimension(), container.getName(), 4, 8, 1, Math.min(255, 90 * heightMod), 6 * heightMod, 6000)); // 70% chance to spawn
                            break;
                        case EPIC:
                            container.instance().setGenerationConfiguration(new IGOreConfig(builder, container.getDimension(), container.getName(), 3, 6, 1, Math.min(255, 60 * heightMod), 4 * heightMod, 5000)); // 50% Chance of successful Spawn
                            break;
                        default:
                            ImmersiveGeology.getNewLogger().error("Null Rarity for material " + container.getName() + " setting as default Backup Rarity");
                            container.instance().setGenerationConfiguration(new IGOreConfig(builder, container.getDimension(), container.getName(), 140, 80, 1, 90, 1, 1));
                            break;
                    }
                }
            }
            builder.pop();
        }
    }


    /*
    old settings for large ore veins
            switch (container.get().getRarity()) {
                    case COMMON:
                        container.get().setGenerationConfiguration(new IGOreConfig(builder, container.getDimension(), container.getName(), 140, 240, 1, Math.min(255, 140 * heightMod), 3  * heightMod, 7  * heightMod)); // 0.0010% Chance of successful Spawn
                        break;
                    case UNCOMMON:
                        container.get().setGenerationConfiguration(new IGOreConfig(builder, container.getDimension(), container.getName(), 120, 220, 1, Math.min(255,140 * heightMod), 2  * heightMod, 7  * heightMod));
                        break;
                    case RARE:
                        container.get().setGenerationConfiguration(new IGOreConfig(builder, container.getDimension(), container.getName(), 100,200, 1, Math.min(255, 90 * heightMod), heightMod, 7 * heightMod));
                        break;
                    case EPIC:
                        container.get().setGenerationConfiguration(new IGOreConfig(builder, container.getDimension(), container.getName(), 80, 180,1, Math.min(255, 60 * heightMod), heightMod, 5 * heightMod)); // 0.0015% Chance of successful Spawn
                        break;
                    default:
                        ImmersiveGeology.getNewLogger().error("Null Rarity for material " + container.getName() + " setting as default Backup Rarity");
                        container.get().setGenerationConfiguration(new IGOreConfig(builder, container.getDimension(), container.getName(), 140, 80, 1, 90, 1, 1));
                        break;
                }
     */
}
