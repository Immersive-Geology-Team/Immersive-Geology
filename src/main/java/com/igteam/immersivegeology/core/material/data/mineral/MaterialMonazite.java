package com.igteam.immersivegeology.core.material.data.mineral;

import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

public class MaterialMonazite extends MaterialMineral {

    public MaterialMonazite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_EXTRUSIVE);
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0xC21E56));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.MONOCLINIC;
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getSourceMaterials()
    {
        return new LinkedHashSet<>(Set.of(MetalEnum.Neodymium, MetalEnum.Thorium));
    }
}
