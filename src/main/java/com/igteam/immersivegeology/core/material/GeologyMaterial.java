package com.igteam.immersivegeology.core.material;

import blusunrize.immersiveengineering.api.EnumMetals;
import blusunrize.immersiveengineering.api.IETags;
import com.google.common.collect.Sets;
import com.igteam.immersivegeology.common.tag.IGTags;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.configuration.ConfigurationHelper;
import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static net.minecraft.server.packs.PackType.CLIENT_RESOURCES;

public abstract class GeologyMaterial implements MaterialHelper {
    public static ExistingFileHelper EXISTING_HELPER;
    protected String name;
    protected Logger logger = IGLib.getNewLogger();
    protected Function<IFlagType<?>, Integer> colorFunction; // in goes a category, returns the color white as a default
    protected Predicate<IFlagType<?>> applyColorTint; // In a goes the flag, returns if it uses programmed color tint
    private final LinkedHashSet<IFlagType<?>> materialDataFlags = Sets.newLinkedHashSet();

    public GeologyMaterial() {
        // As long as the class itself is named appropriately we do not need to specify a name in the class.
        String className = this.getClass().getName().toLowerCase();
        this.name = className.substring(className.lastIndexOf(".") + 1).replace("material", "");

        this.colorFunction = materialColorFunction();
        initializeColorTint((p) -> true); //default will be overridden later on in ClientProxy
        initializeFlags();
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

    public int getColor(IFlagType<?> p) {
        return applyColorTint.test(p) ? colorFunction.apply(p) : 0xFFFFFF;
    }

    public void initializeColorTint(Predicate<IFlagType<?>> predicate) {
        applyColorTint = predicate;
    }

    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return (flag) -> (0xffffff);
    }

    public Set<IFlagType<?>> getFlags() {
        return materialDataFlags;
    }

    public ResourceLocation getTextureLocation(IFlagType<?> flag) {
        ResourceLocation texture = new ResourceLocation(IGLib.MODID, "block/colored/" + getName() + "/" + flag.toString().toLowerCase());

        if (flag.getValue() instanceof ItemCategoryFlags iFlag) {
            texture = new ResourceLocation(IGLib.MODID, "item/colored/" + this.name + "/" + iFlag.name().toLowerCase());
        }

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

    protected ResourceLocation greyScaleTextures(IFlagType<?> pattern) {
        if (pattern.getValue() instanceof BlockCategoryFlags b){
            return switch (b) {
                case ORE_BLOCK -> {
                    String ore_overlay = getCrystalFamily() != null ? getCrystalFamily().getName() : "vanilla_normal";
                    yield new ResourceLocation(IGLib.MODID, "block/greyscale/rock/ore_bearing/vanilla/" + ore_overlay);
                }
                case STORAGE_BLOCK -> new ResourceLocation(IGLib.MODID, "block/greyscale/metal/storage");
                case SLAB, SHEETMETAL_BLOCK -> new ResourceLocation(IGLib.MODID, "block/greyscale/metal/sheetmetal");
                case DUST_BLOCK -> new ResourceLocation(IGLib.MODID, "block/greyscale/metal/dust_block");
                case GEODE_BLOCK -> new ResourceLocation(IGLib.MODID, "block/greyscale/stone/geode");
                default -> new ResourceLocation(IGLib.MODID, "block/greyscale/stone/cobble");
            };
        }

        if (pattern.getValue() instanceof ItemCategoryFlags i) {

            switch (i) {
                case DIRTY_CRUSHED_ORE, CRUSHED_ORE, CLAY, SLAG -> {
                    return new ResourceLocation(IGLib.MODID, "item/greyscale/rock/" + i.getName());
                }
                case DUST, GEAR, INGOT, NUGGET, PLATE, ROD, WIRE, METAL_OXIDE, COMPOUND_DUST -> {
                    return new ResourceLocation(IGLib.MODID, "item/greyscale/metal/" + i.getName());
                }
                case CRYSTAL -> {
                    return new ResourceLocation(IGLib.MODID, "item/greyscale/crystal/" + getCrystalFamily().getName());
                }
                case POOR_ORE, NORMAL_ORE, RICH_ORE -> {
                    return new ResourceLocation(IGLib.MODID, "item/greyscale/rock/" + i.getName() + "_" + getCrystalFamily().getName());
                }
                default -> {
                    return new ResourceLocation(IGLib.MODID, "item/greyscale/" + i.getName());
                }
            }
        }
        return null;
    }

    protected Set<StoneFormation> acceptableStoneTypes = new HashSet<>();

    public boolean acceptableStoneType(MaterialStone stone)
    {
        return this.acceptableStoneTypes.contains(stone.getStoneFormation());
    }

    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.CUBIC;
    }

    public TagKey<Item> getItemTag(IFlagType<ItemCategoryFlags> itemFlag)
    {
        // Override for block items
        try
        {
            EnumMetals IEMetal = EnumMetals.valueOf(this.name.toUpperCase());
            IETags.MetalTags ieMetalTags = IETags.getTagsFor(IEMetal);

            switch(itemFlag.getValue())
            {
                case INGOT ->
                {
                    return ieMetalTags.ingot;
                }
                case DUST ->
                {
                    return ieMetalTags.dust;
                }
                case NUGGET ->
                {
                    return ieMetalTags.nugget;
                }
                case PLATE ->
                {
                    return ieMetalTags.plate;
                }
            }
        } catch(Exception ignored){};

        HashMap<String,TagKey<Item>> data_map = IGTags.ITEM_TAG_HOLDER.get(itemFlag);
        LinkedHashSet<GeologyMaterial> material_set = new LinkedHashSet<>(Collections.singletonList(this));
        String key = IGTags.getWrapFromSet(material_set);
        IGLib.IG_LOGGER.info("Getting Tag: " + key);
        return data_map.get(key);
    }

    public FluidType.Properties getFluidProperties(IFlagType<?> flag){
        return FluidType.Properties.create().adjacentPathType(BlockPathTypes.LAVA).density(15).lightLevel(15).supportsBoating(false).canPushEntity(true).descriptionId("fluid.immersivegeology.fluid");
    }

    public IClientFluidTypeExtensions getFluidExtendedProperties(BlockCategoryFlags flag)
    {
        GeologyMaterial material = this;
        return new IClientFluidTypeExtensions()
        {
            @Override
            public int getTintColor()
            {
                return material.getColor(flag);
            }

            @Override
            public ResourceLocation getStillTexture()
            {
                return new ResourceLocation(IGLib.MODID, "block/fluid/default_still");
            }

            @Override
            public ResourceLocation getFlowingTexture()
            {
                return new ResourceLocation(IGLib.MODID, "block/fluid/default_flowing");
            }

            @Override
            public @Nullable ResourceLocation getOverlayTexture()
            {
                return new ResourceLocation(IGLib.MODID, "block/fluid/default_flowing");
            }


        };
    }
}
