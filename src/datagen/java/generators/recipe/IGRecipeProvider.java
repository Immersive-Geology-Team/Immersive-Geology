package generators.recipe;


import blusunrize.immersiveengineering.api.EnumMetals;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.ArcFurnaceRecipeBuilder;
import blusunrize.immersiveengineering.api.crafting.builders.BlastFurnaceRecipeBuilder;
import blusunrize.immersiveengineering.api.crafting.builders.CrusherRecipeBuilder;
import blusunrize.immersiveengineering.common.items.IEItems;
import com.igteam.immersive_geology.core.lib.IGLib;
import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.materials.data.MaterialBase;
import igteam.immersive_geology.materials.helper.APIMaterials;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.builders.*;
import igteam.immersive_geology.processing.helper.IGProcessingMethod;
import igteam.immersive_geology.processing.methods.*;
import net.minecraft.block.Block;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class IGRecipeProvider extends RecipeProvider {

    private Logger logger = IGApi.getNewLogger();

    public IGRecipeProvider(DataGenerator gen) {
        super(gen);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        for(MaterialInterface container : APIMaterials.all()){
            MaterialBase material = container.get();

            for(IGProcessingStage stage : material.getStages()){
                if(stage != null) {
                    logger.log(Level.DEBUG, "Building for " + stage.getStageName() + " in Material: " + material.getName());
                    for (IGProcessingMethod method : stage.getMethods()) {
                        logger.log(Level.DEBUG, "Building for " + stage.getStageName() + " in Material: " + material.getName());
                        switch (method.getRecipeType()) {
                            case Crafting:
                                buildCraftingMethods((IGCraftingMethod) method, consumer);
                                break;
                            case Separator:
                                buildSeparatingMethods((IGSeparatorMethod) method, consumer);
                                break;
                            case Chemical:
                                buildChemicalMethods((IGChemicalMethod) method, consumer);
                                break;
                            case Bloomery:
                                buildBloomeryMethods((IGBloomeryMethod) method, consumer);
                                break;
                            case Calcination:
                                buildCalcinationMethods((IGCalcinationMethod) method, consumer);
                                break;
                            case Roasting:
                                buildRoastingMethods((IGRoastingMethod) method, consumer);
                                break;
                            case Crystalization:
                                buildCrystallizationMethods((IGCrystallizationMethod) method, consumer);
                                break;
                            case Blasting:
                                buildBlastingMethods((IGBlastingMethod) method, consumer);
                                break;
                            case Crushing:
                                buildCrushingMethods((IGCrushingMethod) method, consumer);
                                break;
                            case basicSmelting:
                                buildBasicSmeltingMethods((IGBasicSmeltingMethod) method, consumer);
                                break;
                            case arcSmelting:
                                buildArcSmeltingMethods((IGArcSmeltingMethod) method, consumer);
                        }
                    }
                }
            }
        }
    }

    private void buildArcSmeltingMethods(IGArcSmeltingMethod method, Consumer<IFinishedRecipe> consumer) {
        ArcFurnaceRecipeBuilder recipe = ArcFurnaceRecipeBuilder.builder(method.getOutput());
        recipe.addSlag(method.getSlag());
        recipe.setEnergy(method.getEnergy());
        recipe.setTime(method.getTime());
        recipe.addInput(method.getInput());
        recipe.build(consumer, toRL("arc_smelting/arc_" + Objects.requireNonNull(method.getName())));
    }

    private void buildBasicSmeltingMethods(IGBasicSmeltingMethod method, Consumer<IFinishedRecipe> consumer) {
        IItemProvider input = method.getInput();
        IItemProvider output = method.getOutput();
        float xp = 2;
        int smeltingTime = 200;
        CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(input), output, xp, smeltingTime / 2).addCriterion("has_" + this.toPath(input), hasItem(input)).build(consumer, this.toRL(this.toPath(output) + "_from_blasting"));
    }

    private void buildCrushingMethods(IGCrushingMethod method, Consumer<IFinishedRecipe> consumer) {
        logger.warn("Attempting to build Crusher Method: " + method.getMethodName());
        logger.warn("Output: " + method.getOutput().getItem().getRegistryName().getPath());
        if(!method.getOutput().isEmpty()) {
            CrusherRecipeBuilder recipe = CrusherRecipeBuilder.builder(method.getOutput());

            recipe.addInput(method.getInput());
            if (method.hasSecondary()) {
                recipe.addSecondary(method.getSecondary(), method.getSecondaryChange());
            }
            recipe.setTime(method.getTime());
            recipe.setEnergy(method.getEnergy());
            recipe.build(consumer, toRL("crushing/crush_" + Objects.requireNonNull(method.getMethodName())));
        } else {
            logger.error("Failed to create Crusher Recipe for [" + method.getMethodName() + "]");
        }
    }

    private void buildBlastingMethods(IGBlastingMethod method, Consumer<IFinishedRecipe> consumer) {
        BlastFurnaceRecipeBuilder recipe = BlastFurnaceRecipeBuilder.builder(method.getOutput());
        recipe.addInput(method.getInput());
        recipe.addSlag(method.getSlag().isEmpty() ? new ItemStack(IEItems.Ingredients.slag) : method.getSlag());
        recipe.build(consumer, toRL("blasting/blast_" + Objects.requireNonNull(method.getMethodName())));
    }

    private void buildCrystallizationMethods(IGCrystallizationMethod method, Consumer<IFinishedRecipe> consumer) {
        logger.info("Data Gen for Crystallization Recipes");
        logger.info("[" + method.getMethodName() + "] - " + method.getItemResult().getItem().getRegistryName());
        CrystalizerRecipeBuilder recipe = CrystalizerRecipeBuilder.builder();
        recipe.addItem("result", method.getItemResult());
        recipe.addFluidInput(method.getFluidInput());
        recipe.setTime(method.getTime());
        recipe.setEnergy(method.getEnergy());
        recipe.build(consumer, toRL("crystallization/crystallize_" + Objects.requireNonNull(method.getMethodName())));
    }

    private void buildRoastingMethods(IGRoastingMethod method, Consumer<IFinishedRecipe> consumer) {
        logger.info("Data Gen for Roasting Recipes");
        logger.info("[" + method.getMethodName() + "]");
        ReverberationRecipeBuilder recipe = ReverberationRecipeBuilder.builder(method.getItemResult());
        recipe.addItemInput(method.getItemInput());
        recipe.setWasteMult(method.getWasteMult());
        recipe.setTime(method.getTime());
        recipe.build(consumer, toRL("roasting/roast_" + Objects.requireNonNull(method.getMethodName())));
    }

    private void buildCalcinationMethods(IGCalcinationMethod method, Consumer<IFinishedRecipe> consumer) {
        logger.info("Data Gen for Roasting Recipes");
        logger.info("[" + method.getMethodName() + "]");
        CalcinationRecipeBuilder recipe = CalcinationRecipeBuilder.builder(method.getItemResult());
        recipe.addItemInput(method.getItemInput());
        recipe.setEnergy(method.getEnergy());
        recipe.setTime(method.getTime());
        recipe.build(consumer, toRL("calcination/decompose_" + Objects.requireNonNull(method.getMethodName())));
    }

    private void buildCraftingMethods(IGCraftingMethod method, Consumer<IFinishedRecipe> consumer){
        //TODO add in Shaped Crafting
        ShapelessRecipeBuilder recipe = ShapelessRecipeBuilder.shapelessRecipe(method.getResult(), method.getResultAmount());

        for (ITag<Item> tag : method.getInputTags()) {
            recipe.addIngredient(tag);
        }

        recipe.setGroup(method.getRecipeGroup()).addCriterion(method.getCriterionName(), hasItem(method.getCriterionTrigger()));
        recipe.build(consumer, toRL("shapeless/craft_" + Objects.requireNonNull(method.getResult().asItem().getRegistryName()).getPath()));
    }

    private void buildSeparatingMethods(IGSeparatorMethod method, Consumer<IFinishedRecipe> consumer){
        logger.info("Data Gen for Washing Recipes");
        logger.info("[" + method.getName() + "]");
        SeparatorRecipeBuilder recipe = SeparatorRecipeBuilder.builder(method.getResult()).addInput(method.getInput()).addWaste(method.getWaste());
        recipe.build(consumer, toRL("wash/wash_" + Objects.requireNonNull(method.getName())));
    }

    private void buildChemicalMethods(IGChemicalMethod method, Consumer<IFinishedRecipe> consumer){
        logger.info("Data Gen for Chemical Vat Recipes");
        logger.info("[" + method.getMethodName() + "]");
        VatRecipeBuilder recipe = VatRecipeBuilder.builder(method.getFluidResult(), method.getItemResult(), method.getFluidInput1(), method.getFluidInput2(), method.getItemInput(), method.getEnergy(), method.getTime());
        recipe.build(consumer, toRL("vat/leach_" + Objects.requireNonNull(method.getMethodName())));
    }

    private void buildBloomeryMethods(IGBloomeryMethod method, Consumer<IFinishedRecipe> consumer){
        logger.info("Data Gen for Bloomery Recipes");
        logger.info("[" + method.getMethodName() + "]");
        BloomeryRecipeBuilder recipe = BloomeryRecipeBuilder.builder(method.getItemResult(), method.getItemInput());
        recipe.setTime(method.getTime());
        recipe.build(consumer, toRL("bloomery/refine_" + Objects.requireNonNull(method.getMethodName())));
    }

    private final HashMap<String, Integer> PATH_COUNT = new HashMap<>();
    private ResourceLocation toRL(String s)
    {
        if(!s.contains("/"))
            s = "crafting/"+s;
        if(PATH_COUNT.containsKey(s))
        {
            int count = PATH_COUNT.get(s)+1;
            PATH_COUNT.put(s, count);
            return new ResourceLocation(IGLib.MODID, s+count);
        }
        PATH_COUNT.put(s, 1);
        return new ResourceLocation(IGLib.MODID, s);
    }

    private String toPath(IItemProvider src) {
        return Objects.requireNonNull(src.asItem().getRegistryName()).getPath();
    }
}
