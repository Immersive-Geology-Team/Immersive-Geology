package com.igteam.immersivegeology.core.material.data.mineral;

import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraftforge.common.Tags.Biomes;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialFerberite extends MaterialMineral {

    public MaterialFerberite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);

        CONFIG = new MineralConfig(14,60,2,-12,180,1200, false,Optional.of(Biomes.IS_MOUNTAIN));
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0x2688D1));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.MONOCLINIC;
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getSourceMaterials()
    {
        return new LinkedHashSet<>(Set.of(MetalEnum.Iron, MetalEnum.Tungsten));
    }
}
