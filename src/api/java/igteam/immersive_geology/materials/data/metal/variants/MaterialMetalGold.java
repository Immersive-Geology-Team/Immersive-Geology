package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IRecipeBuilder;
import igteam.immersive_geology.processing.helper.RecipeMethod;

public class MaterialMetalGold extends MaterialBaseMetal {

    public MaterialMetalGold() {
        super("gold");
    }

    @Override
    public int getColor() {
        return 0xFFD700;
    }


    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();


        new IGProcessingStage("Extration Stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.crafting()
                        .shapeless(getItem(ItemPattern.ingot), 16, MetalEnum.Silver.getItemTag(ItemPattern.ingot), MetalEnum.Silver.getItemTag(ItemPattern.nugget))
                        .finializeRecipe("gold_test", "has_silver", MetalEnum.Silver.getItemTag(ItemPattern.ingot)).build(this);
            }
        }.build(this);
    }

    @Override
    public boolean isNative() {
        return true;
    }
}
