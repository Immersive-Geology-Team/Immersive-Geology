package com.igteam.immersivegeology.core.material.data.mineral;

import com.igteam.immersivegeology.client.helper.IGVeinTextureType;

import com.igteam.immersivegeology.common.block.helper.MineralWeathering;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialColorHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.dries007.tfc.util.Metal;
import net.minecraftforge.common.Tags.Biomes;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialPyrite extends MaterialMineral {

    public MaterialPyrite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.METAMORPHIC);
        this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);

        // TODO Banished to the Nether and Lava! ~UnSchtalch
        CONFIG = new MineralConfig(12,30,3,-64,112,900, 0.5,false,Optional.of(Biomes.IS_HOT));
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
    public LinkedHashSet<MaterialInterface<?>> getSourceMaterials()
    {
        return new LinkedHashSet<>(Set.of(MetalEnum.Iron));
    }

    @Override
    public boolean willTarnishOverTime()
    {
        return true;
    }

    @Override
    public IGVeinTextureType getVeinType()
    {
        return IGVeinTextureType.CRYSTAL;
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();

        IGMethodBuilder.roast(this, IGStageDesignation.PREPARATION).create("crushed_ore_"+getName() + "_to_oxide",
                getItemTag(ItemCategoryFlags.CRUSHED_ORE), 1, MetalEnum.Iron.getStack(ItemCategoryFlags.METAL_OXIDE), 800, 250);

        //TODO Iron Oxide Blasting


    }
}
