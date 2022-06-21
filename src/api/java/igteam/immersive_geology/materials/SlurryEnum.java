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
    COPPER(new MaterialSlurryWrapper(MetalEnum.Copper, FluidEnum.HydrochloricAcid)),
    //this is fine...
    CALCIUM(new MaterialSlurryWrapper(MineralEnum.Gypsum, FluidEnum.HydrochloricAcid)),
    SILVER(new MaterialSlurryWrapper(MetalEnum.Silver, FluidEnum.HydrochloricAcid)),

    NEODYMIUM(new MaterialSlurryWrapper(MetalEnum.Neodymium, FluidEnum.HydrochloricAcid),
            new MaterialSlurryWrapper(MetalEnum.Neodymium, FluidEnum.HydrofluoricAcid));
    final LinkedHashSet<MaterialSlurryWrapper> entries;

    SlurryEnum(MaterialSlurryWrapper... slurries){
        entries = new LinkedHashSet<>(Arrays.asList(slurries));
    }

    public LinkedHashSet<MaterialSlurryWrapper> getEntries() {
        return entries;
    }

    public MaterialSlurryWrapper getType(FluidEnum baseFluid) {

        MaterialSlurryWrapper found = entries.toArray(new MaterialSlurryWrapper[entries.size()])[0];
        for (MaterialSlurryWrapper wrapper : entries) {
            if(wrapper.getFluidBase().getName().equals(baseFluid.getName())){
                found = wrapper;
                break;
            }
        }

        return found;
    }
}
