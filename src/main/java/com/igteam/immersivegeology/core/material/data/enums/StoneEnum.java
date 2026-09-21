/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.enums;

import com.igteam.immersivegeology.core.lib.shim.MCShims.Codec;

import com.igteam.immersivegeology.core.lib.shim.MCShims.TargetBlockState;

import net.minecraft.block.state.IBlockState;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.stone.compat.adastra.*;
import com.igteam.immersivegeology.core.material.data.stone.compat.tfc.*;
import com.igteam.immersivegeology.core.material.data.stone.compat.tfc.MaterialAndesite;
import com.igteam.immersivegeology.core.material.data.stone.compat.tfc.MaterialDiorite;
import com.igteam.immersivegeology.core.material.data.stone.compat.tfc.MaterialGranite;
import com.igteam.immersivegeology.core.material.data.stone.vanilla.*;
import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.IStoneType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.util.ResourceLocation;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public enum StoneEnum implements IStoneType {
    //===== Terra Firma Craft =====\\
    Andesite(new MaterialAndesite()),
    Basalt(new MaterialBasalt()),
    Dacite(new MaterialDacite()),
    Rhyolite(new MaterialRhyolite()),

    Diorite(new MaterialDiorite()),
    Gabbro(new MaterialGabbro()),
    Granite(new MaterialGranite()),

    Gneiss(new MaterialGneiss()),
    Marble(new MaterialMarble()),
    Phyllite(new MaterialPhyllite()),
    Quartzite(new MaterialQuartzite()),
    Schist(new MaterialSchist()),
    Slate(new MaterialSlate()),

    Chalk(new MaterialChalk()),
    Chert(new MaterialChert()),
    Claystone(new MaterialClaystone()),
    Conglomerate(new MaterialConglomerate()),
    Dolomite(new MaterialDolomite()),
    Limestone(new MaterialLimestone()),
    Shale(new MaterialShale()),


    //===== Minecraft Stones =====\\
    MCStone(new MaterialVanilla()),
    MCDeepslate(new MaterialMCDeepslate()),
    MCAndesite(new MaterialMCAndesite()),
    MCDiorite(new MaterialMCDiorite()),
    MCGranite(new MaterialMCGranite()),
    MCBasalt(new MaterialMCBasalt()),
    MCEndStone(new MaterialMCEndStone()),
    MCNetherrack(new MaterialMCNetherrack()),
    MCDripstone(new MaterialMCDripstone()),
    MCSandstone(new MaterialMCSandstone());

    //===== Minecraft Sands =====\\
    //Sand(new MaterialSand()),

    //===== Beyond Earth / Ad Astra =====\\
    //MoonStone(new MaterialMoonStone()),
    //MarsStone(new MaterialMarsStone()),
    //MercuryStone(new MaterialMercuryStone()),
    //VenusStone(new MaterialVenusStone()),
    //GlacioStone(new MaterialGlacioStone());

    public static final Codec<StoneEnum> CODEC = Codec.STRING.xmap(StoneEnum::valueOf, Enum::name);
    private static final Map<String, StoneEnum> stoneEnumMap = new HashMap<>();

    static {
        for (StoneEnum stoneEnum : values()) {
            stoneEnumMap.put(stoneEnum.name().toLowerCase(Locale.ROOT), stoneEnum);
        }
    }
    private final MaterialStone material;
    StoneEnum(MaterialStone m){
        this.material = m;
    }
    /**
     * Resolved stone type per block, so the name matching below runs once for each block the world contains
     * rather than once for every block world generation samples.
     * <p>
     * Blocks are registry singletons and the match depends only on the block's description id, so a result is
     * good for the lifetime of the game. Empty means the block is not a stone type Immersive Geology knows.
     */
    private static final Map<Block, Optional<StoneEnum>> worldStateCache = new ConcurrentHashMap<>();

    public static StoneEnum selectWorldState(IBlockState stoneState) {
        Block block = stoneState.getBlock();
        Optional<StoneEnum> cached = worldStateCache.get(block);
        if(cached==null)
        {
            cached = worldStateCache.computeIfAbsent(block, b -> Optional.ofNullable(resolveWorldState(b)));
        }
        return cached.orElse(null);
    }

    private static StoneEnum resolveWorldState(Block block) {
        try
        {
            String name = block.getTranslationKey().toLowerCase(Locale.ROOT);
            String stoneName = capitalizeFirstLetter(name.substring(name.lastIndexOf('.')+1));

            // Check for Minecraft stones first
            if(name.contains(ModFlags.MINECRAFT.getName()))
            {
                return stoneEnumMap.get("mc"+stoneName.toLowerCase(Locale.ROOT));
            }

            // Try direct lookup of the stone name
            StoneEnum result = stoneEnumMap.get(stoneName.toLowerCase(Locale.ROOT));
            if(result!=null)
            {
                return result;
            }
        } catch(Exception ex) {
            IGLib.IG_LOGGER.warn("Unable to find matching stone type for ore {}", ex.getMessage());
        }

        return null;
    }

    private static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input; // Return input as is if it's null or empty
        }
        // Convert first character to uppercase and concatenate with the rest of the string
        return input.substring(0, 1).toUpperCase(Locale.ROOT) + input.substring(1);
    }

    @Override
    public MaterialStone instance() {
        return material;
    }

    @Override
    public int index() {
        return ordinal();
    }

    public List<TargetBlockState> getTargets(MineralEnum mineral)
	{
        return instance().getTargets(mineral);
	}

    public boolean isWorldState(IBlockState stoneState)
    {
        return stoneState.getBlock().getTranslationKey().equalsIgnoreCase(material.getName());
    }

    public String getTFCStoneLoc()
    {
        return instance().getTFCStoneLoc();
    }

    public boolean isStoneTypeValid()
    {
        List<ModFlags> list = getFlags().stream().filter(f -> f instanceof ModFlags).map(flag -> (ModFlags) flag).toList();
        if(list.isEmpty()) return true;

        boolean pass = false;
        for(ModFlags flag : list)
        {
            pass = flag.isStrictlyLoaded();
        }

        return pass;
    }

    public boolean isVanilla()
    {
        return getFlags().contains(ModFlags.MINECRAFT);
    }

    /**
     * Derived from the mod flags, exactly as the registry key builder used to derive it inline. Last flag wins,
     * which is the behaviour the existing block ids were generated with.
     */
    @Override
    public String getRegistryPrefix()
    {
        String prefix = "";
        for(ModFlags modflag : ModFlags.values())
        {
            if(hasFlag(modflag)) prefix = modflag.name().toLowerCase(Locale.ROOT)+"_";
        }
        return prefix;
    }

    @Override
    public Set<ResourceLocation> getDimensions()
    {
        return instance().getDimensions();
    }

    /**
     * Only Minecraft's own stone. TerraFirmaCraft's rock is present in a TFC world and absent from a vanilla one
     * even when the mod is loaded, so it goes through the world-type check in IGTFCWorld instead.
     */
    @Override
    public boolean declaresPresence()
    {
        return isVanilla();
    }
}
