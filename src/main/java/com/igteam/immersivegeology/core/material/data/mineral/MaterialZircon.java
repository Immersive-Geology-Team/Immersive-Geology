package com.igteam.immersivegeology.core.material.data.mineral;

import com.igteam.immersivegeology.common.world.features.helper.noise.IGGenerationType;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class MaterialZircon extends MaterialMineral {

    public MaterialZircon() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);
        // TODO Remove fully probably
        CONFIG = new MineralConfig(0,0,0,0,0,0, 0.5,true,Optional.empty(), IGGenerationType.DEFAULT);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0x8B2E1D));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.TETRAGONAL;
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getDerivedMaterials()
    {
        return new LinkedHashSet<>(List.of(MetalEnum.Zirconium));
    }
}
