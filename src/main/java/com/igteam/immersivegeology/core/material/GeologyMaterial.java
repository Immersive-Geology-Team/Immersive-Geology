/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler.IMultiblock;
import com.google.common.collect.Sets;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.common.tag.IGTags;
import com.igteam.immersivegeology.common.world.IWorldGenConfig;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.configuration.ConfigurationHelper;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import com.igteam.immersivegeology.core.material.helper.material.*;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeChain;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

import static net.minecraft.server.packs.PackType.CLIENT_RESOURCES;

public abstract class GeologyMaterial implements MaterialHelper {
    public static ExistingFileHelper EXISTING_HELPER;
    protected String name, unserialized_name;
    protected Logger logger = IGLib.getNewLogger();
    protected BiFunction<IFlagType<?>, Integer, Integer> colorFunction; // in goes a category, returns the color white as a default
    protected BiPredicate<IFlagType<?>, Integer> applyColorTint; // In a goes the flag and int, returns if it uses programmed color tint
    private final LinkedHashSet<IFlagType<?>> materialDataFlags = Sets.newLinkedHashSet();

    protected final List<Supplier<IMultiblock>> validMultiblocks = new ArrayList<>();
    protected Rarity materialRarity = Rarity.COMMON;

    protected IGRecipeChain directBlasting = new IGRecipeChain(this, "direct_blasting", 0);
    protected IGRecipeChain sulphideElectrowining = new IGRecipeChain(this, "sulphide_electrowining", 1);

    Set<Pair<Function<Integer, MaterialHelper>, Integer>> generation_group = new HashSet<>();
    private final LinkedHashSet<IGRecipeStage> stage_set = new LinkedHashSet<>();

    public GeologyMaterial() {
        // As long as the class itself is named appropriately we do not need to specify a name in the class.
        String className = this.getClass().getName().toLowerCase();
        String classNameNormal = this.getClass().getName();
        this.name = className.substring(className.lastIndexOf(".") + 1).replace("material", "");
        this.unserialized_name =  classNameNormal.substring(classNameNormal.lastIndexOf(".") + 1).replace("Material", "");

        this.generation_group.add(Pair.of((i) -> this, 100));
        this.colorFunction = materialColorFunction();
        initializeColorTint((p, integer) -> true); //default will be overridden later on in ClientProxy
        initializeFlags();
    }

