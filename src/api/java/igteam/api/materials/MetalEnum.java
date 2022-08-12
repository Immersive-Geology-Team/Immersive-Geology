package igteam.api.materials;

import igteam.api.materials.data.MaterialBase;
import igteam.api.materials.data.metal.MaterialBaseMetal;
import igteam.api.materials.data.metal.variants.*;
import igteam.api.materials.pattern.FluidPattern;
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

public enum MetalEnum implements MaterialInterface<MaterialBaseMetal> {
    Aluminum(new MaterialMetalAluminium()),
    Bronze(new MaterialMetalBronze()),
    Chromium(new MaterialMetalChromium()),
    Copper(new MaterialMetalCopper()),
    Gold(new MaterialMetalGold()),
    Iron(new MaterialMetalIron()),
    Lead(new MaterialMetalLead()),
    Manganese(new MaterialMetalManganese()),
    Nickel(new MaterialMetalNickel()),
    Platinum(new MaterialMetalPlatinum()),
    Silver(new MaterialMetalSilver()),
    Cobalt(new MaterialMetalCobalt()),
    Uranium(new MaterialMetalUranium()),
    Tin(new MaterialMetalTin()),
    Titanium(new MaterialMetalTitanium()),
    Tungsten(new MaterialMetalTungsten()),
    Thorium(new MaterialMetalThorium()),
    Vanadium(new MaterialMetalVanadium()),
 //   Zirconium(new MaterialMetalZirconium()),
    Zinc(new MaterialMetalZinc()),
    Sodium(new MaterialMetalSodium()),
    Osmium(new MaterialMetalOsmium()),
    Neodymium(new MaterialMetalNeodymium()),
    Unobtanium(new MaterialMetalUnobtanium());

    private final MaterialBaseMetal material;

    MetalEnum(MaterialBaseMetal m){
        this.material = m;
    }

    @Override
    public boolean isFluidPortable(ItemPattern pattern){
        return false;
    }

    @Override
    public MaterialBaseMetal instance() {
        return material;
    }
}