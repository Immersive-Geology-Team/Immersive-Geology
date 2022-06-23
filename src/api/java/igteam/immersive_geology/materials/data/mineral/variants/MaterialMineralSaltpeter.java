package igteam.immersive_geology.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.immersive_geology.materials.FluidEnum;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.data.mineral.MaterialBaseMineral;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.pattern.FluidPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IRecipeBuilder;
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialMineralSaltpeter extends MaterialBaseMineral {

    public MaterialMineralSaltpeter() {
        super("saltpeter");
        initializeColorMap((p) -> 0xffffff);
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.ORTHORHOMBIC;
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this,"Nitric production stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.chemical(this).create(
                        "dust_" + getName() + "_to_acid_and_salt",
                        getItemTag(ItemPattern.dust),1,
                        new FluidTagInput(FluidEnum.SulfuricAcid.getFluidTag(FluidPattern.fluid), 125),
                        new FluidTagInput(FluidTags.WATER, 125),
                        MetalEnum.Sodium.getStack(ItemPattern.compound_dust), //We never ever gonna use potassium, so sodium
                        new FluidStack(FluidEnum.NitricAcid.getFluid(FluidPattern.fluid), 125),
                        100, 12800);
                IRecipeBuilder.decompose(this).create(
                        "compound_dust_" + MetalEnum.Sodium.getName() + "_to_metal_oxide",
                        MetalEnum.Sodium.getStack(ItemPattern.metal_oxide),
                        MetalEnum.Sodium.getStack(ItemPattern.compound_dust),
                        300,12000);
                IRecipeBuilder.arcSmelting(this).create("metal_oxide_"+getName() +"_to_dust",
                                new IngredientWithSize(MetalEnum.Sodium.getItemTag(ItemPattern.metal_oxide), 1),
                                MetalEnum.Sodium.getStack(ItemPattern.dust),null,
                                new IngredientWithSize(IETags.coalCokeDust, 1))
                        .setEnergyTime(102400, 200);
            }
        };
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.POTASSIUM),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.NITROGEN),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 3)
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
        sources.add(MetalEnum.Sodium);
        sources.add(FluidEnum.NitricAcid);

        return sources;
    }
}
