package com.igteam.immersivegeology.core.material.data.mineral;

import blusunrize.immersiveengineering.api.crafting.builders.CrusherRecipeBuilder;
import blusunrize.immersiveengineering.common.register.IEItems;
import blusunrize.immersiveengineering.common.register.IEItems.Ingredients;
import com.igteam.immersivegeology.common.world.features.helper.noise.IGGenerationType;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialSulphideMineral;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeNode;
import com.igteam.immersivegeology.core.material.helper.material.recipe.methods.IECrushingMethod;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class MaterialSphalerite extends MaterialSulphideMineral
{

    public MaterialSphalerite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);
        addFlags(ModFlags.TFC, MaterialFlags.EXISTING_IMPLEMENTATION);
        addExistingFlag(ModFlags.TFC, BlockCategoryFlags.ORE_BLOCK);
        addFlags(ItemCategoryFlags.SLAG);
		addFlags(ItemCategoryFlags.PELLET);
		addFlags(ItemCategoryFlags.POWDERED_SLAG);

		CONFIG = new MineralConfig(21,45,3,0,140,800, 0.5,false,Optional.of(BiomeTags.IS_NETHER), IGGenerationType.TUBE);
	}

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xff6F8070));
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getDerivedMaterials()
    {
        return new LinkedHashSet<>(List.of(MetalEnum.Zinc, MetalEnum.Iron));
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();

        float chance = 0.33f;
        int time = 100;
        int energy = 6000;
        for(ItemCategoryFlags ore : List.of(ItemCategoryFlags.POOR_ORE, ItemCategoryFlags.NORMAL_ORE, ItemCategoryFlags.RICH_ORE))
        {
            IECrushingMethod method = IGMethodBuilder.crushing(this, IGStageDesignation.EXTRACTION).create(ore, ItemCategoryFlags.DIRTY_CRUSHED_ORE, energy, time);
            method.addSecondary(getStack(ItemCategoryFlags.DIRTY_CRUSHED_ORE, 1), chance);
            method.addSecondary(new ItemStack(Ingredients.DUST_SULFUR), 0.5f);
            if(ore.equals(ItemCategoryFlags.NORMAL_ORE) || ore.equals(ItemCategoryFlags.RICH_ORE)) method.addSecondary(getStack(ItemCategoryFlags.DIRTY_CRUSHED_ORE, 1), chance / 2);
            if(ore.equals(ItemCategoryFlags.RICH_ORE)) method.addSecondary(getStack(ItemCategoryFlags.DIRTY_CRUSHED_ORE, 1), chance / 2);
        }

        IGMethodBuilder.bloomery(this, IGStageDesignation.REFINEMENT).create(ItemCategoryFlags.CRUSHED_ORE,
                2, ItemCategoryFlags.INGOT, 1, 400);

        IGMethodBuilder.roast(this, IGStageDesignation.PREPARATION).create(
                ItemCategoryFlags.CRUSHED_ORE, 1,
                ItemCategoryFlags.SLAG, 1, 800, IGLib.SULFUR_OUTGAS);

        IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION).create(
                ItemCategoryFlags.SLAG,
                ItemCategoryFlags.POWDERED_SLAG);

        IGMethodBuilder.separating(this, IGStageDesignation.PURIFICATION).create(
                getItemTag(ItemCategoryFlags.POWDERED_SLAG),
                MetalEnum.Zinc.getStack(ItemCategoryFlags.METAL_OXIDE),
                MetalEnum.Iron.getStack(ItemCategoryFlags.METAL_OXIDE),
                0.75f, 200, 250);
    }

    @Override
    public float getNoiseProbability()
    {
        return 0.203125f;
    }

    @Override
    public List<String> getAcceptableDimensions()
    {
        return List.of("minecraft:overworld", "minecraft:the_nether");
    }
}
