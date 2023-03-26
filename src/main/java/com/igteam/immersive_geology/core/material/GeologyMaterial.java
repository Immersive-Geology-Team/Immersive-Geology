package com.igteam.immersive_geology.core.material;

import com.google.common.collect.Sets;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.block.helper.StaticTemplateManager;
import com.igteam.immersive_geology.common.configuration.CommonConfiguration;
import com.igteam.immersive_geology.common.configuration.helper.ConfigurationHelper;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.material.helper.BlockCategoryFlags;
import com.igteam.immersive_geology.core.material.helper.IFlagType;
import com.igteam.immersive_geology.core.material.helper.ItemCategoryFlags;
import com.igteam.immersive_geology.core.material.helper.MaterialHelper;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static net.minecraft.server.packs.PackType.CLIENT_RESOURCES;

public abstract class GeologyMaterial implements MaterialHelper {
    protected String name;
    protected Logger logger = ImmersiveGeology.getNewLogger();
    protected Function<IFlagType<?>, Integer> colorFunction; // in goes a category, returns the color white as a default
    protected Predicate<IFlagType<?>> applyColorTint;
    private final Set<IFlagType<?>> materialDataFlags = Sets.newHashSet();

    public GeologyMaterial(String name) {
        this.name = name;
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
        for (IFlagType<?> flag : flags) {
            materialDataFlags.add(flag);
        }
    }

    protected void removeMaterialFlags(IFlagType<?>... flags){
        for (IFlagType<?> flag : flags) {
            materialDataFlags.remove(flag);
        }
    }

    protected boolean hasFlag(IFlagType<?> flag) {
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

        boolean exists = StaticTemplateManager.EXISTING_HELPER.exists(new ResourceLocation(IGLib.MODID, "textures/" + texture.getPath() + ".png"), CLIENT_RESOURCES);

        return exists ? texture : greyScaleTextures(flag);
    }

    private ResourceLocation greyScaleTextures(IFlagType<?> pattern) {
        if (pattern.getValue() instanceof BlockCategoryFlags b){
            switch (b) {
                case ORE_BLOCK:
                    return new ResourceLocation(IGLib.MODID, "block/greyscale/rock/ore_bearing/vanilla/vanilla_normal");
                case STORAGE_BLOCK:
                    return new ResourceLocation(IGLib.MODID, "block/greyscale/metal/storage");
                case SLAB:
                case SHEETMETAL_BLOCK:
                    return new ResourceLocation(IGLib.MODID, "block/greyscale/metal/sheetmetal");
                case DUST_BLOCK:
                    return new ResourceLocation(IGLib.MODID, "block/greyscale/metal/dust_block");
                case GEODE_BLOCK:
                    return new ResourceLocation(IGLib.MODID, "block/greyscale/stone/geode");
                default:
                    return new ResourceLocation(IGLib.MODID, "block/greyscale/stone/cobble");
            }
        }

        if (pattern.getValue() instanceof ItemCategoryFlags i) {
            switch (i) {
                case RAW_ORE -> {
                    String chunk_vein_name = "rock_chunk_vein";// + (getCrystalFamily() != null ? "_" + getCrystalFamily().getName() : "");
                    return new ResourceLocation(IGLib.MODID, "item/greyscale/rock/ore_chunk/" + chunk_vein_name);
                }
                case DIRTY_CRUSHED_ORE, CRUSHED_ORE -> {
                    return new ResourceLocation(IGLib.MODID, "item/greyscale/rock/crushed_ore");
                }
                case CLAY, SLAG -> {
                    return new ResourceLocation(IGLib.MODID, "item/greyscale/rock/" + i.name());
                }
                case DUST, GEAR, INGOT, NUGGET, PLATE, ROD, WIRE, METAL_OXIDE, COMPOUND_DUST -> {
                    return new ResourceLocation(IGLib.MODID, "item/greyscale/metal/" + i.name());
                }
                case CRYSTAL -> {
                    return new ResourceLocation(IGLib.MODID, "item/greyscale/crystal/raw_crystal_cubic");// + getCrystalFamily().getName());
                }
                default -> {
                    return new ResourceLocation(IGLib.MODID, "item/greyscale/" + i.name());
                }
            }
        }
        return null;
    }
}
