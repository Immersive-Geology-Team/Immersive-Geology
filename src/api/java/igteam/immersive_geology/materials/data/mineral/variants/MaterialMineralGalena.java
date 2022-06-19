package igteam.immersive_geology.materials.data.mineral.variants;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.data.mineral.MaterialBaseMineral;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialMineralGalena extends MaterialBaseMineral {

    public MaterialMineralGalena() {
        super("galena");
        initializeColorMap((p) -> 0x857F83);
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.CUBIC;
    }

    @Override
    public boolean hasSlag() {return true;}
    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, "Roasting Stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.roast(this).create("mineral_" + getName() + "_to_slag",
                        getParentMaterial().getStack(ItemPattern.crushed_ore), getParentMaterial().getStack(ItemPattern.slag), 1000, 1);

            }
        };
        new IGProcessingStage(this, "Blasting Stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.blasting(this).create(
                        "slag_" + getName() + "_to_ingot",
                        getParentMaterial().getItemTag(ItemPattern.slag),
                        MetalEnum.Lead.getStack(ItemPattern.ingot, 1));
            }
        };

        new IGProcessingStage(this, "Arc furnace smelting Stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.arcSmelting(this).create("slag_"+getName() +"_to_metal_and_compound_dust",
                        new IngredientWithSize(getParentMaterial().getItemTag(ItemPattern.slag), 1),
                        MetalEnum.Lead.getStack(ItemPattern.ingot),
                        MetalEnum.Silver.getStack(ItemPattern.compound_dust),
                        new IngredientWithSize(Ingredient.fromItems(Items.BONE_MEAL), 1))
                        .setEnergyTime(120000, 100);
                
                IRecipeBuilder.decompose(this).create("compound_dust_"+ MetalEnum.Silver.getName() + "_to_dust",
                        MetalEnum.Silver.getStack(ItemPattern.dust),
                        MetalEnum.Silver.getStack(ItemPattern.compound_dust),
                        120, 120000);
            }
        };

    }
    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.LEAD),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SULFUR)
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
        sources.add(MetalEnum.Lead);
        sources.add(MetalEnum.Silver);

        return sources;
    }

}
