package com.igteam.immersivegeology.core.material.data.mineral;

import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;

import java.util.Set;
import java.util.function.Function;

public class MaterialBauxite extends MaterialMineral {

    public MaterialBauxite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);
    }

    @Override
    protected Function<IFlagType<?>, Integer> materialColorFunction() {
        return ((p) -> (0x999FAF));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.MONOCLINIC;
    }

    @Override
    public Set<MaterialInterface<?>> getSourceMaterials()
    {
        return Set.of(MetalEnum.Aluminum);
    }
}
