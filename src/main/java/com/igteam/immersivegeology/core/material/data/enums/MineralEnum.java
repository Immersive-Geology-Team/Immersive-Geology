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
import com.igteam.immersivegeology.common.world.features.helper.IGGenerationType;
import com.igteam.immersivegeology.core.material.data.mineral.*;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.mojang.datafixers.util.Pair;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.function.Supplier;

public enum MineralEnum implements MaterialInterface<MaterialMineral>, IWorldGenConfig
{
    Acanthite(new MaterialAcanthite()),
    Alumina(new MaterialAlumina()),
    Anatase(new MaterialAnatase()),
    Bauxite(new MaterialBauxite()),
    Cassiterite(new MaterialCassiterite()),
    Carnallite(new MaterialCarnallite()),
    Chalcocite(new MaterialChalcocite()),
    Chalcopyrite(new MaterialChalcopyrite()),
    Chromite(new MaterialChromite()),
    Cobaltite(new MaterialCobaltite()),
    Cryolite(new MaterialCryolite()),
    Cuprite(new MaterialCuprite()),
    Fluorite(new MaterialFluorite()),
    Galena(new MaterialGalena()),
    Gypsum(new MaterialGypsum()),
    Hematite(new MaterialHematite()),
    Ilmenite(new MaterialIlmenite()),
    Magnetite(new MaterialMagnetite()),
    Molybdenite(new MaterialMolybdenite()),
    Monazite(new MaterialMonazite()),
    Pyrite(new MaterialPyrite()),
    Pyrolusite(new MaterialPyrolusite()),
    Saltpeter(new MaterialSaltpeter()),
    Scheelite(new MaterialScheelite()),
    Smithsonite(new MaterialSmithsonite()),
    Sphalerite(new MaterialSphalerite()),
    Rocksalt(new MaterialRocksalt()),
    Thorianite(new MaterialThorianite()),
    Thorite(new MaterialThorite()),
    Millerite(new MaterialMillerite()),
    Unobtania(new MaterialUnobtania()),
    Uraninite(new MaterialUraninite()),
    Vanadinite(new MaterialVanadinite()),
    Wolframite(new MaterialWolframite()),
    Zircon(new MaterialZircon());

    private final MaterialMineral material;
    MineralEnum(MaterialMineral m){
        this.material = m;
    }

	public static List<String> getAtlasPermutations()
	{
        List<String> permutations = new ArrayList<>();
        Arrays.stream(values()).forEach((e) ->
        {
            for(MineralWeathering weathering : MineralWeathering.values())
            {
                permutations.add(weathering.name().toLowerCase()+"/"+e.getName());
            }
        });

        return permutations;
	}

	@Override
    public MaterialMineral instance() {
        return material;
    }

    @Override
    public String getName()
    {
        return material.getName();
    }

    @Override
    public IOreBlock getOreBlock(StoneEnum stone, OreRichness richness)
    {
        if(material.getOreBlock(stone, richness) == null) return material.getOreBlock(StoneEnum.MCStone, richness);
        return material.getOreBlock(stone, richness);
    }

    @Override
    public BlockState getDefaultBlockstate()
    {
        return material.getBlock(BlockCategoryFlags.EVAPORATE).defaultBlockState();
    }

    @Override
    public IOreBlock getOreBlock(MaterialHelper stone, OreRichness richness)
    {
        if(material.getOreBlock(stone, richness) == null) return material.getOreBlock(StoneEnum.MCStone, richness);
        return material.getOreBlock(stone, richness);
    }

    public int getVeinSize()
	{
        return material.CONFIG.veinSize();
	}

    public int getMinY()
    {
        return material.CONFIG.minY();
    }

    public int getMaxY()
    {
        return material.CONFIG.maxY();
    }

    public int veinsPerChunk()
    {
        return material.CONFIG.veinsPerChunk();
    }

    public int rarity()
    {
        return material.CONFIG.rarity();
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

    @Override
    public boolean useSparsePlacement()
    {
        return material.CONFIG.useSparsePlacement();
    }

    public Optional<TagKey<Biome>> getPreferredBiome()
    {
        return material.CONFIG.preferredBiome();
    }

    @Override
    public IGGenerationType getGenerationType()
    {
        return material.CONFIG.generationType();
    }

    @Override
    public double getAssociateMaterialChance()
    {
        return instance().getAssociateMaterialChance();
    }

    @Override
    public Set<Pair<Supplier<MaterialHelper>, Integer>> getAssociateMaterialSet()
    {
        return instance().getAssociateMaterialSet();
    }

    @Override
    public double getMinSpawnTemp()
    {
        return -1;
    }

    @Override
    public double getMaxSpawnTemp()
    {
        return 2;
    }

    @Override
    public double getMinDownfall()
    {
        return 0;
    }

    @Override
    public double getMaxDownfall()
    {
        return 1;
    }
}
