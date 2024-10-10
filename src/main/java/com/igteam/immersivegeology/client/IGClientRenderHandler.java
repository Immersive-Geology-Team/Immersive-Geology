/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client;

import com.igteam.immersivegeology.common.block.IGFluidBlock;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.common.item.helper.IGFlagItem;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class IGClientRenderHandler implements ItemColor, BlockColor {
    // Handles, IG Block and Item Tint Colors, Render Layering and Special Renders

    @OnlyIn(Dist.CLIENT)
    private static Map<RenderTypeSkeleton, RenderType> renderTypes;

    private static final Map<Block, RenderTypeSkeleton> mapping = new HashMap<>();
    private static final Map<Block, Block> inheritances = new HashMap<>();

    public static IGClientRenderHandler INSTANCE = new IGClientRenderHandler();

    // Register This as the color handler for all IG Items and Blocks
    public static void register(){
        for(Supplier<Item> holder : IGRegistrationHolder.getItemRegistryMap().values()){
            Item i = holder.get();
            if(i instanceof IGFlagItem){
                Minecraft.getInstance().getItemColors().register(INSTANCE, i);
            }
        }

        for(RegistryObject<Block> holder : IGRegistrationHolder.getBlockRegistryMap().values()){
            Block b = holder.get();
            if(b instanceof IGBlockType igBlock){
                Minecraft.getInstance().getBlockColors().register(INSTANCE, b);
                setRenderType(b, igBlock.getFlag().getRenderType());
            }

            if(b instanceof IGFluidBlock fluidBlock)
            {
                if(fluidBlock.isTranslucent()){
                    ItemBlockRenderTypes.setRenderLayer(fluidBlock.getFluid().getSource(), RenderType.translucent());
                    ItemBlockRenderTypes.setRenderLayer(fluidBlock.getFluid().getFlowing(), RenderType.translucent());
                    continue;
                }
                ItemBlockRenderTypes.setRenderLayer(fluidBlock.getFluid().getSource(), RenderType.solid());
                ItemBlockRenderTypes.setRenderLayer(fluidBlock.getFluid().getFlowing(), RenderType.solid());
            }
        }

        setRenderType(IGMultiblockProvider.COREDRILL.block().get(), RenderTypeSkeleton.CUTOUT_MIPPED);
        setRenderType(IGMultiblockProvider.CHEMICAL_REACTOR.block().get(), RenderTypeSkeleton.CUTOUT_MIPPED);
        setRenderType(IGMultiblockProvider.REVERBERATION_FURNACE.block().get(), RenderTypeSkeleton.CUTOUT_MIPPED);
    }

    // Initialize the keys and mappings for render layers
    @OnlyIn(Dist.CLIENT)
    public static void init(FMLClientSetupEvent event) {
        for(Block b : inheritances.keySet()) {
            Block inherit = inheritances.get(b);
            if(mapping.containsKey(inherit))
                mapping.put(b, mapping.get(inherit));
        }

        for(Block b : mapping.keySet()) {
            ItemBlockRenderTypes.setRenderLayer(b, renderTypes.get(mapping.get(b)));
        }

        inheritances.clear();
        mapping.clear();
    }

    // Color Function for IG Blocks
    @Override
    public int getColor(BlockState state, @Nullable BlockAndTintGetter getter, @Nullable BlockPos pos, int index) {
        if(state.getBlock() instanceof IGBlockType type)
            return type.getColor(index);
        return 0xffffff;
    }

    // Color Function for IG Items
    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if(stack.getItem() instanceof IGFlagItem type)
            return type.getColor(tintIndex);
        return 0xffffff;
    }

    @OnlyIn(Dist.CLIENT)
    public static void setRenderType(Block block, RenderTypeSkeleton skeleton) {
        setRenderTypeClient(block, skeleton);
    }

    @OnlyIn(Dist.CLIENT)
    private static void setRenderTypeClient(Block block, RenderTypeSkeleton skeleton) {
        resolveRenderTypes();
        mapping.put(block, skeleton);
    }

    @OnlyIn(Dist.CLIENT)
    private static void resolveRenderTypes() {
        if(renderTypes == null) {
            renderTypes = new HashMap<>();

            renderTypes.put(RenderTypeSkeleton.SOLID, RenderType.solid());
            renderTypes.put(RenderTypeSkeleton.CUTOUT, RenderType.cutout());
            renderTypes.put(RenderTypeSkeleton.CUTOUT_MIPPED, RenderType.cutoutMipped());
            renderTypes.put(RenderTypeSkeleton.TRANSLUCENT, RenderType.translucent());
        }
    }

    public enum RenderTypeSkeleton {
        SOLID,
        CUTOUT,
        CUTOUT_MIPPED,
        TRANSLUCENT;
    }
}
