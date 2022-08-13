package igteam.api.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import igteam.api.materials.FluidEnum;
import igteam.api.materials.MetalEnum;
import igteam.api.materials.SlurryEnum;
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

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialMineralChromite extends MaterialBaseMineral {

    public MaterialMineralChromite() {
        super("chromite");
        initializeColorMap((p) -> 0x615964);
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.CUBIC;
    } // Specifically Hexoctahedral

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, "Iron Extraction") {
            @Override
            protected void describe() {
                IRecipeBuilder.decompose(this).create(
                        "compound_dust_" + MetalEnum.Iron.getName() + "_to_metal_oxide",
                        MetalEnum.Iron.getStack(ItemPattern.metal_oxide),
                        MetalEnum.Iron.getItemTag(ItemPattern.compound_dust), 1,
                        300, 153600);

                IRecipeBuilder.blasting(this).create(
                        "oxide_" + MetalEnum.Iron.getName() + "_to_ingot",
                        MetalEnum.Iron.getItemTag(ItemPattern.metal_oxide),
                        MetalEnum.Iron.getStack(ItemPattern.ingot));
            }
        };

        new IGProcessingStage(this,"Chromium extraction Stage") {
            @Override
            protected void describe() {

                IRecipeBuilder.crushing(this).create( "crushed_ore_" +getName() + "_to_dust",
                        getItemTag(ItemPattern.crushed_ore),
                        getStack(ItemPattern.dust), 6000, 200);

                IRecipeBuilder.chemical(this).create(
                        "dust_"+getName()+"_to_slurry_and_salt",
                        getItemTag(ItemPattern.dust), 1,
                        new FluidTagInput(FluidEnum.NitricAcid.getFluidTag(FluidPattern.fluid), 125),
                        new FluidTagInput(FluidTags.WATER, 125),
                        MetalEnum.Iron.getStack(ItemPattern.compound_dust),
                        SlurryEnum.CHROMIUM.getType(FluidEnum.NitricAcid).getFluidStack(FluidPattern.slurry, 250),
                        200, 51200);

                IRecipeBuilder.chemical(this).create(
                        "slurry_"+getName()+"_to_slurry",
                        null, 1,
                        new FluidTagInput(SlurryEnum.CHROMIUM.getType(FluidEnum.NitricAcid).getFluidTag(FluidPattern.slurry), 250),
                        new FluidTagInput(FluidEnum.HydrochloricAcid.getFluidTag(FluidPattern.fluid), 125),
                        ItemStack.EMPTY,
                        SlurryEnum.CHROMIUM.getType(FluidEnum.HydrochloricAcid).getFluidStack(FluidPattern.slurry, 250),
                        200, 51200);

                IRecipeBuilder.crystalize(this).create(
                        "slurry_" + getName() + "_to_crystal",
                        MetalEnum.Chromium.getStack(ItemPattern.crystal),
                        SlurryEnum.CHROMIUM.getType(FluidEnum.HydrochloricAcid).getFluidTag(FluidPattern.slurry), 250,
                        300, 38400);
            }
        };
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.IRON),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.CHROMIUM, 2),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 4)
        )
        );
    }

    @Override
    public Rarity getRarity()
    {
        return Rarity.COMMON;
    }

    @Override
    public Set<MaterialInterface<?>> getSourceMaterials() {
        Set<MaterialInterface<?>> sources = new LinkedHashSet<>();
        sources.add(MetalEnum.Chromium);
        sources.add(MetalEnum.Iron);

        return sources;
    }
}
