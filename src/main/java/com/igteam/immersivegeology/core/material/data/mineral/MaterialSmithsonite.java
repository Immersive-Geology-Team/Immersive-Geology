package com.igteam.immersivegeology.core.material.data.mineral;

import blusunrize.immersiveengineering.common.register.IEItems.Ingredients;
import com.igteam.immersivegeology.common.world.features.helper.noise.IGGenerationType;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeChain;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeNode;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags.Biomes;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public class MaterialSmithsonite extends MaterialMineral {

    public MaterialSmithsonite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);
        addFlags(ItemCategoryFlags.SLAG);
        addFlags(ItemCategoryFlags.PELLET);
        addFlags(ItemCategoryFlags.POWDERED_SLAG);

		CONFIG = new MineralConfig(33,50,2,30,70,550,0.5,false,Optional.of(Biomes.IS_WET), IGGenerationType.DEFAULT);
	}

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xff81D1DC));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getDerivedMaterials()
    {
        return new LinkedHashSet<>(List.of(MetalEnum.Zinc, MetalEnum.Copper));
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();
        IGRecipeNode slag =  IGMethodBuilder.decompose(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.SLAG,
                ItemCategoryFlags.CRUSHED_ORE, 1,300, 153600).addOptionalToTree(directBlasting);

        IGRecipeNode p_slag = IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION).create(ItemCategoryFlags.SLAG,
                ItemCategoryFlags.POWDERED_SLAG).addToTree(directBlasting, slag);

        //It has blue tint, so copper is present
       IGMethodBuilder.separating(this, IGStageDesignation.PREPARATION).create(getItemTag(ItemCategoryFlags.POWDERED_SLAG),
                MetalEnum.Zinc.getStack(ItemCategoryFlags.METAL_OXIDE),
                MetalEnum.Copper.getStack(ItemCategoryFlags.METAL_OXIDE),
                0.075f, 200, 1000).addToTree(directBlasting, p_slag);

        //No byproducts and zinc is evaporated
        IGMethodBuilder.blasting(this, IGStageDesignation.EXTRACTION).create(this, ItemCategoryFlags.SLAG,
                getPrimaryProduct().instance(),ItemCategoryFlags.INGOT, new ItemStack(Ingredients.SLAG), 900).addToTree(directBlasting, slag);

        //No byproducts and most of the zinc is evaporated
        IGMethodBuilder.blasting(this, IGStageDesignation.EXTRACTION).create("crushed_ore_"+getName()+"_to_ingot",
                getItemTag(ItemCategoryFlags.CRUSHED_ORE),
                getPrimaryProduct().getStack(ItemCategoryFlags.NUGGET, 5)).addOptionalToTree(directBlasting);

    }
    @Override
    public Set<IGRecipeChain> getRecipeChains()
    {
        return Set.of(directBlasting);
    }

    @Override
    public float getNoiseProbability()
    {
        return 0.14581299f;
    }
}
