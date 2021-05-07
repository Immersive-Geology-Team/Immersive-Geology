package com.igteam.immersive_geology.core.data.generators;

import com.igteam.immersive_geology.common.block.BlockBase;
import com.igteam.immersive_geology.common.block.IGOreBlock;
import com.igteam.immersive_geology.common.block.helpers.BlockMaterialType;
import com.igteam.immersive_geology.common.block.helpers.IGBlockType;
import com.igteam.immersive_geology.core.data.IGDataProvider;
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
        for(IGBlockType blockType : IGRegistrationHolder.registeredIGBlocks.values()) {
            try {
                switch(blockType.getBlockUseType()){
                    case ORE_STONE:
                        registerOreBlock(blockType);
                        break;
                    default:
                        registerDefaultBlock(blockType);
                }
            } catch (Exception e){
                IGDataProvider.log.error("Failed to create Block Model/State: \n" + e);
            }
        }
    }

    private void registerDefaultBlock(IGBlockType blockType){
        if(blockType instanceof BlockBase) {
            BlockBase block = (BlockBase) blockType;
            getVariantBuilder(block).forAllStates(blockState -> ConfiguredModel.builder().modelFile(models().withExistingParent(new ResourceLocation(IGLib.MODID, "block/" + block.getBlockUseType().getName() + "_" + block.getMaterial(BlockMaterialType.BASE_MATERIAL).getName()).getPath(),
                    new ResourceLocation(IGLib.MODID, "block/base/" + block.getBlockUseType().getName()))).build());

        }
    }

    private void registerOreBlock(IGBlockType blockType){
        if(blockType instanceof IGOreBlock){
            IGOreBlock oreBlock = (IGOreBlock) blockType;
            String stone_name = oreBlock.getMaterial(BlockMaterialType.BASE_MATERIAL).getStoneType().getName().toLowerCase();
            String base_name = oreBlock.getMaterial(BlockMaterialType.BASE_MATERIAL).getName(); //gets the name metamorphic and such
            String ore_name = oreBlock.getMaterial(BlockMaterialType.ORE_MATERIAL).getName();

            BlockModelBuilder  baseModel  = models().withExistingParent(new ResourceLocation(IGLib.MODID, "block/" + "ore_stone_" + base_name + "_" + ore_name).getPath(),
                    new ResourceLocation(IGLib.MODID, "block/base/ore_bearing/ore_bearing_" + stone_name))
                    .texture("ore", new ResourceLocation(IGLib.MODID, "block/greyscale/rock/ore_bearing/vanilla/vanilla_normal"));
            getVariantBuilder(blockType.getSelf()).forAllStates(blockState -> ConfiguredModel.builder().modelFile(baseModel).build());
        }
    }

}
