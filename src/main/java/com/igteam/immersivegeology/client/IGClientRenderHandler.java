package com.igteam.immersivegeology.client;

import com.igteam.immersivegeology.common.blocks.helper.IGBlockType;
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

public class IGClientRenderHandler implements ItemColor, BlockColor {
    // Handles, IG Block and Item Tint Colors, Render Layering and Special Renders

    @OnlyIn(Dist.CLIENT)
    private static Map<RenderTypeSkeleton, RenderType> renderTypes;

    private static final Map<Block, RenderTypeSkeleton> mapping = new HashMap<>();
    private static final Map<Block, Block> inheritances = new HashMap<>();

    public static IGClientRenderHandler INSTANCE = new IGClientRenderHandler();

    // Register This as the color handler for all IG Items and Blocks
    public static void register(){
        for(Item i : IGRegistrationHolder.getItemRegistryMap().values().stream().map(RegistryObject::get).toList()){
            if(i instanceof IGFlagItem){
                Minecraft.getInstance().getItemColors().register(INSTANCE, i);
            }
        }

        for(Block b : IGRegistrationHolder.getBlockRegistryMap().values().stream().map(RegistryObject::get).toList()){
            if(b instanceof IGBlockType){
                Minecraft.getInstance().getBlockColors().register(INSTANCE, b);
            }
        }

        IGClientRenderHandler.setRenderType(IGMultiblockProvider.COREDRILL.block().get(), IGClientRenderHandler.RenderTypeSkeleton.CUTOUT_MIPPED);
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

        ItemBlockRenderTypes.setRenderLayer(IGRegistrationHolder.getMB("crystallizer").block().get(), RenderType.cutoutMipped());

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
