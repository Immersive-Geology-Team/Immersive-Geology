package com.igteam.immersive_geology.core.data.generators;

import com.igteam.immersive_geology.common.block.BlockBase;
import com.igteam.immersive_geology.common.block.IGOreBlock;
import com.igteam.immersive_geology.common.block.IGStairsBlock;
import com.igteam.immersive_geology.common.block.helpers.BlockMaterialType;
import com.igteam.immersive_geology.common.block.helpers.IGBlockType;
import com.igteam.immersive_geology.common.fluid.IGFluid;
import com.igteam.immersive_geology.core.data.IGDataProvider;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;
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
            if (block instanceof IGBlockType) {
                IGBlockType blockType = (IGBlockType) block;
                try {
                    switch (blockType.getBlockUseType()) {
                        case ORE_STONE:
                            registerOreBlock(blockType);
                            break;
                        case SHEETMETAL_STAIRS:
                            registerStairsBlock(blockType);
                            break;
                        case FLUIDS:
                            break;
                        default:
                            registerDefaultBlock(blockType);
                    }
                } catch (Exception e) {
                    IGDataProvider.log.error("Failed to create Block Model/State: \n" + e);
                }
            }
        }
        registerFluidBlocks();
    }

    private void registerFluidBlocks(){
        for(IGFluid f : IGFluid.IG_FLUIDS){
            ResourceLocation stillTexture = f.getAttributes().getStillTexture();
            ModelFile model = models().getBuilder("block/fluid/"+f.getRegistryName().getPath())
                    .texture("particle", stillTexture);
            getVariantBuilder(f.block).partialState().setModels(new ConfiguredModel(model));
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
            getVariantBuilder(oreBlock).forAllStates(blockState -> ConfiguredModel.builder().modelFile(baseModel).build());
        }
    }

    private void registerStairsBlock(IGBlockType blockType){
        if(blockType instanceof IGStairsBlock) {
            IGStairsBlock stairsBlock = (IGStairsBlock) blockType;
            VariantBlockStateBuilder builder = getVariantBuilder(stairsBlock);
            BlockModelBuilder baseModel = models().withExistingParent(new ResourceLocation(IGLib.MODID, "block/" + stairsBlock.getBlockUseType().getName() + "_" + stairsBlock.getMaterial(BlockMaterialType.BASE_MATERIAL).getName()).getPath(),
                    new ResourceLocation(IGLib.MODID, "block/base/" + stairsBlock.getBlockUseType().getName()));

            BlockModelBuilder innerModel = models().withExistingParent(new ResourceLocation(IGLib.MODID, "block/" + stairsBlock.getBlockUseType().getName() + "_inner_" + stairsBlock.getMaterial(BlockMaterialType.BASE_MATERIAL).getName()).getPath(),
                    new ResourceLocation(IGLib.MODID, "block/base/" + stairsBlock.getBlockUseType().getName()+ "_inner"));

            BlockModelBuilder outerModel = models().withExistingParent(new ResourceLocation(IGLib.MODID, "block/" + stairsBlock.getBlockUseType().getName() + "_outer_" + stairsBlock.getMaterial(BlockMaterialType.BASE_MATERIAL).getName()).getPath(),
                    new ResourceLocation(IGLib.MODID, "block/base/" + stairsBlock.getBlockUseType().getName()+ "_outer"));

            builder.forAllStates(blockState ->
                    blockState.get(stairsBlock.SHAPE) == StairsShape.INNER_LEFT ?
                        (blockState.get(stairsBlock.HALF) == Half.BOTTOM ?
                            (blockState.get(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(innerModel).rotationY(180).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.WEST ? ConfiguredModel.builder().modelFile(innerModel).rotationY(90).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(innerModel).rotationY(270).uvLock(true).build() :
                             ConfiguredModel.builder().modelFile(innerModel).uvLock(true).build()) :

                            (blockState.get(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(innerModel).rotationX(180).rotationY(270).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.WEST ? ConfiguredModel.builder().modelFile(innerModel).rotationX(180).rotationY(180).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(innerModel).rotationX(180).rotationY(0).uvLock(true).build() :
                             ConfiguredModel.builder().modelFile(innerModel).rotationX(180).rotationY(90).uvLock(true).build())) :

                    blockState.get(stairsBlock.SHAPE) == StairsShape.INNER_RIGHT ?
                        (blockState.get(stairsBlock.HALF) == Half.BOTTOM ?
                            (blockState.get(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(innerModel).rotationY(270).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.WEST ? ConfiguredModel.builder().modelFile(innerModel).rotationY(180).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(innerModel).rotationY(0).uvLock(true).build() :
                             ConfiguredModel.builder().modelFile(innerModel).rotationY(90).uvLock(true).build()) :

                           (blockState.get(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(innerModel).rotationX(180).rotationY(0).uvLock(true).build() :
                            blockState.get(stairsBlock.FACING) == Direction.WEST ? ConfiguredModel.builder().modelFile(innerModel).rotationX(180).rotationY(270).uvLock(true).build() :
                            blockState.get(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(innerModel).rotationX(180).rotationY(90).uvLock(true).build() :
                            ConfiguredModel.builder().modelFile(innerModel).rotationX(180).rotationY(180).uvLock(true).build())) :

                    blockState.get(stairsBlock.SHAPE) == StairsShape.OUTER_LEFT ?
                         (blockState.get(stairsBlock.HALF) == Half.BOTTOM ?
                            (blockState.get(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(outerModel).rotationY(180).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.WEST ? ConfiguredModel.builder().modelFile(outerModel).rotationY(90).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(outerModel).rotationY(270).uvLock(true).build() :
                             ConfiguredModel.builder().modelFile(outerModel).rotationY(0).uvLock(true).build()) :

                            (blockState.get(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(outerModel).rotationX(180).rotationY(270).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.WEST ? ConfiguredModel.builder().modelFile(outerModel).rotationX(180).rotationY(180).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(outerModel).rotationX(180).rotationY(0).uvLock(true).build() :
                             ConfiguredModel.builder().modelFile(outerModel).rotationX(180).rotationY(90).uvLock(true).build())) :

                    blockState.get(stairsBlock.SHAPE) == StairsShape.OUTER_RIGHT ?
                        (blockState.get(stairsBlock.HALF) == Half.BOTTOM ?
                            (blockState.get(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(outerModel).rotationY(270).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.WEST ? ConfiguredModel.builder().modelFile(outerModel).rotationY(180).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(outerModel).rotationY(0).uvLock(true).build() :
                             ConfiguredModel.builder().modelFile(outerModel).rotationY(90).build()) :

                            (blockState.get(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(outerModel).rotationX(180).rotationY(0).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.WEST ? ConfiguredModel.builder().modelFile(outerModel).rotationX(180).rotationY(270).uvLock(true).build() :
                             blockState.get(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(outerModel).rotationX(180).rotationY(90).uvLock(true).build() :
                             ConfiguredModel.builder().modelFile(outerModel).rotationX(180).rotationY(180).uvLock(true).build())) :

                    (blockState.get(stairsBlock.HALF) == Half.BOTTOM ?
                       (blockState.get(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(baseModel).rotationY(270).uvLock(true).build() :
                        blockState.get(stairsBlock.FACING) == Direction.SOUTH ? ConfiguredModel.builder().modelFile(baseModel).rotationY(90).uvLock(true).build() :
                        blockState.get(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(baseModel).rotationY(0).uvLock(true).build() :
                        ConfiguredModel.builder().modelFile(baseModel).rotationY(180).build()) :

                       (blockState.get(stairsBlock.FACING) == Direction.NORTH ? ConfiguredModel.builder().modelFile(baseModel).rotationX(180).rotationY(270).uvLock(true).build() :
                        blockState.get(stairsBlock.FACING) == Direction.SOUTH ? ConfiguredModel.builder().modelFile(baseModel).rotationX(180).rotationY(90).uvLock(true).build() :
                        blockState.get(stairsBlock.FACING) == Direction.EAST ? ConfiguredModel.builder().modelFile(baseModel).rotationX(180).rotationY(0).uvLock(true).build() :
                        ConfiguredModel.builder().modelFile(baseModel).rotationX(180).rotationY(180).uvLock(true).build()))
        );
        }
    }


}
