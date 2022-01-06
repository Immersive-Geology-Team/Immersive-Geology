package igteam.immersive_geology.materials;

import igteam.immersive_geology.materials.data.slurry.variants.MaterialSlurryWrapper;

import java.util.*;

public enum SlurryEnum {
    ZINC(new MaterialSlurryWrapper(MetalEnum.Zinc, FluidEnum.HydrochloricAcid),
            new MaterialSlurryWrapper(MetalEnum.Zinc, FluidEnum.SulfuricAcid)),
    TITANIUM(new MaterialSlurryWrapper(MetalEnum.Titanium, FluidEnum.HydrochloricAcid)),
    COBALT ( new MaterialSlurryWrapper(MetalEnum.Cobalt, FluidEnum.HydrochloricAcid)),
    NICKEL(new MaterialSlurryWrapper(MetalEnum.Nickel, FluidEnum.HydrochloricAcid)),
    CHROMIUM(new MaterialSlurryWrapper(MetalEnum.Chromium, FluidEnum.HydrochloricAcid),
            new MaterialSlurryWrapper(MetalEnum.Chromium, FluidEnum.NitricAcid)),
    MANGANESE (new MaterialSlurryWrapper(MetalEnum.Manganese, FluidEnum.HydrochloricAcid),
            new MaterialSlurryWrapper(MetalEnum.Manganese, FluidEnum.SulfuricAcid)),
    VANADIUM(new MaterialSlurryWrapper(MetalEnum.Vanadium, FluidEnum.SulfuricAcid));

    Set<MaterialSlurryWrapper> entries = new LinkedHashSet<>();

    SlurryEnum(MaterialSlurryWrapper... slurries){
        entries.addAll(Arrays.asList(slurries));
    }

    public Set<MaterialSlurryWrapper> getEntries() {
        return entries;
    }
}
