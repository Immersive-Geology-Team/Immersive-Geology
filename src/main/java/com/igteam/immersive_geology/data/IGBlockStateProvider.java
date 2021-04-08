package com.igteam.immersive_geology.data;

import com.ibm.icu.impl.CalendarAstronomer;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.block.BlockBase;
import com.igteam.immersive_geology.common.block.blocks.IGOreBlock;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;

public class IGBlockStateProvider extends BlockStateProvider {

    public IGBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper)
    {
        super(gen, IGLib.MODID, exFileHelper);
    }
    @Override
    public String getName(){
        return "Block Model/States";
    }

    @Override
    protected void registerStatesAndModels() {
        for(Block block : IGRegistrationHolder.registeredIGBlocks.values()) {
            try {
                if (block instanceof IGOreBlock) {
                    IGOreBlock oreBlock = (IGOreBlock) block;
                    String base_name = oreBlock.getStoneBase().getName(); //gets the name metamorphic and such
                    String ore_name = oreBlock.getOreBase().getName();

                    BlockModelBuilder  baseModel  = models().withExistingParent(new ResourceLocation(IGLib.MODID, "block/" + "ore_stone_" + ore_name + "_" + base_name).getPath(),
                            new ResourceLocation(IGLib.MODID, "block/base/ore_bearing/ore_bearing_" + oreBlock.getStoneBase().getStoneType().getName().toLowerCase()));
                    getVariantBuilder(block).forAllStates(blockState -> ConfiguredModel.builder().modelFile(baseModel).build());


                } else if (block instanceof BlockBase) {
                    getVariantBuilder(block).forAllStates(blockState -> ConfiguredModel.builder().modelFile(models().withExistingParent(new ResourceLocation(IGLib.MODID, "block/" + ((BlockBase) block).getBlockUseType().getName() + "_" + ((BlockBase) block).getMaterial().getName()).getPath(),
                            new ResourceLocation(IGLib.MODID, "block/base/" + ((BlockBase) block).getBlockUseType().getName()))).build());
                }
            } catch (Exception e){
                IGDataProvider.log.error("Failed to create Block Model/State: \n" + e);
            }
        }
    }
}
