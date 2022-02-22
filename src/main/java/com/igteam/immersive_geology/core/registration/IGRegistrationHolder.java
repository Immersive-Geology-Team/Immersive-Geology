package com.igteam.immersive_geology.core.registration;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.block.IGGenericBlock;
import com.igteam.immersive_geology.common.fluid.IGFluid;
import com.igteam.immersive_geology.common.item.IGGenericBlockItem;
import com.igteam.immersive_geology.common.item.IGGenericItem;
import com.igteam.immersive_geology.core.lib.IGLib;
import igteam.immersive_geology.block.IGBlockType;
import igteam.immersive_geology.item.IGItemType;
import igteam.immersive_geology.materials.*;
import igteam.immersive_geology.materials.data.fluid.MaterialBaseFluid;
import igteam.immersive_geology.materials.helper.IGRegistryProvider;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.helper.MaterialTexture;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MiscPattern;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class IGRegistrationHolder {
    private static final Logger logger = ImmersiveGeology.getNewLogger();

    public static void initialize(){
        logger.info("Registration of Material Interfaces");

        registerForInterface(StoneEnum.values());
        registerForInterface(MetalEnum.values());
        registerForInterface(MineralEnum.values());
        registerForInterface(FluidEnum.values());
        registerForInterface(GasEnum.values());
    }


    public static void buildRecipes() {
        logger.log(Level.INFO, "Building Recipes");
        buildMaterialRecipes(StoneEnum.values());
        buildMaterialRecipes(MetalEnum.values());
        buildMaterialRecipes(MineralEnum.values());
        buildMaterialRecipes(FluidEnum.values());
        buildMaterialRecipes(GasEnum.values());
    }

    private static void registerForInterface(MaterialInterface... material){
        Arrays.stream(material).iterator().forEachRemaining((m) -> {
            //Item Patterns
            Arrays.stream(ItemPattern.values()).iterator().forEachRemaining((pattern) -> {
                if(m.hasPattern(pattern)){
                    registerForItemPattern(m, pattern);
                }
            });

            //Block Patterns
            Arrays.stream(BlockPattern.values()).iterator().forEachRemaining((pattern) -> {
                if(m.hasPattern(pattern)){
                    registerForBlockPattern(m, pattern);
                }
            });

            //Misc Patterns
            Arrays.stream(MiscPattern.values()).iterator().forEachRemaining((pattern) -> {
                if(m.hasPattern(pattern)){
                    registerForMiscPattern(m, pattern);
                }
            });
        });
    }

    private static void registerForItemPattern(MaterialInterface m, ItemPattern p){
        switch(p) {
            case ore_chunk: case ore_bit: case dirty_crushed_ore: {
                Arrays.stream(StoneEnum.values()).iterator().forEachRemaining((stone) -> {
                    IGGenericItem multi_item = new IGGenericItem(m, p);
                    multi_item.addMaterial(MaterialTexture.base, stone);
                    multi_item.addMaterial(MaterialTexture.overlay, m);
                    multi_item.finalizeData();
                    register(multi_item);
                });
            }
            break;
            case block_item: {
                //DO NOT REGISTER HERE, used as an item pattern for all block items,
                //which is registered with the Block itself.
            }
            break;
            default: {
                IGGenericItem item = new IGGenericItem(m, p);
                item.finalizeData();
                register(item);
            }
            break;
        }
    }

    private static void registerForBlockPattern(MaterialInterface m, BlockPattern p){
        if (p == BlockPattern.ore) {
            Arrays.stream(StoneEnum.values()).iterator().forEachRemaining((stone) -> {
                if (m.generateOreFor(stone)) {
                    IGGenericBlock multi_block = new IGGenericBlock(m, p);
                    multi_block.addMaterial(stone, MaterialTexture.base);
                    multi_block.addMaterial(m, MaterialTexture.overlay);
                    multi_block.finalizeData();
                    register(multi_block.asItem());
                    register(multi_block);
                }
            });
        } else {
            IGGenericBlock multi_block = new IGGenericBlock(m, p);
            multi_block.finalizeData();
            register(multi_block.asItem());
            register(multi_block);
        }
    }

    private static void registerForMiscPattern(MaterialInterface<MaterialBaseFluid> m, MiscPattern p){
        if (p == MiscPattern.fluid) {
            IGFluid fluid = new IGFluid(m);

            register(fluid);
        }
    }

    private static void register(Item i){
        logger.info("Registering: " + i.getRegistryName());
        IGRegistryProvider.IG_ITEM_REGISTRY.put(i.getRegistryName(), i);
    }

    private static void register(Block b){
        logger.info("Registering: " + b.getRegistryName());
        IGRegistryProvider.IG_BLOCK_REGISTRY.put(b.getRegistryName(), b);
    }

    private static void register(Fluid f){
        logger.info("Registering: " + f.getRegistryName());
        IGRegistryProvider.IG_FLUID_REGISTRY.put(f.getRegistryName(), f);
    }

    @SubscribeEvent
    public static void itemRegistration(final RegistryEvent.Register<Item> event){
        logger.info("Applying Item Registration");


        IGRegistryProvider.IG_ITEM_REGISTRY.keySet().forEach((key) -> {
            logger.info("Attempting Registry for " + key.toString());
            event.getRegistry().register(IGRegistryProvider.IG_ITEM_REGISTRY.get(key));
        });
    }

    @SubscribeEvent
    public static void blockRegistration(final RegistryEvent.Register<Block> event){
        logger.info("Applying Block Registries");

        IGRegistryProvider.IG_BLOCK_REGISTRY.values().forEach((block) ->{
            event.getRegistry().register(block);
        });
    }

    @SubscribeEvent
    public static void fluidRegistration(final RegistryEvent.Register<Fluid> event){
        logger.info("Applying Fluid Registries");

        IGRegistryProvider.IG_FLUID_REGISTRY.values().forEach((fluid) ->{
            event.getRegistry().register(fluid);
        });
    }

    public static ResourceLocation getRegistryKey(IGGenericBlockItem item){
        return new ResourceLocation(IGLib.MODID, item.getHolderKey() + "_" + item.getIGBlockType().getPattern().getName());
    }

    public static ResourceLocation getRegistryKey(IGItemType item){
        return new ResourceLocation(IGLib.MODID, item.getHolderKey());
    }

    public static ResourceLocation getRegistryKey(IGBlockType block){
        return new ResourceLocation(IGLib.MODID, block.getHolderKey());
    }

    private static void buildMaterialRecipes(MaterialInterface... material){
        Arrays.stream(material).iterator().forEachRemaining(MaterialInterface::build);
    }
}
