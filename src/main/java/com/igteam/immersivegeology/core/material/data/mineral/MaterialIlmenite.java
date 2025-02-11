package com.igteam.immersivegeology.core.material.data.mineral;

import com.igteam.immersivegeology.common.world.features.helper.IGGenerationType;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.Tags.Biomes;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialIlmenite extends MaterialMineral {

    public MaterialIlmenite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_EXTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);

        addFlags(ItemCategoryFlags.SLAG);
        addFlags(ItemCategoryFlags.POWDERED_SLAG);

        CONFIG = new MineralConfig(15,40,2,5,140,2000, 0.5,false,Optional.of(Biomes.IS_MOUNTAIN), IGGenerationType.DEFAULT);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0x4A3E3E));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getSourceMaterials()
    {
        return new LinkedHashSet<>(Set.of(MetalEnum.Iron, MetalEnum.Titanium));
    }
    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();

        // Becher process, simplified

        IGMethodBuilder.decompose(this, IGStageDesignation.PURIFICATION).create(
                "crushed_ore_"+getName()+"_to_slag", getStack( ItemCategoryFlags.SLAG, 1),
                getItemTag(ItemCategoryFlags.CRUSHED_ORE), 1, 300, 153600);

        IGMethodBuilder.pulverization(this, IGStageDesignation.PURIFICATION).create(
                ItemCategoryFlags.SLAG,ItemCategoryFlags.POWDERED_SLAG);

        IGMethodBuilder.separating(this, IGStageDesignation.EXTRACTION).create(
                getItemTag(ItemCategoryFlags.POWDERED_SLAG),
                getProductionMaterial().getStack(ItemCategoryFlags.METAL_OXIDE),
                getByproductMaterial().getStack(ItemCategoryFlags.METAL_OXIDE), 0.5f, 300, 1000);

        //Important - NO WATER in reactions!

        //TODO Titanium extraction. Hunter process (Sodium) OR Kroll process (Magnesium)


    }
}