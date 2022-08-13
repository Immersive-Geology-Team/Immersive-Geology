package igteam.api.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import igteam.api.materials.FluidEnum;
import igteam.api.materials.MetalEnum;
import igteam.api.materials.SlurryEnum;
import igteam.api.materials.helper.MaterialSourceWorld;
import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.pattern.FluidPattern;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.helper.CrystalFamily;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IRecipeBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialMineralAnatase extends MaterialBaseMineral {

    public MaterialMineralAnatase() {
        super("anatase");
        initializeColorMap((p) -> 0x475B74);
    }

    @Override
    public Rarity getRarity() {
        return Rarity.COMMON;
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.TETRAGONAL;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(
            Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.TITANIUM),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 2)
            )
        );
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, "Leaching stage") {
            @Override
            protected void describe() {

                IRecipeBuilder.crushing(this).create( "crushed_ore_" +getName() + "_to_dust",
                        getItemTag(ItemPattern.crushed_ore),
                        getStack(ItemPattern.dust), 6000, 200);

                IRecipeBuilder.chemical(this).create(
                        "dust_" + getName()+"_to_slurry",
                        getItemTag(ItemPattern.dust), 1,
                        new FluidTagInput(FluidEnum.HydrochloricAcid.getFluidTag(FluidPattern.fluid), 125),
                        new FluidTagInput(FluidTags.WATER, 125),
                        ItemStack.EMPTY,
                        SlurryEnum.TITANIUM.getType(FluidEnum.HydrochloricAcid).getFluidStack(FluidPattern.slurry, 250),
                        200, 51200);

                IRecipeBuilder.chemical(this).create(
                        "slurry_"+getName() + "_to_dust",
                        MetalEnum.Sodium.getItemTag(ItemPattern.dust),4,
                        new FluidTagInput(SlurryEnum.TITANIUM.getType(FluidEnum.HydrochloricAcid).getFluidTag(FluidPattern.slurry), 250),
                        new FluidTagInput(FluidTags.WATER, 125),
                        MetalEnum.Titanium.getStack(ItemPattern.dust, 1),
                        new FluidStack(FluidEnum.Brine.getFluid(FluidPattern.fluid), 500),
                200, 51200);
            }
        };
    }

    @Override
    public Set<MaterialInterface<?>> getSourceMaterials() {
        Set<MaterialInterface<?>> sources = new LinkedHashSet<>();
        sources.add(MetalEnum.Titanium);
        return sources;
    }

    @Override
    public MaterialSourceWorld getDimension() {
        return MaterialSourceWorld.end;
    }
}
