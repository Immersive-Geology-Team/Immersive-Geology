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
import net.minecraft.item.Rarity;
import net.minecraft.tags.FluidTags;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialMineralCobaltite extends MaterialBaseMineral {

    public MaterialMineralCobaltite() {
        super("cobaltite");
        initializeColorMap((p) -> 0x939AC4);
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.ORTHORHOMBIC;
    }

    @Override
    public boolean hasSlag() {return true;}
    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this,"Extraction Stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.roast(this).create(
                        "crushed_ore_" + getName() + "_to_slag",
                        getItemTag(ItemPattern.crushed_ore), 1,
                        getStack(ItemPattern.slag),
                        200, 2);

                IRecipeBuilder.crushing(this).create(
                        "slag_" +getName()+"_to_dust",
                        getItemTag(ItemPattern.slag),
                        getStack(ItemPattern.dust),
                        3000, 200);

                IRecipeBuilder.blasting(this).create(
                        "slag_" +getName() + "_to_ingot",
                        getItemTag(ItemPattern.slag),
                        MetalEnum.Cobalt.getStack(ItemPattern.ingot));

                IRecipeBuilder.chemical(this).create(
                        "dust_" +getName()+"_to_slurry",
                        getItemTag(ItemPattern.dust), 1,
                        new FluidTagInput(FluidEnum.HydrochloricAcid.getFluidTag(FluidPattern.fluid), 125),
                        new FluidTagInput(FluidTags.WATER, 125),
                        MetalEnum.Platinum.getStack(ItemPattern.compound_dust, 1),
                        SlurryEnum.COBALT.getType(FluidEnum.HydrochloricAcid).getFluidStack(FluidPattern.slurry, 250),
                        200,
                        51200);

                IRecipeBuilder.crystalize(this).create(
                        "slurry" + SlurryEnum.COBALT.getType(FluidEnum.HydrochloricAcid).getName() + "_to_crystal",
                        MetalEnum.Cobalt.getStack(ItemPattern.crystal),
                        SlurryEnum.COBALT.getType(FluidEnum.HydrochloricAcid).getFluidTag(FluidPattern.slurry), 250,
                        300, 38400);

                IRecipeBuilder.separating(this).create(
                        MetalEnum.Platinum.getItemTag(ItemPattern.compound_dust),
                        MetalEnum.Platinum.getStack(ItemPattern.dust),
                        MetalEnum.Osmium.getStack(ItemPattern.dust));

            }
        };
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.COBALT),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.ARSENIC),
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
        sources.add(MetalEnum.Cobalt);
        sources.add(MetalEnum.Platinum);
        sources.add(MetalEnum.Osmium);

        return sources;
    }
}
