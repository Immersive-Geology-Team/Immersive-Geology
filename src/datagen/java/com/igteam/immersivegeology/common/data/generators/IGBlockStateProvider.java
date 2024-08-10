package com.igteam.immersivegeology.common.data.generators;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.data.DataGenUtils;
import blusunrize.immersiveengineering.data.models.*;
import blusunrize.immersiveengineering.data.models.NongeneratedModels.NongeneratedModel;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.igteam.immersivegeology.common.blocks.multiblocks.IGTemplateMultiblock;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblocks;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder.PartialBlockstate;
import net.neoforged.neoforge.client.model.generators.loaders.ObjModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IGBlockStateProvider extends BlockStateProvider {
    protected static final List<Vec3i> COLUMN_THREE = ImmutableList.of(BlockPos.ZERO, BlockPos.ZERO.above(), BlockPos.ZERO.above(2));

    public final Map<Block, ModelFile> unsplitModels = new HashMap<>();
    protected static final Map<ResourceLocation, String> generatedParticleTextures = new HashMap<>();
    protected final ExistingFileHelper existingFileHelper;
    protected final NongeneratedModels innerModels;

    public IGBlockStateProvider(DataGenerator generator, ExistingFileHelper helper){
        super(generator.getPackOutput(), IGLib.MODID, helper);
        this.existingFileHelper = helper;
        this.innerModels = new NongeneratedModels(generator.getPackOutput(), existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        crystallizer();
    }

    private void crystallizer() {
        IGLib.IG_LOGGER.info("Generating Crystallizer Multiblock Model Data");
        createMultiblock(innerObj("block/multiblock/obj/crystallizer/crystallizer.obj"), IGMultiblocks.CRYSTALLIZER);
    }

    private void createMultiblock(NongeneratedModel unsplitModel, IGTemplateMultiblock multiblock)
    {
        createMultiblock(unsplitModel, multiblock, false);
    }

    private void createDynamicMultiblock(NongeneratedModel unsplitModel, IGTemplateMultiblock multiblock)
    {
        createMultiblock(unsplitModel, multiblock, true);
    }

    private void createMultiblock(NongeneratedModel unsplitModel, IGTemplateMultiblock multiblock, boolean dynamic)
    {
        final ModelFile mainModel = split(unsplitModel, multiblock, false, dynamic);
        if(multiblock.getBlock().getStateDefinition().getProperties().contains(IEProperties.MIRRORED))
            createMultiblock(
                    multiblock::getBlock,
                    mainModel,
                    split(mirror(unsplitModel, innerModels), multiblock, true, dynamic),
                    IEProperties.FACING_HORIZONTAL, IEProperties.MIRRORED
            );
        else
            createMultiblock(multiblock::getBlock, mainModel, null, IEProperties.FACING_HORIZONTAL, null);
    }

    private void createMultiblock(Supplier<? extends Block> b, ModelFile masterModel)
    {
        createMultiblock(b, masterModel, null, IEProperties.FACING_HORIZONTAL, null);
    }

    private void createMultiblock(Supplier<? extends Block> b, ModelFile masterModel, @Nullable ModelFile mirroredModel,
                                  @Nullable Property<Boolean> mirroredState)
    {
        createMultiblock(b, masterModel, mirroredModel, IEProperties.FACING_HORIZONTAL, mirroredState);
    }

    private void createMultiblock(Supplier<? extends Block> b, ModelFile masterModel, @Nullable ModelFile mirroredModel,
                                  EnumProperty<Direction> facing, @Nullable Property<Boolean> mirroredState)
    {
        unsplitModels.put(b.get(), masterModel);
        Preconditions.checkArgument((mirroredModel==null)==(mirroredState==null));
        VariantBlockStateBuilder builder = getVariantBuilder(b.get());
        boolean[] possibleMirrorStates;
        if(mirroredState!=null)
            possibleMirrorStates = new boolean[]{false, true};
        else
            possibleMirrorStates = new boolean[1];
        for(boolean mirrored : possibleMirrorStates)
            for(Direction dir : facing.getPossibleValues())
            {
                final int angleY;
                final int angleX;
                if(facing.getPossibleValues().contains(Direction.UP))
                {
                    angleX = -90*dir.getStepY();
                    if(dir.getAxis()!= Direction.Axis.Y)
                        angleY = getAngle(dir, 180);
                    else
                        angleY = 0;
                }
                else
                {
                    angleY = getAngle(dir, 180);
                    angleX = 0;
                }
                ModelFile model = mirrored?mirroredModel: masterModel;
                PartialBlockstate partialState = builder.partialState()
                        .with(facing, dir);
                if(mirroredState!=null)
                    partialState = partialState.with(mirroredState, mirrored);
                partialState.setModels(new ConfiguredModel(model, angleX, angleY, true));
            }
    }

    private ModelFile split(NongeneratedModel loc, IGTemplateMultiblock mb)
    {
        return split(loc, mb, false);
    }

    private ModelFile split(NongeneratedModel loc, IGTemplateMultiblock mb, boolean mirror)
    {
        return split(loc, mb, mirror, false);
    }

    private ModelFile split(NongeneratedModel loc, IGTemplateMultiblock mb, boolean mirror, boolean dynamic)
    {
        UnaryOperator<BlockPos> transform = UnaryOperator.identity();
        if(mirror)
        {
            loadTemplateFor(mb);
            Vec3i size = mb.getSize(null);
            transform = p -> new BlockPos(size.getX()-p.getX()-1, p.getY(), p.getZ());
        }
        return split(loc, mb, transform, dynamic);
    }

    private ModelFile split(
            NongeneratedModel name, IGTemplateMultiblock multiblock, UnaryOperator<BlockPos> transform, boolean dynamic
    )
    {
        loadTemplateFor(multiblock);
        final Vec3i offset = multiblock.getMasterFromOriginOffset();
        Stream<Vec3i> partsStream = multiblock.getTemplate(null).blocksWithoutAir()
                .stream()
                .map(info -> info.pos())
                .map(transform)
                .map(p -> p.subtract(offset));
        return split(name, partsStream.collect(Collectors.toList()), dynamic);
    }

    private void loadTemplateFor(IGTemplateMultiblock multiblock)
    {
        final ResourceLocation name = multiblock.getUniqueName();
        if(IGTemplateMultiblock.SYNCED_CLIENT_TEMPLATES.containsKey(name))
            return;
        final String filePath = "structures/"+name.getPath()+".nbt";
        int slash = filePath.indexOf('/');
        String prefix = filePath.substring(0, slash);
        ResourceLocation shortLoc = new ResourceLocation(
                name.getNamespace(),
                filePath.substring(slash+1)
        );
        try
        {
            final Resource resource = existingFileHelper.getResource(shortLoc, PackType.SERVER_DATA, "", prefix);
            try(final InputStream input = resource.open())
            {
                final CompoundTag nbt = NbtIo.readCompressed(input, NbtAccounter.unlimitedHeap());
                final StructureTemplate template = new StructureTemplate();
                template.load(BuiltInRegistries.BLOCK.asLookup(), nbt);
                IGTemplateMultiblock.SYNCED_CLIENT_TEMPLATES.put(name, template);
            }
        } catch(IOException e)
        {
            throw new RuntimeException("Failed on "+name, e);
        }
    }

    protected NongeneratedModel innerObj(String loc, @Nullable RenderType layer)
    {
        Preconditions.checkArgument(loc.endsWith(".obj"));
        final var result = obj(loc.substring(0, loc.length()-4), modLoc(loc), innerModels);
        setRenderType(layer, result);
        return result;
    }

    protected NongeneratedModel innerObj(String loc)
    {
        return innerObj(loc, null);
    }

    protected BlockModelBuilder obj(String loc)
    {
        return obj(loc, (RenderType)null);
    }

    protected BlockModelBuilder obj(String loc, @Nullable RenderType layer)
    {
        final var model = obj(loc, models());
        setRenderType(layer, model);
        return model;
    }

    protected <T extends ModelBuilder<T>>
    T obj(String loc, ModelProvider<T> modelProvider)
    {
        Preconditions.checkArgument(loc.endsWith(".obj"));
        return obj(loc.substring(0, loc.length()-4), modLoc(loc), modelProvider);
    }

    protected <T extends ModelBuilder<T>>
    T obj(String name, ResourceLocation model, ModelProvider<T> provider)
    {
        return obj(name, model, ImmutableMap.of(), provider);
    }

    protected <T extends ModelBuilder<T>>
    T obj(String name, ResourceLocation model, Map<String, ResourceLocation> textures, ModelProvider<T> provider)
    {
        return obj(provider.withExistingParent(name, mcLoc("block")), model, textures);
    }

    protected <T extends ModelBuilder<T>>
    T obj(T base, ResourceLocation model, Map<String, ResourceLocation> textures)
    {
        assertModelExists(model);
        T ret = base
                .customLoader(ObjModelBuilder::begin)
                .automaticCulling(false)
                .modelLocation(addModelsPrefix(model))
                .flipV(true)
                .end();
        String particleTex = DataGenUtils.getTextureFromObj(model, existingFileHelper);
        if(particleTex.charAt(0)=='#')
            particleTex = textures.get(particleTex.substring(1)).toString();
        ret.texture("particle", particleTex);
        generatedParticleTextures.put(ret.getLocation(), particleTex);
        for(Entry<String, ResourceLocation> e : textures.entrySet())
            ret.texture(e.getKey(), e.getValue());
        return ret;
    }

    protected BlockModelBuilder splitModel(String name, NongeneratedModel model, List<Vec3i> parts, boolean dynamic)
    {
        BlockModelBuilder result = models().withExistingParent(name, mcLoc("block"))
                .customLoader(SplitModelBuilder::begin)
                .innerModel(model)
                .parts(parts)
                .dynamic(dynamic)
                .end();
        addParticleTextureFrom(result, model);
        return result;
    }

    protected ModelFile split(NongeneratedModel baseModel, List<Vec3i> parts, boolean dynamic)
    {
        return splitModel(baseModel.getLocation().getPath()+"_split", baseModel, parts, dynamic);
    }

    protected ModelFile split(NongeneratedModel baseModel, List<Vec3i> parts)
    {
        return split(baseModel, parts, false);
    }

    protected ModelFile splitDynamic(NongeneratedModel baseModel, List<Vec3i> parts)
    {
        return split(baseModel, parts, true);
    }

    protected void addParticleTextureFrom(BlockModelBuilder result, ModelFile model)
    {
        String particles = generatedParticleTextures.get(model.getLocation());
        if(particles!=null)
        {
            result.texture("particle", particles);
            generatedParticleTextures.put(result.getLocation(), particles);
        }
    }

    protected ConfiguredModel emptyWithParticles(String name, String particleTexture)
    {
        ModelFile model = models().withExistingParent(name, modLoc("block/ie_empty"))
                .texture("particle", particleTexture);
        generatedParticleTextures.put(modLoc(name), particleTexture);
        return new ConfiguredModel(model);
    }

    public void assertModelExists(ResourceLocation name)
    {
        String suffix = name.getPath().contains(".")?"": ".json";
        Preconditions.checkState(
                existingFileHelper.exists(name, PackType.CLIENT_RESOURCES, suffix, "models"),
                "Model \""+name+"\" does not exist");
    }

    protected IEOBJBuilder<BlockModelBuilder> ieObjBuilder(String loc)
    {
        return ieObjBuilder(getAutoNameIEOBJ(loc), modLoc(loc));
    }

    protected IEOBJBuilder<BlockModelBuilder> ieObjBuilder(String name, ResourceLocation model)
    {
        return ieObjBuilder(name, model, models());
    }

    protected <T extends ModelBuilder<T>>
    IEOBJBuilder<T> ieObjBuilder(String loc, ModelProvider<T> modelProvider)
    {
        return ieObjBuilder(getAutoNameIEOBJ(loc), modLoc(loc), modelProvider);
    }

    private static String getAutoNameIEOBJ(String loc)
    {
        Preconditions.checkArgument(loc.endsWith(".obj.ie"));
        return loc.substring(0, loc.length()-7);
    }

    protected <T extends ModelBuilder<T>>
    IEOBJBuilder<T> ieObjBuilder(String name, ResourceLocation model, ModelProvider<T> modelProvider)
    {
        final String particle = DataGenUtils.getTextureFromObj(model, existingFileHelper);
        generatedParticleTextures.put(modLoc(name), particle);
        return modelProvider.withExistingParent(name, mcLoc("block"))
                .texture("particle", particle)
                .customLoader(IEOBJBuilder::begin)
                .modelLocation(addModelsPrefix(model));
    }

    protected <T extends ModelBuilder<T>> T mirror(NongeneratedModel inner, ModelProvider<T> provider)
    {
        return provider.getBuilder(inner.getLocation().getPath()+"_mirrored")
                .customLoader(MirroredModelBuilder::begin)
                .inner(inner)
                .end();
    }

    protected int getAngle(Direction dir, int offset)
    {
        return (int)((dir.toYRot()+offset)%360);
    }

    protected void createHorizontalRotatedBlock(Supplier<? extends Block> block, ModelFile model)
    {
        createHorizontalRotatedBlock(block, $ -> model, List.of());
    }

    protected void createHorizontalRotatedBlock(Supplier<? extends Block> block, ModelFile model, int offsetRotY) {
        createRotatedBlock(block, $ -> model, IEProperties.FACING_HORIZONTAL, List.of(), 0, offsetRotY);
    }

    protected void createHorizontalRotatedBlock(Supplier<? extends Block> block, Function<PartialBlockstate, ModelFile> model, List<Property<?>> additionalProps)
    {
        createRotatedBlock(block, model, IEProperties.FACING_HORIZONTAL, additionalProps, 0, 180);
    }

    protected void createAllRotatedBlock(Supplier<? extends Block> block, ModelFile model)
    {
        createAllRotatedBlock(block, $ -> model, List.of());
    }

    protected void createAllRotatedBlock(Supplier<? extends Block> block, Function<PartialBlockstate, ModelFile> model, List<Property<?>> additionalProps)
    {
        createRotatedBlock(block, model, IEProperties.FACING_ALL, additionalProps, 90, 0);
    }

    protected void createRotatedBlock(Supplier<? extends Block> block, ModelFile model, Property<Direction> facing,
                                      List<Property<?>> additionalProps, int offsetRotX, int offsetRotY)
    {
        createRotatedBlock(block, $ -> model, facing, additionalProps, offsetRotX, offsetRotY);
    }

    protected void createRotatedBlock(Supplier<? extends Block> block, Function<PartialBlockstate, ModelFile> model, Property<Direction> facing,
                                      List<Property<?>> additionalProps, int offsetRotX, int offsetRotY)
    {
        VariantBlockStateBuilder stateBuilder = getVariantBuilder(block.get());
        forEachState(stateBuilder.partialState(), additionalProps, state -> {
            ModelFile modelLoc = model.apply(state);
            for(Direction d : facing.getPossibleValues())
            {
                int x;
                int y;
                switch(d)
                {
                    case UP -> {
                        x = 90;
                        y = 0;
                    }
                    case DOWN -> {
                        x = -90;
                        y = 0;
                    }
                    default -> {
                        y = getAngle(d, offsetRotY);
                        x = 0;
                    }
                }
                state.with(facing, d).setModels(new ConfiguredModel(modelLoc, x+offsetRotX, y, false));
            }
        });
    }

    protected static String getName(RenderStateShard state)
    {
        //TODO clean up/speed up
        try
        {
            // Datagen should only ever run in a deobf environment, so no need to use unreadable SRG names here
            // This is a workaround for the fact that client-side Mixins are not applied in datagen
            Field f = RenderStateShard.class.getDeclaredField("name");
            f.setAccessible(true);
            return (String)f.get(state);
        } catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static <T extends Comparable<T>> void forEach(PartialBlockstate base, Property<T> prop,
                                                         List<Property<?>> remaining, Consumer<PartialBlockstate> out)
    {
        for(T value : prop.getPossibleValues())
            forEachState(base, remaining, map -> {
                map = map.with(prop, value);
                out.accept(map);
            });
    }

    public static void forEachState(PartialBlockstate base, List<Property<?>> props, Consumer<PartialBlockstate> out)
    {
        if(props.size() > 0)
        {
            List<Property<?>> remaining = props.subList(1, props.size());
            Property<?> main = props.get(0);
            forEach(base, main, remaining, out);
        }
        else
            out.accept(base);
    }

    protected ResourceLocation addModelsPrefix(ResourceLocation in)
    {
        return new ResourceLocation(in.getNamespace(), "models/"+in.getPath());
    }

    protected void setRenderType(@Nullable RenderType type, ModelBuilder<?>... builders)
    {
        if(type!=null)
        {
            final String typeName = ModelProviderUtils.getName(type);
            for(final ModelBuilder<?> model : builders)
                model.renderType(typeName);
        }
    }
}
