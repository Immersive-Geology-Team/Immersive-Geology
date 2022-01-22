package igteam.immersive_geology.materials.data.metal.variants;

import blusunrize.immersiveengineering.api.EnumMetals;
import blusunrize.immersiveengineering.api.IEApi;
import blusunrize.immersiveengineering.api.IEEnums;
import blusunrize.immersiveengineering.api.IETags;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IRecipeBuilder;
import igteam.immersive_geology.processing.helper.RecipeMethod;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Items;

public class MaterialMetalGold extends MaterialBaseMetal {

    public MaterialMetalGold() {
        super("gold");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0xFFD700;
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this,"Extration Stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.crafting()
                        .shapeless(Items.GOLD_INGOT, 16,
                                IETags.getTagsFor(EnumMetals.SILVER).ingot,
                                IETags.getTagsFor(EnumMetals.SILVER).nugget)
                        .finializeRecipe("gold_test", "has_silver", MetalEnum.Silver.getItemTag(ItemPattern.ingot)).build(this);
            }
        };
    }

    @Override
    public boolean isNative() {
        return true;
    }

    @Override
    public boolean hasExistingImplementation(){
        return true;
    }
}
