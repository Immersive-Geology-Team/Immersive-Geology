/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.enums;

import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.common.block.helper.MineralWeathering;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.common.world.IWorldGenConfig;
import com.igteam.immersivegeology.core.material.data.chemical.mantle.MaterialMoltenMantle;
import com.igteam.immersivegeology.core.material.data.types.MaterialMetal;
import com.igteam.immersivegeology.core.material.data.metal.*;
import com.igteam.immersivegeology.core.material.helper.ScaffoldingHelper;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.mojang.datafixers.util.Pair;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.function.Supplier;

public enum MetalEnum implements MaterialInterface<MaterialMetal>, IWorldGenConfig
{
    Aluminum(new MaterialAluminum()),
    Bronze(new MaterialBronze()),
    Chromium(new MaterialChromium()),
    Cobalt(new MaterialCobalt()),
    Copper(new MaterialCopper()),
    Gold(new MaterialGold()),
    Iron(new MaterialIron()),
    Lead(new MaterialLead()),
    Manganese(new MaterialManganese()),
    Magnesium(new MaterialMagnesium()),
    Molybdenum(new MaterialMolybdenum()),
    Neodymium(new MaterialNeodymium()),
    Nickel(new MaterialNickel()),
    Osmium(new MaterialOsmium()),
    Platinum(new MaterialPlatinum()),
    Hastelloy(new MaterialHastelloy()),
    Silver(new MaterialSilver()),
    Steel(new MaterialSteel()),
    StainlessSteel(new MaterialStainlessSteel()),
    Sodium(new MaterialSodium()),
    Thorium(new MaterialThorium()),
    Tin(new MaterialTin()),
    Titanium(new MaterialTitanium()),
    Tungsten(new MaterialTungsten()),
    Unobtanium(new MaterialUnobtanium()),
    Uranium(new MaterialUranium()),
    Vanadium(new MaterialVanadium()),
    Zinc(new MaterialZinc()),
    Zirconium(new MaterialZirconium()),
    TungstenCarbide(new MaterialTungstenCarbide()),
    // Mantle Fluid
    MoltenMantle(new MaterialMoltenMantle());

    public static List<? extends IWorldGenConfig> nativeMetals()
    {
        return Arrays.stream(values()).filter(v -> v.hasFlag(BlockCategoryFlags.ORE_BLOCK)).toList();
    }

    public static List<? extends IWorldGenConfig> scaffoldingMetals()
    {
        return Arrays.stream(values()).filter(v -> v.hasFlag(BlockCategoryFlags.SCAFFOLDING)).toList();
    }

    public static List<String> getAtlasPermutations()
    {
        List<String> permutations = new ArrayList<>();
        nativeMetals().forEach((e) ->
        {
            for(MineralWeathering weathering : MineralWeathering.values())
            {
                permutations.add(weathering.name().toLowerCase() + "/" + e.getName());
            }
        });

        return permutations;
    }

    public static List<String> getAtlasScaffoldingPermutations()
    {
        List<String> permutations = new ArrayList<>();
        scaffoldingMetals().forEach((e) ->
        {
            permutations.add(MineralWeathering.PRISTINE.name().toLowerCase() + "/" + e.getName());
        });

        return permutations;
    }

    private final MaterialMetal material;
    MetalEnum(MaterialMetal m){
        this.material = m;
    }
    @Override
    public MaterialMetal instance() {
        return material;
    }

    @Override
    public String getName()
    {
        return material.getName();
    }

    @Override
    public double getAssociateMaterialChance()
    {
        return material.getAssociateMaterialChance();
    }

    @Override
    public Set<Pair<Supplier<MaterialHelper>, Integer>> getAssociateMaterialSet()
    {
        return instance().getAssociateMaterialSet();
    }

    @Override
    public int getVeinSize()
    {
        return material.CONFIG.veinSize();
    }

    @Override
    public int getMinY()
    {
        return material.CONFIG.minY();
    }

    @Override
    public int getMaxY()
    {
        return material.CONFIG.maxY();
    }

    @Override
    public boolean useSparsePlacement()
    {
        return material.CONFIG.useSparsePlacement();
    }

    @Override
    public int veinsPerChunk()
    {
        return material.CONFIG.veinsPerChunk();
    }

    @Override
    public int rarity()
    {
        return material.CONFIG.rarity();
    }

    @Override
    public Optional<TagKey<Biome>> getPreferredBiome()
    {
        return material.CONFIG.preferredBiome();
    }

    @Override
    public IOreBlock getOreBlock(StoneEnum stone, OreRichness richness)
    {
        return material.getOreBlock(stone, richness);
    }

    @Override
    public BlockState getDefaultBlockstate()
    {
        return null;
    }

    @Override
    public IOreBlock getOreBlock(MaterialHelper stone, OreRichness richness)
    {
        return material.getOreBlock(stone, richness);
    }

    @Override
    public int generationChance()
    {
        return material.CONFIG.generationChance();
    }

    @Override
    public double density()
    {
        return material.CONFIG.density();
    }

    public ScaffoldingHelper getScaffoldingBlock(){
        return instance().getScaffoldingBlock();
    }
}
