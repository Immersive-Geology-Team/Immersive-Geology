package com.igteam.immersive_geology.core.material;

import com.google.common.collect.Sets;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.block.helper.StaticTemplateManager;
import com.igteam.immersive_geology.common.configuration.helper.ConfigurationHelper;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersive_geology.core.material.helper.flags.IFlagType;
import com.igteam.immersive_geology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersive_geology.core.material.helper.material.CrystalFamily;
import com.igteam.immersive_geology.core.material.helper.material.MaterialHelper;
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

    protected ResourceLocation greyScaleTextures(IFlagType<?> pattern) {
        if (pattern.getValue() instanceof BlockCategoryFlags b){
            switch (b) {
                case ORE_BLOCK:
                    String ore_overlay = getCrystalFamily() != null ? getCrystalFamily().getName() : "vanilla_normal";
                    return new ResourceLocation(IGLib.MODID, "block/greyscale/rock/ore_bearing/vanilla/" + ore_overlay);
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
                case DIRTY_CRUSHED_ORE, CRUSHED_ORE, CLAY, SLAG -> {
                    return new ResourceLocation(IGLib.MODID, "item/greyscale/rock/" + i.getName());
                }
                case DUST, GEAR, INGOT, NUGGET, PLATE, ROD, WIRE, METAL_OXIDE, COMPOUND_DUST -> {
                    return new ResourceLocation(IGLib.MODID, "item/greyscale/metal/" + i.getName());
                }
                case CRYSTAL -> {
                    return new ResourceLocation(IGLib.MODID, "item/greyscale/crystal/" + getCrystalFamily().getName());
                }
                case RAW_ORE -> {
                    return new ResourceLocation(IGLib.MODID, "item/greyscale/rock/raw_ore/" + i.getName() + "_" + getCrystalFamily().getName());
                }
                default -> {
                    return new ResourceLocation(IGLib.MODID, "item/greyscale/" + i.getName());
                }
            }
        }
        return null;
    }

    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.CUBIC;
    }
}
