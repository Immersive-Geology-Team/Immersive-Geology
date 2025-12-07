/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.mineral;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersivegeology.common.world.features.helper.noise.IGGenerationType;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialSulphideMineral;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeChain;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeNode;
import net.minecraftforge.common.Tags.Biomes;
import net.minecraftforge.fluids.FluidStack;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class MaterialApatite extends MaterialSulphideMineral {

    protected IGRecipeChain acid_production = new IGRecipeChain(this, "Phosphoric Acid Production", 0);


    public MaterialApatite() {
        super();
        this.acceptableStoneTypes.remove(StoneFormation.NETHER_STONE);
        this.acceptableStoneTypes.add(StoneFormation.METAMORPHIC);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);
        CONFIG = new MineralConfig(24,30,1,-64,212,600,0.85,false,Optional.of(Biomes.IS_COLD), IGGenerationType.TUBE);
    }

    @Override
    public List<String> getAcceptableDimensions()
    {
        return List.of("minecraft:overworld");
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xff87CEEB));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getDerivedMaterials()
    {
        LinkedHashSet<MaterialInterface<?>> materials = new LinkedHashSet<>();
        materials.add(MetalEnum.Calcium);
		return materials;
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();

        IGRecipeNode prep = IGMethodBuilder.crushing(this, IGStageDesignation.EXTRACTION)
                .create("crushed_ore" + getName() + "_to_dust", getStack(ItemCategoryFlags.CRUSHED_ORE, 1),
                        getStack(ItemCategoryFlags.GRIT, 1), 10000, 100).addToTree(acid_production);

        IGRecipeNode grit = IGMethodBuilder.crushing(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.CRUSHED_ORE,
                ItemCategoryFlags.GRIT, 6000, 100).addToTree(acid_production, prep);

        IGRecipeNode powder_b = IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION)
                .create(ItemCategoryFlags.CRUSHED_ORE, ItemCategoryFlags.POWDER).addToTree(acid_production, prep);
        IGRecipeNode powder_a = IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION)
                .create(ItemCategoryFlags.GRIT, ItemCategoryFlags.POWDER, 200, 16000).addToTree(acid_production, grit);

        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING)
                .create("apatite_dust_to_acid", MetalEnum.Calcium.getStack(ItemCategoryFlags.COMPOUND_DUST),
                        ChemicalEnum.PhosphoricAcid.getFluidStack(IGLib.ACID_RECOVERED_FROM_SLURRY),
                        new IngredientWithSize(getItemTag(ItemCategoryFlags.POWDER), IGLib.COMPOUND_FROM_ACID_AMOUNT),
                        new FluidTagInput(ChemicalEnum.SulfuricAcid.getFluidTag(BlockCategoryFlags.FLUID), IGLib.ACID_TO_COMPOUND_AMOUNT),
                        null, null, 200, 51200).joinBranches(acid_production, powder_a, powder_b);
    }

    @Override
    public float getNoiseProbability()
    {
        return 0.30493164f;
    }
}
