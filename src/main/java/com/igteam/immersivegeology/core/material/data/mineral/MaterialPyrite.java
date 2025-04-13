package com.igteam.immersivegeology.core.material.data.mineral;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersivegeology.client.helper.IGVeinTextureType;

import com.igteam.immersivegeology.common.block.helper.MineralWeathering;
import com.igteam.immersivegeology.common.world.features.helper.noise.IGGenerationType;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.ChemicalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialSulphideMineral;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialColorHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeChain;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeNode;
import net.minecraft.tags.BiomeTags;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialPyrite extends MaterialSulphideMineral
{

    public MaterialPyrite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.METAMORPHIC);
        this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);

        setAsocialMaterialChance(1f);
        addGenerationFriend((h) -> MineralEnum.Chalcopyrite.instance(), 50);

        addFlags(ItemCategoryFlags.SLAG);
        addFlags(ItemCategoryFlags.PELLET);
        addFlags(ItemCategoryFlags.POWDERED_SLAG);
        CONFIG = new MineralConfig(12,30,3,-64,112,2000, 0.5,false,Optional.of(BiomeTags.IS_NETHER), IGGenerationType.TUBE);
    }

    Function<Integer, Integer> coloredWeathering = MaterialColorHelper.setupWeatheredColors(
            List.of(MaterialColorHelper.weatheredColor(MineralWeathering.PRISTINE, 0xFFD700),
                    MaterialColorHelper.weatheredColor(MineralWeathering.TARNISHED, 0x8B6B3D)));

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction()
    {
        return ((p, i) -> coloredWeathering.apply(i));
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getDerivedMaterials()
    {
        return new LinkedHashSet<>(List.of(MetalEnum.Iron, MetalEnum.Molybdenum, MetalEnum.Osmium, MetalEnum.Platinum));
    }

    @Override
    public boolean canTarnish()
    {
        return true;
    }

    @Override
    public IGVeinTextureType getVeinTextureType()
    {
        return IGVeinTextureType.CRYSTAL;
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();

        IGMethodBuilder.roast(this, IGStageDesignation.PREPARATION).create(
                "crushed_ore_"+getName() + "_to_oxide",
                getItemTag(ItemCategoryFlags.CRUSHED_ORE), 1,
                getStack(ItemCategoryFlags.SLAG,1), 800, 250).addToTree(sulphideElectrowining);

        IGRecipeNode powdered_slag = IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION).create(
                ItemCategoryFlags.SLAG,
                ItemCategoryFlags.POWDERED_SLAG).addToTree(sulphideElectrowining);

        IGMethodBuilder.separating(this, IGStageDesignation.EXTRACTION).create(
           getItemTag(ItemCategoryFlags.POWDERED_SLAG),
           getPrimaryProduct().getStack(ItemCategoryFlags.METAL_OXIDE),
           getSecondaryProduct().getStack(ItemCategoryFlags.METAL_OXIDE),
           0.075f, 200, 250).addToTree(sulphideElectrowining, powdered_slag);

        IGRecipeNode slurry = IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create(
                ItemCategoryFlags.POWDERED_SLAG, BlockCategoryFlags.SLURRY,
                MetalEnum.Osmium.getStack(ItemCategoryFlags.COMPOUND_DUST, 1),
                ChemicalEnum.SulfuricAcid.getSlurryWith(MineralEnum.Pyrite, 3*IGLib.SLURRY_FROM_ACID_AMOUNT),
                IngredientWithSize.of(getStack(ItemCategoryFlags.POWDERED_SLAG, 3)),
                new FluidTagInput(ChemicalEnum.SulfuricAcid.getFluidTag(BlockCategoryFlags.FLUID), 3*IGLib.ACID_TO_SLURRY_AMOUNT),
                null, null, 200, 51200).addToTree(sulphideElectrowining, powdered_slag);

        IGMethodBuilder.crystallize(this, IGStageDesignation.CRYSTALLIZATION).create(
                "mineral_slurry_"+getName() +"_to_" + getSecondaryProduct().getName() + "_crystal",
                MetalEnum.Molybdenum.getStack(ItemCategoryFlags.CRYSTAL, IGLib.COMPOUND_FROM_ACID_AMOUNT),
                ChemicalEnum.SulfuricAcid.getSlurryWith(MetalEnum.Iron, 2*IGLib.ACID_RECOVERED_FROM_SLURRY),
                ChemicalEnum.SulfuricAcid.getSlurryTagWith(MineralEnum.Pyrite), 2*IGLib.SLURRY_TO_CRYSTAL_MB,
                300, 38400).addToTree(sulphideElectrowining, slurry);
    }

    @Override
    public Set<IGRecipeChain> getRecipeChains()
    {
        return Set.of(directBlasting, sulphideElectrowining);
    }


    @Override
    public float getNoiseProbability()
    {
        return 0.089660645f;
    }
}
