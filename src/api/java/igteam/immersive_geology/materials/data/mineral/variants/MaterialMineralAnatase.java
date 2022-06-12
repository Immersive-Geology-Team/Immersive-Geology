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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.LinkedHashSet;

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
    public LinkedHashSet<ElementProportion> getElements()
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
                IRecipeBuilder.chemical(this).create(
                        "crushed_ore_" + getName()+"_to_slurry",
                        getStack(ItemPattern.crushed_ore),
                        new FluidTagInput(FluidEnum.HydrochloricAcid.getFluidTag(FluidPattern.fluid), 125),
                        new FluidTagInput(FluidTags.WATER, 125),
                        ItemStack.EMPTY,
                        SlurryEnum.TITANIUM.getType(FluidEnum.HydrochloricAcid).getFluidStack(FluidPattern.slurry, 250),
                        250, 10000);

                IRecipeBuilder.chemical(this).create(
                        "slurry_"+getName() + "_to_dust",
                        MetalEnum.Sodium.getItemTag(ItemPattern.dust),4,
                        new FluidTagInput(SlurryEnum.TITANIUM.getType(FluidEnum.HydrochloricAcid).getFluidTag(FluidPattern.slurry), 250),
                        new FluidTagInput(FluidTags.WATER, 125),
                        MetalEnum.Titanium.getStack(ItemPattern.dust, 1),
                        new FluidStack(FluidEnum.Brine.getFluid(FluidPattern.fluid), 250),
                250, 10000);
            }
        };
    }
}
