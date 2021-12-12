package com.igteam.immersive_geology.api.materials.fluid;

import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.material_data.fluids.slurry.MaterialSlurryWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum SlurryEnum {
    ZINC(new MaterialSlurryWrapper(MaterialEnum.Zinc, FluidEnum.HydrochloricAcid, 0.5f),
            new MaterialSlurryWrapper(MaterialEnum.Zinc, FluidEnum.SulfuricAcid, 0.5f)),
    TITANIUM(new MaterialSlurryWrapper(MaterialEnum.Titanium, FluidEnum.HydrochloricAcid, 0.5f)),
    COBALT ( new MaterialSlurryWrapper(MaterialEnum.Cobalt, FluidEnum.HydrochloricAcid, 0.5f)),
    NICKEL(new MaterialSlurryWrapper(MaterialEnum.Nickel, FluidEnum.HydrochloricAcid, 0.5f)),
    CHROMIUM(new MaterialSlurryWrapper(MaterialEnum.Chromium, FluidEnum.HydrochloricAcid, 0.5f),
            new MaterialSlurryWrapper(MaterialEnum.Chromium, FluidEnum.NitricAcid, 0.5f)),
    MANGANESE (new MaterialSlurryWrapper(MaterialEnum.Manganese, FluidEnum.HydrochloricAcid, 0.5f),
            new MaterialSlurryWrapper(MaterialEnum.Manganese, FluidEnum.SulfuricAcid,0.5f));
    List<MaterialSlurryWrapper> entries = new ArrayList<>();
    SlurryEnum(MaterialSlurryWrapper... slurries){
        entries.addAll(Arrays.asList(slurries));
    }

    public List<MaterialSlurryWrapper> getEntries() {
        return entries;
    }
}
