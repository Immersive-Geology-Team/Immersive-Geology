package igteam.api.materials;

import igteam.api.materials.data.MaterialBase;
import igteam.api.materials.data.mineral.variants.*;
import igteam.api.materials.pattern.FluidPattern;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.helper.CrystalFamily;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.BlockPattern;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.materials.pattern.MaterialPattern;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraftforge.fluids.FluidStack;
import igteam.api.processing.IGProcessingStage;

import java.util.Set;

public enum MineralEnum implements MaterialInterface<MaterialBaseMineral> {
    Alumina(new MaterialMineralAlumina()),
    Anatase(new MaterialMineralAnatase()),
    Bauxite(new MaterialMineralBauxite()),
    Acanthite(new MaterialMineralAcanthite()),
    Chalcocite(new MaterialMineralChalcocite()),
    Cuprite(new MaterialMineralCuprite()),
 //   Zircon(new MaterialMineralZircon()),
    Monazite(new MaterialMineralMonazite()),
    Ilmenite(new MaterialMineralIlmenite()),
    Cobaltite(new MaterialMineralCobaltite()),
    Cassiterite(new MaterialMineralCassiterite()),
    Chalcopyrite(new MaterialMineralChalcopyrite()),
    Chromite(new MaterialMineralChromite()),
    Cryolite(new MaterialMineralCryolite()),
    Ferberite(new MaterialMineralFerberite()),
    Fluorite(new MaterialMineralFluorite()),  //TODO Immersive Engineering has trouble with this as it's looking for a GEM version under the tag forge:gem/fluorite
    Gypsum(new MaterialMineralGypsum()),
    Hematite(new MaterialMineralHematite()),
    Hubnerite(new MaterialMineralHubnerite()),
    Pyrolusite(new MaterialMineralPyrolusite()),
    RockSalt(new MaterialMineralRockSalt()),
    SaltPeter(new MaterialMineralSaltpeter()),
    Scheelite(new MaterialMineralScheelite()),
  //  Thorianite(new MaterialMineralThorianite()), // Not used IRL. Like at F all
//    Thorite(new MaterialMineralThorite()),
    Uraninite(new MaterialMineralUraninite()),
    Sphalerite(new MaterialMineralSphalerite()),
    Smithsonite(new MaterialMineralSmithsonite()),
    Ullmannite(new MaterialMineralUllmannite()),
    Galena(new MaterialMineralGalena()),
    Pyrite(new MaterialMineralPyrite()),
    Vanadinite(new MaterialMineralVanadinite()),
    Kaolinite(new MaterialMineralKaolinite()),
    Unobtania(new MaterialMineralUnobtania());

    private final MaterialBaseMineral material;

    MineralEnum(MaterialBaseMineral m){
        this.material = m;
    }

    @Override
    public MaterialBaseMineral instance() {
        return material;
    }

    @Override
    public boolean isFluidPortable(ItemPattern pattern){
        return false;
    }
}
