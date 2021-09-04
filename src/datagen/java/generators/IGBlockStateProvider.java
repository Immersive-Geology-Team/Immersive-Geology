package generators;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.data.models.SplitModelBuilder;
import com.google.common.base.Preconditions;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.block.BlockBase;
import com.igteam.immersive_geology.common.block.IGOreBlock;
import com.igteam.immersive_geology.common.block.IGStairsBlock;
import com.igteam.immersive_geology.common.block.helpers.BlockMaterialType;
import com.igteam.immersive_geology.common.block.helpers.IGBlockType;
import com.igteam.immersive_geology.common.fluid.IGFluid;
import com.igteam.immersive_geology.common.multiblocks.ChemicalVatMultiblock;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGMultiblockRegistrationHolder;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.client.model.generators.loaders.OBJLoaderBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IGBlockStateProvider extends BlockStateProvider {

    private Logger log = ImmersiveGeology.getNewLogger();

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
                    log.error("Failed to create Block Model/State: \n" + e);
                }
            }
        }

        chemicalvat();

        registerFluidBlocks();
    }

    private void chemicalvat(){
        ResourceLocation texture = modLoc("multiblock/chemicalvat_base");
        ResourceLocation modelNormal = modLoc("models/multiblock/obj/chemicalvat.obj");
        ResourceLocation modelMirrored = modLoc("models/multiblock/obj/chemicalvat_mirrored.obj");

        BlockModelBuilder normal = multiblockModel(IGMultiblockRegistrationHolder.Multiblock.chemicalvat, modelNormal, texture, "", ChemicalVatMultiblock.INSTANCE, false);
        BlockModelBuilder mirrored = multiblockModel(IGMultiblockRegistrationHolder.Multiblock.chemicalvat, modelMirrored, texture, "_mirrored", ChemicalVatMultiblock.INSTANCE, true);

        createMultiblock(IGMultiblockRegistrationHolder.Multiblock.chemicalvat, normal, mirrored, texture);
    }

    private BlockModelBuilder multiblockModel(Block block, ResourceLocation model, ResourceLocation texture, String add, TemplateMultiblock mb, boolean mirror){
        UnaryOperator<BlockPos> transform = UnaryOperator.identity();
        if(mirror){
            Vector3i size = mb.getSize(null);
            transform = p -> new BlockPos(size.getX() - p.getX() - 1, p.getY(), p.getZ());
        }
        final Vector3i offset = mb.getMasterFromOriginOffset();
        @SuppressWarnings("deprecation")
        Stream<Vector3i> partsStream = mb.getStructure(null).stream()
                .filter(info -> !info.state.isAir())
                .map(info -> info.pos)
                .map(transform)
                .map(p -> p.subtract(offset));

        String name = getMultiblockPath(block) + add;
        BlockModelBuilder base = this.models().withExistingParent(name, mcLoc("block"))
                .customLoader(OBJLoaderBuilder::begin).modelLocation(model).detectCullableFaces(false).flipV(true).end()
                .texture("texture", texture)
                .texture("particle", texture);

        BlockModelBuilder split = this.models().withExistingParent(name + "_split", mcLoc("block"))
                .customLoader(SplitModelBuilder::begin)
                .innerModel(base)
                .parts(partsStream.collect(Collectors.toList()))
                .dynamic(false).end();

        return split;
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
    private void createMultiblock(Block b, ModelFile masterModel, ModelFile mirroredModel, ResourceLocation particleTexture){
        createMultiblock(b, masterModel, mirroredModel, IEProperties.MULTIBLOCKSLAVE, IEProperties.FACING_HORIZONTAL, IEProperties.MIRRORED, 180, particleTexture);
    }

    private void createMultiblock(Block b, ModelFile masterModel, @Nullable ModelFile mirroredModel, Property<Boolean> isSlave, EnumProperty<Direction> facing, @Nullable Property<Boolean> mirroredState, int rotationOffset, ResourceLocation particleTex){
        Preconditions.checkArgument((mirroredModel == null) == (mirroredState == null));
        VariantBlockStateBuilder builder = getVariantBuilder(b);

        boolean[] possibleMirrorStates;
        if(mirroredState != null)
            possibleMirrorStates = new boolean[]{false, true};
        else
            possibleMirrorStates = new boolean[1];
        for(boolean mirrored:possibleMirrorStates)
            for(Direction dir:facing.getAllowedValues()){
                final int angleY;
                final int angleX;
                if(facing.getAllowedValues().contains(Direction.UP)){
                    angleX = -90 * dir.getYOffset();
                    if(dir.getAxis() != Direction.Axis.Y)
                        angleY = getAngle(dir, rotationOffset);
                    else
                        angleY = 0;
                }else{
                    angleY = getAngle(dir, rotationOffset);
                    angleX = 0;
                }

                ModelFile model = mirrored ? mirroredModel : masterModel;
                VariantBlockStateBuilder.PartialBlockstate partialState = builder.partialState()
//						.with(isSlave, false)
                        .with(facing, dir);

                if(mirroredState != null)
                    partialState = partialState.with(mirroredState, mirrored);

                partialState.setModels(new ConfiguredModel(model, angleX, angleY, true));
            }
    }

    private int getAngle(Direction dir, int offset){
        return (int) ((dir.getHorizontalAngle() + offset) % 360);
    }

    private String getMultiblockPath(Block b){
        return "multiblock/" + getPath(b);
    }

    private String getPath(Block b){
        return b.getRegistryName().getPath();
    }

    private void itemModelWithParent(Block block, ModelFile parent){
        getItemBuilder(block).parent(parent)
                .texture("particle", modLoc("block/" + getPath(block)));
    }

    private void simpleBlockWithItem(Block block){
        ModelFile file = cubeAll(block);

        getVariantBuilder(block).partialState()
                .setModels(new ConfiguredModel(file));
        itemModelWithParent(block, file);
    }

    private ItemModelBuilder getItemBuilder(Block block){
        return itemModels().getBuilder(modLoc("item/" + getPath(block)).toString());
    }

}
