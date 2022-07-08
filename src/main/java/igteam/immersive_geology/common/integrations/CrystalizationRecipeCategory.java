package igteam.immersive_geology.common.integrations;

import igteam.api.main.IGMultiblockProvider;
import igteam.api.processing.recipe.CrystalRecipe;
import igteam.immersive_geology.core.lib.IGLib;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class CrystalizationRecipeCategory extends  IGRecipeCategory<CrystalRecipe> {

    public static final ResourceLocation ID = new ResourceLocation(IGLib.MODID, "crystalizer");

    public CrystalizationRecipeCategory(IGuiHelper guiHelper) {
        super(CrystalRecipe.class, guiHelper, ID, "machine.immersive_geology.machine_steel_crystallizer");
        ResourceLocation background = new ResourceLocation(IGLib.MODID, "textures/gui/jei/crystalizer.png");
        IDrawableStatic back = guiHelper.drawableBuilder(background, 0, 0, 101, 101)
                .setTextureSize(101,101).build();
        setBackground(back);
        setIcon(new ItemStack(IGMultiblockProvider.crystallizer));

    }

    @Override
    public void setIngredients(CrystalRecipe recipe, IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.FLUID,recipe.getInputFluid().getMatchingFluidStacks());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getItemOutputs().get(0));

    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CrystalRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();
        guiFluidStacks.init(0, true, 15, 58);
        guiFluidStacks.set (0, ingredients.getInputs(VanillaTypes.FLUID).get(0));

        guiItemStacks.init(0, false, 65, 48);
        guiItemStacks.set (0, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }

    @Override
    public void draw(CrystalRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        super.draw(recipe, matrixStack, mouseX, mouseY);
    }
}
