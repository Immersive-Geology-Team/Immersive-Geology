package generators.recipe;


import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.*;
import blusunrize.immersiveengineering.common.items.IEItems;
import igteam.api.IGApi;
import igteam.api.materials.data.MaterialBase;
import igteam.api.materials.helper.APIMaterials;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.builders.*;
import igteam.api.processing.helper.IGProcessingMethod;
import igteam.api.processing.methods.*;
import igteam.api.veins.IGOreVein;
import igteam.api.veins.OreVeinGatherer;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class IGRecipeProvider extends RecipeProvider {

    private Logger logger = IGApi.getNewLogger();

    public IGRecipeProvider(DataGenerator gen) {
        super(gen);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        for (MaterialInterface container : APIMaterials.all()) {
            MaterialBase material = container.instance();

            for (IGProcessingStage stage : material.getStages()) {
                if (stage != null) {
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
                                break;
                            case Synthesis:
                                buildRefineryMethods((IGRefineryMethod) method, consumer);
                                break;
                            case Cutting:
                                buildCuttingMethods((IGHydrojetMethod) method, consumer);
                        }
                    }
                }
            }
        }

        buildExcavatorRecipes(consumer);
    }

    private void buildExcavatorRecipes(Consumer<IFinishedRecipe> consumer){
        OreVeinGatherer gatherer = OreVeinGatherer.INSTANCE;

        for (IGOreVein v: gatherer.RegisteredVeins)
        {
            buildVeinData(v, consumer);
        }
    }

    private void buildVeinData(IGOreVein vein, Consumer<IFinishedRecipe> consumer)
    {
        MineralMixBuilder builder = MineralMixBuilder.builder(vein.getSpawnDimension());
        Set<ITag.INamedTag<Item>> minerals = vein.getVeinContent().keySet();
        for (ITag.INamedTag<Item> t: minerals)
        {
            builder.addOre(t, vein.getVeinContent().get(t));
        }
        builder.setFailchance(vein.getFailChance());
        builder.setWeight(vein.getWeight());
        builder.build(consumer, new ResourceLocation(IGApi.MODID, "mineral/"+vein.getVeinName()));
    }

    private void buildCuttingMethods(IGHydrojetMethod method, Consumer<IFinishedRecipe> consumer) {
        CuttingRecipeBuilder recipe = CuttingRecipeBuilder.builder(method.getResult());
        recipe.addFluidInputs(method.getFluidInput());
        recipe.addInput(method.getInput());
        recipe.setEnergy(80);
        recipe.setTime(60);
        recipe.build(consumer, method.getLocation());
    }

    private void buildRefineryMethods(IGRefineryMethod method, Consumer<IFinishedRecipe> consumer)
    {
        RefineryRecipeBuilder recipe = RefineryRecipeBuilder.builder(method.getFluidResult());
        recipe.addInput(method.getFluidInput1());
        recipe.addInput(method.getFluidInput2());
        recipe.setEnergy(160);
        recipe.build(consumer,method.getLocation());
    }

    private void buildArcSmeltingMethods(IGArcSmeltingMethod method, Consumer<IFinishedRecipe> consumer) {
        ArcFurnaceRecipeBuilder recipe = ArcFurnaceRecipeBuilder.builder(method.getOutput());
        if (!method.getSlag().isEmpty()) {
            recipe.addSlag(method.getSlag());
        }
        recipe.setEnergy(method.getEnergy());
        recipe.setTime(method.getTime());
        recipe.addIngredient("input", method.getInput());
        for (IngredientWithSize t:method.getAdditives())
        {
            recipe.addMultiInput(t);
        }
        recipe.build(consumer, method.getLocation());
    }

    private void buildBasicSmeltingMethods(IGBasicSmeltingMethod method, Consumer<IFinishedRecipe> consumer) {
        IItemProvider input = method.getInput();
        IItemProvider output = method.getOutput();
        float xp = 2;
        int smeltingTime = 200;
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(input), output, xp, smeltingTime / 2).addCriterion("has_" + this.toPath(input), hasItem(input)).build(consumer, method.getLocation());
    }

    private void buildCrushingMethods(IGCrushingMethod method, Consumer<IFinishedRecipe> consumer) {
        logger.warn("Attempting to build Crusher Method: " + method.getName());
        logger.warn("Output: " + method.getOutput().getItem().getRegistryName().getPath());
        if(!method.getOutput().isEmpty()) {
            CrusherRecipeBuilder recipe = CrusherRecipeBuilder.builder(method.getOutput());

            recipe.addInput(method.getInput());
            if (method.hasSecondary()) {
                recipe.addSecondary(method.getSecondary(), method.getSecondaryChange());
            }
            recipe.setTime(method.getTime());
            recipe.setEnergy(method.getEnergy());
            recipe.build(consumer, method.getLocation());
        } else {
            logger.error("Failed to create Crusher Recipe for [" + method.getName() + "]");
        }
    }

    private void buildBlastingMethods(IGBlastingMethod method, Consumer<IFinishedRecipe> consumer) {
        BlastFurnaceRecipeBuilder recipe = BlastFurnaceRecipeBuilder.builder(method.getOutput());
        recipe.addInput(method.getInput());
        recipe.addSlag(method.getSlag().isEmpty() ? new ItemStack(IEItems.Ingredients.slag) : method.getSlag());
        recipe.build(consumer, method.getLocation());
    }

    private void buildCrystallizationMethods(IGCrystallizationMethod method, Consumer<IFinishedRecipe> consumer) {
        logger.info("Data Gen for Crystallization Recipes");
        logger.info("[" + method.getName() + "] - " + method.getItemResult().getItem().getRegistryName());
        CrystalizerRecipeBuilder recipe = CrystalizerRecipeBuilder.builder();
        recipe.addItem("result", method.getItemResult());
        recipe.addFluidInput(method.getFluidInput());
        recipe.setTime(method.getTime());
        recipe.setEnergy(method.getEnergy());
        recipe.build(consumer, method.getLocation());
    }

    private void buildRoastingMethods(IGRoastingMethod method, Consumer<IFinishedRecipe> consumer) {
        logger.info("Data Gen for Roasting Recipes");
        logger.info("[" + method.getMethodName() + "]");
        ReverberationRecipeBuilder recipe = ReverberationRecipeBuilder.builder(method.getItemResult());
        recipe.addItemInput(method.getItemInput());
        recipe.setWasteMult(method.getWasteMult());
        recipe.setTime(method.getTime());
        recipe.build(consumer, method.getLocation());
    }

    private void buildCalcinationMethods(IGCalcinationMethod method, Consumer<IFinishedRecipe> consumer) {
        logger.info("Data Gen for Roasting Recipes");
        logger.info("[" + method.getMethodName() + "]");
        CalcinationRecipeBuilder recipe = CalcinationRecipeBuilder.builder(method.getItemResult());
        recipe.addItemInput(method.getItemInput());
        recipe.setEnergy(method.getEnergy());
        recipe.setTime(method.getTime());
        recipe.build(consumer, method.getLocation());
    }

    private void buildCraftingMethods(IGCraftingMethod method, Consumer<IFinishedRecipe> consumer){
        if(method.isShaped()){
            ShapedRecipeBuilder recipe = ShapedRecipeBuilder.shapedRecipe(method.getResult(), method.getResultAmount());

            String[] patterns = method.getPatterns();
            for (String line : patterns) {
                recipe.patternLine(line);
            }

            HashMap<Character, Item> map = method.getCharacterInputMap();
            for (Character c : map.keySet()) {
                recipe.key(c, map.get(c));
            }

            recipe.setGroup(method.getRecipeGroup()).addCriterion(method.getCriterionName(), hasItem(method.getCriterionTrigger()));
            recipe.build(consumer, method.getLocation());
        } else {
            ShapelessRecipeBuilder recipe = ShapelessRecipeBuilder.shapelessRecipe(method.getResult(), method.getResultAmount());

            for (ITag<Item> tag : method.getInputTags()) {
                recipe.addIngredient(tag);
            }
            recipe.setGroup(method.getRecipeGroup()).addCriterion(method.getCriterionName(), hasItem(method.getCriterionTrigger()));
            recipe.build(consumer, method.getLocation());
        }

    }

    private void buildSeparatingMethods(IGSeparatorMethod method, Consumer<IFinishedRecipe> consumer){
        logger.info("Data Gen for Washing Recipes");
        logger.info("[" + method.getName() + "]");
        SeparatorRecipeBuilder recipe = SeparatorRecipeBuilder.builder(method.getResult()).addInput(method.getInput()).addWaste(method.getWaste());
        recipe.build(consumer, method.getLocation());
    }

    private void buildChemicalMethods(IGChemicalMethod method, Consumer<IFinishedRecipe> consumer){
        logger.info("Data Gen for Chemical Vat Recipes");
        logger.info("[" + method.getMethodName() + "]");
        VatRecipeBuilder recipe = VatRecipeBuilder.builder(method.getFluidResult(), method.getItemResult(), method.getFluidInput1(), method.getFluidInput2(), method.getItemInput(), method.getEnergy(), method.getTime());
        recipe.build(consumer, method.getLocation());
    }

    private void buildBloomeryMethods(IGBloomeryMethod method, Consumer<IFinishedRecipe> consumer){
        logger.info("Data Gen for Bloomery Recipes");
        logger.info("[" + method.getName() + "]");
        BloomeryRecipeBuilder recipe = BloomeryRecipeBuilder.builder(method.getItemResult(), method.getItemInput());
        recipe.setTime(method.getTime());
        recipe.build(consumer, method.getLocation());
    }
    private String toPath(IItemProvider src) {
        return Objects.requireNonNull(src.asItem().getRegistryName()).getPath();
    }
}
