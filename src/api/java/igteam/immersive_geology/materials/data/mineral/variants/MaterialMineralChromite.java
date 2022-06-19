package igteam.immersive_geology.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import igteam.immersive_geology.materials.FluidEnum;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.SlurryEnum;
import igteam.immersive_geology.materials.data.mineral.MaterialBaseMineral;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.helper.PeriodicTableElement.ElementProportion;
import igteam.immersive_geology.materials.pattern.FluidPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.LinkedHashSet;

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
                        MetalEnum.Iron.getStack(ItemPattern.compound_dust),
                        300, 10000);

                IRecipeBuilder.blasting(this).create(
                        "oxide_" + MetalEnum.Iron.getName() + "_to_ingot",
                        MetalEnum.Iron.getItemTag(ItemPattern.metal_oxide),
                        MetalEnum.Iron.getStack(ItemPattern.ingot));
            }
        };

        new IGProcessingStage(this,"Chromium extraction Stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.chemical(this).create(
                        "crushed_ore_"+getName()+"_to_slurry_and_salt",
                        getStack(ItemPattern.crushed_ore),
                        new FluidTagInput(FluidEnum.NitricAcid.getFluidTag(FluidPattern.fluid), 125),
                        new FluidTagInput(FluidTags.WATER, 125),
                        MetalEnum.Iron.getStack(ItemPattern.compound_dust),
                        SlurryEnum.CHROMIUM.getType(FluidEnum.NitricAcid).getFluidStack(FluidPattern.slurry, 250),
                        240, 12000);

                IRecipeBuilder.chemical(this).create(
                        "slurry_"+getName()+"_to_slurry",
                        ItemStack.EMPTY,
                        new FluidTagInput(SlurryEnum.CHROMIUM.getType(FluidEnum.NitricAcid).getFluidTag(FluidPattern.slurry), 250),
                        new FluidTagInput(FluidEnum.HydrochloricAcid.getFluidTag(FluidPattern.fluid), 125),
                        ItemStack.EMPTY,
                        SlurryEnum.CHROMIUM.getType(FluidEnum.HydrochloricAcid).getFluidStack(FluidPattern.slurry, 250),
                        240, 12000);

                IRecipeBuilder.crystalize(this).create(
                        "slurry_" + getName() + "_to_crystal",
                        MetalEnum.Chromium.getStack(ItemPattern.crystal),
                        new FluidTagInput(SlurryEnum.CHROMIUM.getType(FluidEnum.HydrochloricAcid).getFluidTag(FluidPattern.slurry), 250),
                        240, 12000);
            }
        };
    }

    @Override
    public LinkedHashSet<ElementProportion> getElements()
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
    public MetalEnum getSourceMetals() {
        return MetalEnum.Chromium;
    }
}
