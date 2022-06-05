package generators.recipe;


import com.igteam.immersive_geology.core.lib.IGLib;
import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.materials.data.MaterialBase;
import igteam.immersive_geology.materials.helper.APIMaterials;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.builders.*;
import igteam.immersive_geology.processing.helper.IGProcessingMethod;
import igteam.immersive_geology.processing.methods.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;

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
                    logger.log(Level.INFO, "Building for " + stage.getStageName() + " in Material: " + material.getName());
                    for (IGProcessingMethod method : stage.getMethods()) {
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
                        }
                    }
                }
            }
        }
    }

    private void buildCrystallizationMethods(IGCrystallizationMethod method, Consumer<IFinishedRecipe> consumer) {
        logger.info("Data Gen for Crystallization Recipes");
        logger.info("[" + method.getMethodName() + "]");
        CrystalizerRecipeBuilder recipe = CrystalizerRecipeBuilder.builder(method.getItemResult());
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
}
