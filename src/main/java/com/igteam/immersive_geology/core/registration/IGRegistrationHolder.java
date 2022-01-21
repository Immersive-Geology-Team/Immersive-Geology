package com.igteam.immersive_geology.core.registration;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.block.IGGenericBlock;
import com.igteam.immersive_geology.common.item.IGGenericBlockItem;
import com.igteam.immersive_geology.common.item.IGGenericItem;
import com.igteam.immersive_geology.core.lib.IGLib;
import igteam.immersive_geology.materials.*;
import igteam.immersive_geology.materials.helper.IGRegistryProvider;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.helper.MaterialTexture;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MiscPattern;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class IGRegistrationHolder {
    private static Logger logger = ImmersiveGeology.getNewLogger();

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
            case ore_chunk, ore_bit, dirty_crushed_ore -> {
                Arrays.stream(StoneEnum.values()).iterator().forEachRemaining((stone) -> {
                    IGGenericItem multi_item = new IGGenericItem(m, p);
                    multi_item.addMaterial(MaterialTexture.base, stone);
                    multi_item.addMaterial(MaterialTexture.overlay, m);
                    multi_item.finalizeData();
                    register(multi_item);
                });
            }
            case block_item -> {
                //DO NOT REGISTER HERE, used as an item pattern for all block items,
                //which is registered with the Block itself.
            }
            default -> {
                IGGenericItem item = new IGGenericItem(m, p);
                item.finalizeData();
                register(item);
            }
        }
    }

    private static void registerForBlockPattern(MaterialInterface m, BlockPattern p){
        switch(p) {
            case ore -> {
                Arrays.stream(StoneEnum.values()).iterator().forEachRemaining((stone) -> {
                    IGGenericBlock multi_block = new IGGenericBlock(m, p);
                    multi_block.addMaterial(stone, MaterialTexture.base);
                    multi_block.addMaterial(m, MaterialTexture.overlay);
                    multi_block.finalizeData();
                    register(multi_block.asItem());
                    register(multi_block);
                });
            }
            default -> {
                IGGenericBlock multi_block = new IGGenericBlock(m, p);
                multi_block.finalizeData();
                register(multi_block.asItem());
                register(multi_block);
            }
        }
    }

    private static void registerForMiscPattern(MaterialInterface m, MiscPattern p){

    }

    private static void register(Item i){
        IGRegistryProvider.IG_ITEM_REGISTRY.put(i.getRegistryName(), i);
    }

    private static void register(Block b){
        IGRegistryProvider.IG_BLOCK_REGISTRY.put(b.getRegistryName(), b);
    }

    private static void register(Fluid f){
        IGRegistryProvider.IG_FLUID_REGISTRY.put(f.getRegistryName(), f);
    }

    @SubscribeEvent
    public static void itemRegistration(final RegistryEvent.Register<Item> event){
        logger.info("Applying Item Registration");

        IGRegistryProvider.IG_ITEM_REGISTRY.values().forEach((item) -> {
            event.getRegistry().register(item);
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
        IGRegistryProvider.IG_FLUID_REGISTRY.values().forEach((fluid) ->{
            event.getRegistry().register(fluid);
        });
    }

    public static ResourceLocation getRegistryKey(IGGenericBlockItem item){
        return new ResourceLocation(IGLib.MODID, item.getHolderKey());
    }

    public static ResourceLocation getRegistryKey(IGGenericItem item){
        return new ResourceLocation(IGLib.MODID, item.getHolderKey());
    }

    public static ResourceLocation getRegistryKey(IGGenericBlock block){
        return new ResourceLocation(IGLib.MODID, block.getHolderKey());
    }

    private static void buildMaterialRecipes(MaterialInterface... material){
        Arrays.stream(material).iterator().forEachRemaining((m) -> {
            m.build();
        });
    }

}
