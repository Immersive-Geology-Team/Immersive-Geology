package com.igteam.immersivegeology.core.material.data.mineral;


import com.igteam.immersivegeology.common.block.helper.MineralWeathering;
import com.igteam.immersivegeology.common.world.features.helper.IGGenerationType;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.data.types.MaterialSulphideMineral;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.MaterialColorHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.tags.BiomeTags;
import net.minecraftforge.common.Tags.Biomes;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialChalcopyrite extends MaterialSulphideMineral
{

    public MaterialChalcopyrite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);
        addFlags(ModFlags.TFC, MaterialFlags.EXISTING_IMPLEMENTATION);
        removeMaterialFlags(ItemCategoryFlags.GRIT);
        removeMaterialFlags(ItemCategoryFlags.POWDER);

        addFlags(ItemCategoryFlags.SLAG);
        addFlags(ItemCategoryFlags.POWDERED_SLAG);

        setAsocialMaterialChance(1f);
        addGenerationFriend(() -> MineralEnum.Pyrite.instance(), 50);
        addGenerationFriend(() -> MineralEnum.Chalcocite.instance(), 50);

        //TODO near Lava or nether
        CONFIG = new MineralConfig(14,35,1,-60,256,2000, 0.75,false, Optional.of(BiomeTags.IS_NETHER), IGGenerationType.TUBE);
    }

    Function<Integer, Integer> coloredWeathering = MaterialColorHelper.setupWeatheredColors(
            List.of(MaterialColorHelper.weatheredColor(MineralWeathering.PRISTINE, 0x9E7B3F),
                    MaterialColorHelper.weatheredColor(MineralWeathering.TARNISHED, 0x4C6B3F)));

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction()
    {
        return ((p, i) -> coloredWeathering.apply(i));
    }

    @Override
    public boolean willTarnishOverTime()
    {
        return true;
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.TETRAGONAL;
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getSourceMaterials()
    {
        return new LinkedHashSet<>(Set.of(MetalEnum.Copper, MetalEnum.Iron));
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();

        IGMethodBuilder.roast(this, IGStageDesignation.PREPARATION).create(
               ItemCategoryFlags.CRUSHED_ORE, 1,
               ItemCategoryFlags.SLAG, 1, 800, 250);

        IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION).create(
           ItemCategoryFlags.SLAG,
           ItemCategoryFlags.POWDERED_SLAG);

        IGMethodBuilder.separating(this, IGStageDesignation.PURIFICATION).create(
            getItemTag(ItemCategoryFlags.POWDERED_SLAG),
            MetalEnum.Iron.getStack(ItemCategoryFlags.METAL_OXIDE),
            MetalEnum.Copper.getStack(ItemCategoryFlags.COMPOUND_DUST),
            0.75f, 200, 1000);

        //TODO -- Move it somewhere?
        IGMethodBuilder.roast(this, IGStageDesignation.PURIFICATION).create("roast_copper_sulfide_to_oxide",
                MetalEnum.Copper.getItemTag(ItemCategoryFlags.COMPOUND_DUST), 1,
                MetalEnum.Copper.getStack(ItemCategoryFlags.METAL_OXIDE),  800, 120);

    }
}
