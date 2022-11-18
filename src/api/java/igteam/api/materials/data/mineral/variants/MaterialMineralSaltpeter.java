package igteam.api.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.api.materials.FluidEnum;
import igteam.api.materials.MetalEnum;
import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.pattern.FluidFamily;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.helper.CrystalFamily;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.ItemFamily;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGStageDesignation;
import igteam.api.processing.helper.IRecipeBuilder;
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Rarity;
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

        new IGProcessingStage(this, IGStageDesignation.synthesis, "Nitric production stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.chemical(this).create(
                        "dust_" + getName() + "_to_acid_and_salt",
                        getItemTag(ItemFamily.dust),1,
                        new FluidTagInput(FluidEnum.SulfuricAcid.getFluidTag(FluidFamily.fluid), 125),
                        new FluidTagInput(FluidTags.WATER, 125),
                        MetalEnum.Sodium.getStack(ItemFamily.compound_dust), //We never ever gonna use potassium, so sodium
                        new FluidStack(FluidEnum.NitricAcid.getFluid(FluidFamily.fluid), 125),
                        100, 12800);
                IRecipeBuilder.decompose(this).create(
                        "compound_dust_" + MetalEnum.Sodium.getName() + "_to_metal_oxide",
                        MetalEnum.Sodium.getStack(ItemFamily.metal_oxide),
                        MetalEnum.Sodium.getItemTag(ItemFamily.compound_dust), 1,
                        300,153600);
                IRecipeBuilder.arcSmelting(this).create("metal_oxide_"+getName() +"_to_dust",
                                MetalEnum.Sodium.getItemTag(ItemFamily.metal_oxide), 1,
                                MetalEnum.Sodium.getStack(ItemFamily.dust),null,
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

    @Override
    public boolean isSalt() { return true; }

    @Override
    protected boolean hasCrystal() { return true; }

    @Override
    protected boolean hasOreBit() { return false; }

    @Override
    protected boolean hasOreChunk() { return false; }

    @Override
    protected boolean hasDirtyCrushedOre() { return false; }
}
