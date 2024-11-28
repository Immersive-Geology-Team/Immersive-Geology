/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.enums;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.stone.compat.adastra.*;
import com.igteam.immersivegeology.core.material.data.stone.compat.tfc.*;
import com.igteam.immersivegeology.core.material.data.stone.compat.tfc.MaterialAndesite;
import com.igteam.immersivegeology.core.material.data.stone.compat.tfc.MaterialDiorite;
import com.igteam.immersivegeology.core.material.data.stone.compat.tfc.MaterialGranite;
import com.igteam.immersivegeology.core.material.data.stone.vanilla.*;
import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.mojang.serialization.Codec;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.TargetBlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.Arrays;
import java.util.List;

public enum StoneEnum implements MaterialInterface<MaterialStone> {
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
    Netherrack(new MaterialMCNetherrack()),
    Dripstone(new MaterialMCDripstone()),
    Sandstone(new MaterialMCSandstone()),

    //===== Minecraft Sands =====\\
    //Sand(new MaterialSand()),

    //===== Beyond Earth / Ad Astra =====\\
    MoonStone(new MaterialMoonStone()),
    MarsStone(new MaterialMarsStone()),
    MercuryStone(new MaterialMercuryStone()),
    VenusStone(new MaterialVenusStone()),
    GlacioStone(new MaterialGlacioStone());

    public static final Codec<StoneEnum> CODEC = Codec.STRING.xmap(StoneEnum::valueOf, Enum::name);

    private final MaterialStone material;
    StoneEnum(MaterialStone m){
        this.material = m;
    }

    public static StoneEnum selectWorldState(BlockState stoneState)
    {
        String stoneName = capitalizeFirstLetter(stoneState.getBlock().getName().getString().toLowerCase());
        if(Arrays.stream(values()).anyMatch(stoneEnum -> stoneEnum.name().equalsIgnoreCase("MC" + stoneName))) return valueOf("MC" + stoneName);
        if(Arrays.stream(values()).anyMatch(stoneEnum -> stoneEnum.name().equalsIgnoreCase(stoneName))) return valueOf(stoneName);
        if(Arrays.stream(values()).anyMatch(stoneEnum -> stoneEnum.name().equalsIgnoreCase(stoneName.replace("Block", "").trim()))) return valueOf(stoneName.replace("Block", "").trim());

        // Now for TFC Compat checks
        if(Arrays.stream(values()).anyMatch(stoneEnum -> stoneEnum.name().equalsIgnoreCase(stoneName.substring(stoneName.lastIndexOf('/')+1)))) return valueOf(stoneName);


        return null;
    }

    private static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input; // Return input as is if it's null or empty
        }
        // Convert first character to uppercase and concatenate with the rest of the string
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    @Override
    public MaterialStone instance() {
        return material;
    }

	public List<TargetBlockState> getTargets(MineralEnum mineral)
	{
        return instance().getTargets(mineral);
	}

    public boolean isWorldState(BlockState stoneState)
    {
        return stoneState.getBlock().getName().getString().equalsIgnoreCase(material.getName());
    }
}
