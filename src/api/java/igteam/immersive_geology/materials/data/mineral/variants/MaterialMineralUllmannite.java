package igteam.immersive_geology.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import igteam.immersive_geology.materials.FluidEnum;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.SlurryEnum;
import igteam.immersive_geology.materials.data.mineral.MaterialBaseMineral;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.pattern.FluidPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IRecipeBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialMineralUllmannite extends MaterialBaseMineral {

    public MaterialMineralUllmannite() {
        super("ullmannite");
        initializeColorMap((p) -> 0x484742);
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.CUBIC;
    }

    @Override
    public boolean hasDust() {
        return false;
    }

    @Override
    public boolean hasCrystal() { return false;}

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this,"Roasting Stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.roast(this).create(
                        "crushed_ore_" + getName() + "_to_oxide",
                        getParentMaterial().getStack(ItemPattern.crushed_ore),
                        MetalEnum.Nickel.getStack(ItemPattern.metal_oxide),
                        200, 2);
            }
        };

        new IGProcessingStage(this,"Chemical Stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.chemical(this).create(
                        "metal_oxide_" + MetalEnum.Nickel.getName() + "_to_slurry",
                        MetalEnum.Nickel.getStack(ItemPattern.metal_oxide),
                        new FluidTagInput(FluidEnum.HydrochloricAcid.getFluidTag(FluidPattern.fluid), 250),
                        new FluidTagInput(FluidTags.WATER, 250),
                        ItemStack.EMPTY,
                        SlurryEnum.NICKEL.getType(FluidEnum.HydrochloricAcid).getFluidStack(FluidPattern.slurry, 250),
                        200, 51200);
                IRecipeBuilder.crystalize(this).create(
                        "slurry" + SlurryEnum.NICKEL.getType(FluidEnum.HydrochloricAcid).getName() + "_to_crystal",
                        MetalEnum.Nickel.getStack(ItemPattern.crystal),
                        new FluidTagInput(SlurryEnum.NICKEL.getType(FluidEnum.HydrochloricAcid).getFluidTag(FluidPattern.slurry), 250),
                        300, 38400);
            }
        };
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.NICKEL),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.ANTIMONY),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SULFUR)
        )
        );
    }


    @Override
    public Rarity getRarity()
    {
        return Rarity.UNCOMMON;
    }

    @Override
    public Set<MaterialInterface<?>> getSourceMaterials() {
        Set<MaterialInterface<?>> sources = new LinkedHashSet<>();
        sources.add(MetalEnum.Nickel);

        return sources;
    }
}