    public BlockBehaviour.Properties getProperties(){return BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK);};

    public Set<IGRecipeChain> getRecipeChains()
    {
        return Set.of();
    };

    public void setRarity(Rarity rarity)
    {
        this.materialRarity = rarity;
    }

    public Rarity getRarity()
    {
        return materialRarity;
    }

    public void initializeFlags(){
        ArrayList<IFlagType<?>> flagList = new ArrayList<>();
        flagList.addAll(ConfigurationHelper.defaultItemFlags.apply(this));
        flagList.addAll(ConfigurationHelper.defaultBlockFlags.apply(this));

        for (IFlagType<?> flag : flagList) {
            addFlags(flag);
        }
    }

    // Used to check properties of the material
    protected void addFlags(IFlagType<?>... flags) {
        materialDataFlags.addAll(Arrays.asList(flags));
    }

    protected void removeMaterialFlags(IFlagType<?>... flags){
        Arrays.asList(flags).forEach(materialDataFlags::remove);
    }

    public boolean hasFlag(IFlagType<?> flag) {
        return materialDataFlags.contains(flag);
    }

    @Override
    public String getName() {
        return name.toLowerCase();
    }

    public int getColor(IFlagType<?> p, Integer secondaryColors) {
        return applyColorTint.test(p, secondaryColors) ? colorFunction.apply(p, secondaryColors) : 0xFFFFFFFF;
    }

    public void initializeColorTint(BiPredicate<IFlagType<?>, Integer> predicate) {
        applyColorTint = predicate;
    }

    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return (flag, integer) -> (0xffffffff);
    }

    public Set<IFlagType<?>> getFlags() {
        return materialDataFlags;
    }

    public ResourceLocation getTextureLocation(IFlagType<?> flag) {
        ResourceLocation texture = new ResourceLocation(IGLib.MODID, (flag instanceof ItemCategoryFlags ? "item" : "block") + "/colored/" + getName() + "/" + flag.toString().toLowerCase());

        // This function, is normally ONLY called during data generation
        // And the Existing File Helper is only available during it, hence we default to greyscale textures during runtime
        if(!DatagenModLoader.isRunningDataGen()) return greyScaleTextures(flag);
        if(EXISTING_HELPER == null) {
            logger.info("Existing File Helper is Null, unable to query if textures exist");
            return greyScaleTextures(flag);
        }

        boolean exists = EXISTING_HELPER.exists(new ResourceLocation(IGLib.MODID, "textures/" + texture.getPath() + ".png"), CLIENT_RESOURCES);
        return exists ? texture : greyScaleTextures(flag);
    }

    protected ResourceLocation greyScaleTextures(IFlagType<?> pattern)
    {
        if(pattern.getValue() instanceof BlockCategoryFlags b)
        {
            return switch(b)
            {
                case ORE_BLOCK ->
                {
                    String ore_overlay = getCrystalFamily()!=null?getCrystalFamily().getName(): "vanilla_normal";
                    yield new ResourceLocation(IGLib.MODID, "block/greyscale/rock/ore_bearing/vanilla/"+ore_overlay);
                }
                case STORAGE_BLOCK, STAIRS, SLAB, ENGINEERING_BLOCK, FENCE ->
                        new ResourceLocation(IGLib.MODID, "block/greyscale/metal/storage");
                case EVAPORATE -> new ResourceLocation(IGLib.MODID, "block/greyscale/evaporate/type_1");
                case SHEETMETAL_SLAB, SHEETMETAL_STAIRS, SHEETMETAL_BLOCK ->
                        new ResourceLocation(IGLib.MODID, "block/greyscale/metal/sheetmetal");
                case DUST_BLOCK -> new ResourceLocation(IGLib.MODID, "block/greyscale/metal/dust_block");
                case GEODE_BLOCK -> new ResourceLocation(IGLib.MODID, "block/greyscale/stone/geode");
                default -> new ResourceLocation(IGLib.MODID, "block/greyscale/stone/cobble");
            };
        }

        if(pattern.getValue() instanceof ItemCategoryFlags i)
        {
            switch(i)
            {
                case DIRTY_CRUSHED_ORE, CLAY, POWDERED_SLAG, CRUSHED_ORE ->
                {
                    return new ResourceLocation(IGLib.MODID, "item/greyscale/rock/"+i.getName());
                }
                case GEAR, INGOT, NUGGET, PLATE, SLAG, GRIT, POWDER, COMPOUND_DUST, TOOL_HOE ->
                {
                    return new ResourceLocation(IGLib.MODID, "palette/item/"+i.getName()+"/type_"+getPaletteVariation(i)+"_pristine_"+getName().toLowerCase());
                }
                case METAL_OXIDE ->
                {
                    return new ResourceLocation(IGLib.MODID, "palette/item/"+i.getName()+"/type_"+getPaletteVariation(i)+"_corroded_"+getName().toLowerCase());
                }
                case DRILL_HEAD ->
                {
                    return new ResourceLocation(IGLib.MODID, "palette/item/"+i.getName()+"/drill_pristine_"+getName().toLowerCase());
                }
                case ROD, WIRE ->
                {
                    return new ResourceLocation(IGLib.MODID, "item/greyscale/metal/"+i.getName());
                }
                case CRYSTAL ->
                {
                    return new ResourceLocation(IGLib.MODID, "item/greyscale/crystal/"+getCrystalFamily().getName());
                }
                case POOR_ORE, NORMAL_ORE, RICH_ORE ->
                {
                    return new ResourceLocation(IGLib.MODID, "item/greyscale/rock/"+i.getName()+"_"+getCrystalFamily().getName());
                }
                default ->
                {
                    return new ResourceLocation(IGLib.MODID, "item/greyscale/"+i.getName());
                }
            }
        }
        return null;
    }
    static Random rand = new Random(0);
    public int getPaletteVariation(ItemCategoryFlags flag){
        if(flag.equals(ItemCategoryFlags.INGOT))
        {
            return Math.min(6,1+(rand.nextInt(flag.getVariations()))%flag.getVariations());
        }
        return 1+(rand.nextInt(flag.getVariations()))%flag.getVariations();
    }

    protected Set<StoneFormation> acceptableStoneTypes = new HashSet<>();

    public boolean acceptableStoneType(MaterialStone stone)
    {
        return this.acceptableStoneTypes.contains(stone.getStoneFormation());
    }

    public boolean isValidStoneFormation(StoneFormation formation)
    {
        return this.acceptableStoneTypes.contains(formation);
    }

    public Set<StoneFormation> getValidStoneFormations()
    {
        return acceptableStoneTypes;
    }

    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.CUBIC;
    }


    // is chemical temp is to prevent acids from being viable in wooden barrels.
    public FluidType.Properties getFluidProperties(IFlagType<?> flag){
		return FluidType.Properties.create()
                .temperature(hasFlag(MaterialFlags.IS_MOLTEN_METAL) ? 2000 : hasFlag(MaterialFlags.IS_CHEMICAL) ? 600 : 0)
                .canSwim(true)
                .canDrown(false)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                .viscosity(100)
                .density(hasFlag(MaterialFlags.IS_MOLTEN_METAL) ? 3000 : (hasFlag(MaterialFlags.IS_GAS) ? -100 : 1000))
                .canPushEntity(true)
                .motionScale(hasFlag(MaterialFlags.IS_MOLTEN_METAL) ? 0.025f : 0.05f)
                .fallDistanceModifier(0.25f)
                .descriptionId("fluid.immersivegeology."+flag.getName().toLowerCase());
    }

    public static Consumer<FluidType.Properties> createBuildAttributes(int density, int viscosity, String name)
    {
        return builder -> builder.descriptionId(name).density(density).viscosity(viscosity);
    }

    private final Map<ModFlags, Map<IFlagType<?>, MaterialHelper>> EXISTING_IMPLEMENTATION_MAP = new HashMap<>();

    public Map<ModFlags, Map<IFlagType<?>, MaterialHelper>> getExistingImplementationMap()
    {
        return EXISTING_IMPLEMENTATION_MAP;
    }

    @Override
    public boolean checkExistingImplementation(IFlagType<?> h)
    {
        for(ModFlags m : ModFlags.values())
        {
            if(m.isStrictlyLoaded() && EXISTING_IMPLEMENTATION_MAP.containsKey(m)) {
                if(EXISTING_IMPLEMENTATION_MAP.get(m).containsKey(h)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean weakCheckExistingImplementation(IFlagType<?> h)
    {
        for(ModFlags m : ModFlags.values())
        {
            if(EXISTING_IMPLEMENTATION_MAP.containsKey(m)) {
                if(EXISTING_IMPLEMENTATION_MAP.get(m).containsKey(h)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkExistingImplementation(ModFlags m, IFlagType<?> h)
    {
        return m.isStrictlyLoaded() && EXISTING_IMPLEMENTATION_MAP.containsKey(m) && EXISTING_IMPLEMENTATION_MAP.get(m).containsKey(h);
    }

    public boolean hasExistingFlag(IFlagType<?> h)
    {
        return EXISTING_IMPLEMENTATION_MAP.entrySet().stream().anyMatch((e) -> e.getValue().containsKey(h));
    }

    @Override
    public void addExistingFlag(ModFlags m, ItemCategoryFlags... f){
        if(EXISTING_IMPLEMENTATION_MAP.containsKey(m))
        {
            for(ItemCategoryFlags flag : f) EXISTING_IMPLEMENTATION_MAP.get(m).put(flag, this);
            return;
        }

        HashMap<IFlagType<?>, MaterialHelper> map = new HashMap<>();
        for(ItemCategoryFlags flag : f) map.put(flag, this);
        EXISTING_IMPLEMENTATION_MAP.put(m, map);
    }

    @Override
    public void addExistingFlag(ModFlags m, BlockCategoryFlags... f){
        if(EXISTING_IMPLEMENTATION_MAP.containsKey(m))
        {
            for(BlockCategoryFlags flag : f) EXISTING_IMPLEMENTATION_MAP.get(m).put(flag, this);
            return;
        }

        HashMap<IFlagType<?>, MaterialHelper> map = new HashMap<>();
        for(BlockCategoryFlags flag : f) map.put(flag, this);
        EXISTING_IMPLEMENTATION_MAP.put(m, map);
    }

    public TagKey<Fluid> getFluidTag()
    {
        return getFluidTag(BlockCategoryFlags.FLUID);
    }

	public TagKey<Fluid> getFluidTag(BlockCategoryFlags flag)
	{
        HashMap<String,TagKey<Fluid>> data_map = IGTags.FLUID_TAG_HOLDER.get(flag);
        LinkedHashSet<MaterialHelper> material_set = new LinkedHashSet<>(Collections.singletonList(this));
        String key = IGTags.getWrapFromSet(material_set);
        return data_map.get(key);
	}

    @Nullable
    public TagKey<Fluid> getFluidTag(BlockCategoryFlags flag, MaterialInterface<?>... materials)
    {
        if(!IGTags.isInitialized()) throw new RuntimeException("Called getFluidTag before Tags have been Initialized");
        Set<MaterialHelper> helpers = Arrays.stream(materials)
                .map(MaterialInterface::instance)
                .collect(Collectors.toSet());

        return getFluidTag(flag, helpers.toArray(MaterialHelper[]::new));
    }

    @Nullable
    public TagKey<Fluid> getFluidTag(BlockCategoryFlags flag, MaterialHelper... materials)
    {
        if (!IGTags.isInitialized()) {
            throw new IllegalStateException("Called getFluidTag before Tags have been initialized");
        }

        // Convert the materials array into a Set of MaterialHelper
		HashSet<MaterialHelper> helpers = new HashSet<>(Arrays.asList(materials));

        // Get the mapping of fluid tags for the given flag
        Map<String, TagKey<Fluid>> fluidTagMap = IGTags.FLUID_TAG_HOLDER.get(flag);

        // Combine 'this' with the helper materials into a LinkedHashSet (preserves insertion order)
        LinkedHashSet<MaterialHelper> materialSet = new LinkedHashSet<>();
        materialSet.add(this);
        materialSet.addAll(helpers);

        // Create the lookup key based on the flag and material set
        String key = IGTags.getWrapFromSet(flag, materialSet);
        // If the fluid tag for this key is not present, try initializing
        if (!fluidTagMap.containsKey(key)) {
            IGTags.initialize();
            boolean initializationSuccessful = fluidTagMap.containsKey(key);
            if (!initializationSuccessful) {
                String materialNames = materialSet.stream()
                        .map(MaterialHelper::getName)
                        .collect(Collectors.joining(", "));
                String errorMsg = String.format(
                        "Failed to initialize Fluid Tags: %s %s",
                        flag.name(),
                        materialNames
                );
                throw new IllegalStateException(errorMsg);
            }
        }

        return fluidTagMap.get(key);
    }

    @Override
    public Set<IGRecipeStage> getMaterialStageSet()
    {
        return stage_set;
    }

    @Override
    public void addStage(IGRecipeStage stage)
    {
        this.stage_set.add(stage);
    }

    public MaterialInterface<?> getPrimaryProduct()
    {
        LinkedHashSet<MaterialInterface<?>> set =  getDerivedMaterials();
        if(set.isEmpty()) {
            IGLib.IG_LOGGER.error("Called a Primary Use (product) Source Material with no Entry [{}]", getName());
            return MetalEnum.Unobtanium;
        }

        List<MaterialInterface<?>> list = set.stream().toList();
        return list.get(0);
    }

    public MaterialInterface<?> getSecondaryProduct()
    {
        LinkedHashSet<MaterialInterface<?>> set =  getDerivedMaterials();
        if(set.size() < 2) {
            IGLib.IG_LOGGER.error("Called a Secondary Source (Byproduct) Material with no Entry [{}]", getName());
            return MetalEnum.Unobtanium;
        }

        List<MaterialInterface<?>> list = set.stream().toList();
        return list.get(1);
    }

    public MaterialInterface<?> getTraceProduct(int index)
    {
        LinkedHashSet<MaterialInterface<?>> set =  getDerivedMaterials();
        if(set.size() < (index + 1)) {
            IGLib.IG_LOGGER.error("Called a Trace Material with no Entry [{}]", getName());
            return MetalEnum.Unobtanium;
        }
        List<MaterialInterface<?>> list = set.stream().toList();
        return list.get(index);
    }

    private float asocialMaterialChance = 1f;
    public void setAsocialMaterialChance(float chance)
    {
        asocialMaterialChance = chance;
    }

    public float getAssociateMaterialChance()
    {
        return asocialMaterialChance;
    }

    public void addGenerationFriend(Function<Integer, MaterialHelper> material, int chance)
    {
        generation_group.add(Pair.of(material, chance));
    }

    public Set<Pair<Function<Integer, MaterialHelper>, Integer>> getAssociateMaterialSet()
    {
        return generation_group;
    }

    private int burntime = 0;

    public void setBurntime(int burntime){
        this.burntime = burntime;
    }

    public int getBurntime()
    {
        return burntime;
    }

    public boolean canBurn()
    {
        return burntime > 0;
    }

	public float getNoiseProbability()
	{
        return 0;
	}

    public void fluidTick(Level level, BlockPos pos, FluidState state)
    {

    }

    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        if(hasFlag(MaterialFlags.IS_MOLTEN_METAL))
        {
            if(!entity.fireImmune()) entity.setSecondsOnFire(2);
        }
    }

    public boolean fluidSpreadEvent(LevelAccessor level, BlockPos pos, BlockState state, Direction direction, FluidState fluidState)
    {
        return false;
    }

    public boolean canFormMB(IMultiblock multiblock)
    {
        return validMultiblocks.stream().anyMatch(s -> s.get().equals(multiblock));
    }

    public TagKey<Item> getItemMaterialTag()
    {
        return IGTags.ITEM_MATERIAL_HOLDER.get(this);
    }

    public TagKey<Block> getBlockMaterialTag()
    {
        return IGTags.BLOCK_MATERIAL_HOLDER.get(this);
    }

    public ItemStack getOreIcon()
    {
        if(acceptableStoneType(StoneEnum.MCStone)) return new ItemStack(getOreBlock(StoneEnum.MCStone, OreRichness.NORMAL).asIGItem());
        if(acceptableStoneType(StoneEnum.MCNetherrack)) return new ItemStack(getOreBlock(StoneEnum.MCNetherrack, OreRichness.NORMAL).asIGItem());
        if(acceptableStoneType(StoneEnum.MCEndStone)) return new ItemStack(getOreBlock(StoneEnum.MCEndStone, OreRichness.NORMAL).asIGItem());
        return ItemStack.EMPTY;
    }
}
