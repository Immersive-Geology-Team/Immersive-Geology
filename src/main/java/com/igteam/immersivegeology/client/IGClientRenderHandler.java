/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client;

import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.common.fluid.IGFluidBlock;
import com.igteam.immersivegeology.common.item.IGGenericBlockItem;
import com.igteam.immersivegeology.common.item.helper.IGFlagItem;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

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

        for(DeferredHolder<Block, Block> holder : IGRegistrationHolder.getBlockRegistryMap().values()){
            Block b = holder.get();
            if(b instanceof IGBlockType igBlock){
                Minecraft.getInstance().getBlockColors().register(INSTANCE, b);
                setRenderType(b, igBlock.getFlag().getRenderType());
            }

            if(b instanceof IGFluidBlock fluidBlock)
            {
                if(fluidBlock.isTranslucent()){
                    ItemBlockRenderTypes.setRenderLayer(fluidBlock.fluid.getSource(), RenderType.translucent());
                    ItemBlockRenderTypes.setRenderLayer(fluidBlock.fluid.getFlowing(), RenderType.translucent());
                    continue;
                }
                ItemBlockRenderTypes.setRenderLayer(fluidBlock.fluid.getSource(), RenderType.solid());
                ItemBlockRenderTypes.setRenderLayer(fluidBlock.fluid.getFlowing(), RenderType.solid());
            }
        }

        setRenderType(IGMultiblockProvider.COREDRILL.block().get(), RenderTypeSkeleton.CUTOUT_MIPPED);
        setRenderType(IGMultiblockProvider.CHEMICAL_REACTOR.block().get(), RenderTypeSkeleton.CUTOUT_MIPPED);
        setRenderType(IGMultiblockProvider.REVERBERATION_FURNACE.block().get(), RenderTypeSkeleton.CUTOUT_MIPPED);
        setRenderType(IGMultiblockProvider.BLOOMERY.block().get(), RenderTypeSkeleton.CUTOUT_MIPPED);
        setRenderType(IGMultiblockProvider.GRAVITY_SEPARATOR.block().get(), RenderTypeSkeleton.CUTOUT_MIPPED);
        setRenderType(IGMultiblockProvider.ROTARYKILN.block().get(), RenderTypeSkeleton.CUTOUT_MIPPED);
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
        {
            if(type.getFlag().equals(BlockCategoryFlags.ORE_BLOCK)) return 0xffffffff;
            return type.getColor(index, state);
        }
        return 0xffffffff;
    }

    // Color Function for IG Items
    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if(stack.getItem() instanceof IGFlagItem type)
        {
            if(stack.getItem() instanceof IGGenericBlockItem b)
            {
                if(b.getFlag().equals(BlockCategoryFlags.ORE_BLOCK)) return 0xffffffff;
            }
            int color = type.getColor(tintIndex);
            return (color & 0xff000000) == 0 ? color | 0xff000000 : color;
        }
        return 0xffffffff;
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
