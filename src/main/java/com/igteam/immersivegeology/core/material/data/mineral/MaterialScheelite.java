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

public class MaterialScheelite extends MaterialMineral {

    public MaterialScheelite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);

        CONFIG = new MineralConfig(8,60,1,30,160,400, false,Optional.of(Biomes.IS_SPARSE));
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0x32332E));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.TETRAGONAL;
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getSourceMaterials()
    {
        return new LinkedHashSet<>(Set.of(MetalEnum.Tungsten));
    }
}
