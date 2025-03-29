package com.igteam.immersivegeology.core.material.data.mineral;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersivegeology.common.world.features.helper.IGGenerationType;
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
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public class MaterialCobaltite extends MaterialSulphideMineral
{

    public MaterialCobaltite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.METAMORPHIC);
        removeMaterialFlags(ItemCategoryFlags.GRIT);
        removeMaterialFlags(ItemCategoryFlags.POWDER);
        addFlags(ItemCategoryFlags.SLAG);
        addFlags(ItemCategoryFlags.POWDERED_SLAG);
        CONFIG = new MineralConfig(18,60,1,-64,72,1200,0.5,false, Optional.of(BiomeTags.IS_NETHER), IGGenerationType.TUBE);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0x939AC4));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.ORTHORHOMBIC;
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getDerivedMaterials()
    {
        return new LinkedHashSet<>(List.of(MetalEnum.Cobalt, MetalEnum.Platinum, MetalEnum.Osmium));
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();

        IGMethodBuilder.roast(this, IGStageDesignation.PREPARATION).create(
                "crushed_ore_"+getName() + "_to_slag",
                getItemTag(ItemCategoryFlags.CRUSHED_ORE), 1,
                getStack(ItemCategoryFlags.SLAG,1), 800, 250).addToTree(sulphideElectrowining);

        IGRecipeNode powdered_slag = IGMethodBuilder.pulverization(this, IGStageDesignation.EXTRACTION).create(
                ItemCategoryFlags.SLAG,
                ItemCategoryFlags.POWDERED_SLAG ).addToTree(sulphideElectrowining);

        IGMethodBuilder.separating(this, IGStageDesignation.EXTRACTION).create(
                getItemTag(ItemCategoryFlags.POWDERED_SLAG),
                getPrimaryProduct().getStack(ItemCategoryFlags.METAL_OXIDE),
                new ItemStack(Items.SAND),
                0.075f, 200, 1000).addToTree(sulphideElectrowining, powdered_slag);

        IGRecipeNode slurry = IGMethodBuilder.chemical(this, IGStageDesignation.LEECHING).create(
                ItemCategoryFlags.POWDERED_SLAG, BlockCategoryFlags.SLURRY,
                MetalEnum.Osmium.getStack(ItemCategoryFlags.COMPOUND_DUST, 1),
                ChemicalEnum.NitricAcid.getSlurryWith(MetalEnum.Cobalt, 3*IGLib.SLURRY_FROM_ACID_AMOUNT),
                IngredientWithSize.of(getStack(ItemCategoryFlags.POWDERED_SLAG, 3)),
                new FluidTagInput(ChemicalEnum.NitricAcid.getFluidTag(BlockCategoryFlags.FLUID), 3*IGLib.ACID_TO_SLURRY_AMOUNT),
                null, null, 200, 51200).addToTree(sulphideElectrowining, powdered_slag);
    }
    @Override
    public Set<IGRecipeChain> getRecipeChains()
    {
        return Set.of(sulphideElectrowining);
    }
}
