package com.igteam.immersivegeology.core.material.data.mineral;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersivegeology.common.world.features.helper.IGGenerationType;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
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
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags.Biomes;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public class MaterialMolybdenite extends MaterialSulphideMineral
{

    public MaterialMolybdenite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);

        this.acceptableStoneTypes.add(StoneFormation.METAMORPHIC);

        removeMaterialFlags(ItemCategoryFlags.GRIT);
        removeMaterialFlags(ItemCategoryFlags.POWDER);

        addFlags(ItemCategoryFlags.SLAG);
        addFlags(ItemCategoryFlags.POWDERED_SLAG);

        // TODO Banished to the Nether! ~UnSchtalch
        CONFIG = new MineralConfig(12,70,1,-64,212,2400, 0.75,false,Optional.of(BiomeTags.IS_NETHER), IGGenerationType.TUBE);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xb3cbe4));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getDerivedMaterials()
    {
        return new LinkedHashSet<>(Set.of(MetalEnum.Molybdenum));
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();
        IGMethodBuilder.roast(this, IGStageDesignation.ROASTING).create(
                ItemCategoryFlags.CRUSHED_ORE, 1,   // Input
                ItemCategoryFlags.SLAG, 1,         // Output
                1000,                                          // Roasting Time
                200                                            // Sulfur Dioxide Output Amount
        ).addToTree(sulphideElectrowining);

        IGMethodBuilder.pulverization(this, IGStageDesignation.EXTRACTION).create(
                ItemCategoryFlags.SLAG,
                ItemCategoryFlags.POWDERED_SLAG ).addToTree(sulphideElectrowining);

        //Direct Leaching in Ammonia
        IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create(getName()+"ore_to_slurry",
                MetalEnum.Osmium.getStack(ItemCategoryFlags.COMPOUND_DUST, 1),
                ChemicalEnum.Ammonia.getCloudySlurryWith(MineralEnum.Molybdenite, 3*IGLib.SLURRY_FROM_ACID_AMOUNT),
                IngredientWithSize.of(getStack(ItemCategoryFlags.POWDERED_SLAG, 1)),
                new FluidTagInput(ChemicalEnum.Ammonia.getFluidTag(BlockCategoryFlags.FLUID), 3*IGLib.ACID_TO_SLURRY_AMOUNT),
                null, null,200, 51200).addToTree(sulphideElectrowining);
    }

    @Override
    public Set<IGRecipeChain> getRecipeChains()
    {
        return Set.of(sulphideElectrowining);
    }
}
