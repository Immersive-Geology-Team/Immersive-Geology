package igteam.api.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import igteam.api.materials.FluidEnum;
import igteam.api.materials.MineralEnum;
import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.pattern.FluidPattern;
import igteam.api.processing.helper.IGStageDesignation;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.helper.CrystalFamily;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IRecipeBuilder;
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialMineralFluorite extends MaterialBaseMineral {

    public MaterialMineralFluorite() {
        super("fluorite");
        initializeColorMap((p) -> 0x329870);
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.CUBIC;
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, IGStageDesignation.synthesis) {
            @Override
            protected void describe() {
                IRecipeBuilder.crushing(this).create(
                        "crushed_ore_" + getName() + "_to_dust",
                        getItemTag(ItemPattern.crushed_ore),
                        getStack(ItemPattern.dust),
                        6000, 200);

                IRecipeBuilder.chemical(this).create(
                        "dust_" + getName() + "_to_acid",
                        getItemTag(ItemPattern.dust), 1,
                        new FluidTagInput(FluidEnum.SulfuricAcid.getFluidTag(FluidPattern.fluid), 125),
                        new FluidTagInput(FluidTags.WATER, 250),
                        MineralEnum.Gypsum.getStack(ItemPattern.dust),
                        new FluidStack(FluidEnum.HydrofluoricAcid.getFluid(FluidPattern.fluid), 125),
                        100, 12800);
            }
        };
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.CALCIUM),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.FLUORINE, 2)
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
        return Collections.singleton(FluidEnum.HydrofluoricAcid);
    }

}
