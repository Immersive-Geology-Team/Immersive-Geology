package com.igteam.immersivegeology.core.material.data.mineral;

import com.igteam.immersivegeology.common.world.features.helper.IGGenerationType;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraft.tags.BiomeTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.Tags.Biomes;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialCryolite extends MaterialMineral {

    public MaterialCryolite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);
        addFlags(ModFlags.TFC, MaterialFlags.EXISTING_IMPLEMENTATION);

        addExistingFlag(ModFlags.TFC, BlockCategoryFlags.ORE_BLOCK);
        // TODO Banished to the END of time
        CONFIG = new MineralConfig(14,90,1,0,120,500, 0.5,false,Optional.of(BiomeTags.IS_END), IGGenerationType.DEFAULT);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xC5C5C5));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.MONOCLINIC;
    }
}
