/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.enums;

import blusunrize.immersiveengineering.common.config.IEServerConfig.Ores.VeinType;
import com.igteam.immersivegeology.common.block.IGOreBlock;
import com.igteam.immersivegeology.common.block.IGOreBlock.MineralWeathering;
import com.igteam.immersivegeology.common.block.IGOreBlock.OreRichness;
import com.igteam.immersivegeology.common.world.IWorldGenConfig;
import com.igteam.immersivegeology.core.material.data.mineral.*;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.mojang.serialization.Codec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum MineralEnum implements MaterialInterface<MaterialMineral>, IWorldGenConfig
{
    Acanthite(new MaterialAcanthite()),
    Alumina(new MaterialAlumina()),
    Anatase(new MaterialAnatase()),
    Bauxite(new MaterialBauxite()),
    Cassiterite(new MaterialCassiterite()),
    Chalcocite(new MaterialChalcocite()),
    Chalcopyrite(new MaterialChalcopyrite()),
    Chromite(new MaterialChromite()),
    Cobaltite(new MaterialCobaltite()),
    Cryolite(new MaterialCryolite()),
    Cuprite(new MaterialCuprite()),
    Ferberite(new MaterialFerberite()),
    Fluorite(new MaterialFluorite()),
    Galena(new MaterialGalena()),
    Gypsum(new MaterialGypsum()),
    Hematite(new MaterialHematite()),
    Hubnerite(new MaterialHubnerite()),
    Ilmenite(new MaterialIlmenite()),
    Kaolinite(new MaterialKaolinite()),
    Magnetite(new MaterialMagnetite()),
    Molybenite(new MaterialMolybenite()),
    Monazite(new MaterialMonazite()),
    Pyrite(new MaterialPyrite()),
    Pyrolusite(new MaterialPyrolusite()),
    Scheelite(new MaterialScheelite()),
    Smithsonite(new MaterialSmithsonite()),
    Sphalerite(new MaterialSphalerite()),
    Thorianite(new MaterialThorianite()),
    Thorite(new MaterialThorite()),
    Ullmannite(new MaterialUllmannite()),
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
            if(!e.equals(Kaolinite))
            {
                for(MineralWeathering weathering : MineralWeathering.values())
                {
                    permutations.add(weathering.name().toLowerCase()+"/"+e.getName());
                }
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
    public IGOreBlock getOreBlock(StoneEnum stone, OreRichness richness)
    {
        return material.getOreBlock(stone, richness);
    }

    @Override
    public IGOreBlock getOreBlock(MaterialHelper stone, OreRichness richness)
    {
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
    public boolean useSparsePlacement()
    {
        return material.CONFIG.useSparsePlacement();
    }

    public Optional<TagKey<Biome>> getPreferredBiome()
    {
        return material.CONFIG.preferredBiome();
    }
}
