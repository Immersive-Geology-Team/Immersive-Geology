package igteam.immersive_geology.common.integrations;

import igteam.api.main.IGMultiblockProvider;
import igteam.api.materials.GasEnum;
import igteam.api.processing.recipe.ReverberationRecipe;
import igteam.immersive_geology.core.lib.IGLib;
import com.mojang.blaze3d.matrix.MatrixStack;
import igteam.api.materials.pattern.FluidPattern;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;

public class RoastingRecipeCategory extends IGRecipeCategory<ReverberationRecipe> {

    public static final ResourceLocation ID = new ResourceLocation(IGLib.MODID, "revfurnace");

    public RoastingRecipeCategory(IGuiHelper guiHelper) {
        super(ReverberationRecipe.class, guiHelper, ID, "machine.immersive_geology.machine_steel_reverberation_furnace");
        ResourceLocation background = new ResourceLocation(IGLib.MODID, "textures/gui/jei/reverberation_furnace.png");
        IDrawableStatic back = guiHelper.drawableBuilder(background, 0, 0, 128, 101)
                .setTextureSize(128,101).build();
        setBackground(back);
        setIcon(new ItemStack(IGMultiblockProvider.reverberation_furnace));
    }

    @Override
    public void draw(ReverberationRecipe recipe, MatrixStack transform, double mouseX, double mouseY) {
        super.draw(recipe, transform, mouseX, mouseY);
    }


    @Override
    public void setIngredients(ReverberationRecipe recipe, IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(recipe.input.getMatchingStacks()));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getItemOutputs().get(0));
        //BAD IDEA - ((23/06/2022) no idea what this means now. ~Muddykat)
        ingredients.setOutput(VanillaTypes.FLUID,
                new FluidStack(GasEnum.SulphurDioxide.getFluid(FluidPattern.gas),
                        (int) (recipe.getWasteMultipler() * 125)));
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ReverberationRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

        guiItemStacks.init(0, true, 16, 40);
        guiItemStacks.set (0, ingredients.getInputs(VanillaTypes.ITEM).get(0));

        guiItemStacks.init(1, false, 67, 40);
        guiItemStacks.set (1, ingredients.getOutputs(VanillaTypes.ITEM).get(0));

        guiFluidStacks.init(0, false, 101, 58);
        guiFluidStacks.set (0, ingredients.getOutputs(VanillaTypes.FLUID).get(0));
    }
}
