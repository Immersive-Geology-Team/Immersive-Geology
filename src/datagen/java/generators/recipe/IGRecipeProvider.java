package generators.recipe;


import com.igteam.immersive_geology.core.lib.IGLib;
import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.data.MaterialBase;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.builders.SeparatorRecipeBuilder;
import igteam.immersive_geology.processing.helper.IGProcessingMethod;
import igteam.immersive_geology.processing.methods.IGCraftingMethod;
import igteam.immersive_geology.processing.methods.IGSeparatorMethod;
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
        for(MaterialInterface container : MetalEnum.values()){
            MaterialBase metal = container.get();
            for(IGProcessingStage stage : metal.getStages()){
                logger.log(Level.INFO, "Building for " + stage.getStageName() + " in Material: " + metal.getName());
                for(IGProcessingMethod method : stage.getMethods()){
                    switch (method.getRecipeType()){
                        case Crafting: buildCraftingMethods((IGCraftingMethod) method, consumer); break;
                        case Separator: buildSeparatingMethods((IGSeparatorMethod) method, consumer); break;
                    }
                }
            }
        }
    }

    private void buildCraftingMethods(IGCraftingMethod method, Consumer<IFinishedRecipe> consumer){
        ShapelessRecipeBuilder recipe = ShapelessRecipeBuilder.shapelessRecipe(method.getResult(), method.getResultAmount());

        for (ITag<Item> tag : method.getInputTags()) {
            recipe.addIngredient(tag);
        }

        recipe.setGroup(method.getRecipeGroup()).addCriterion(method.getCriterionName(), hasItem(method.getCriterionTrigger()));
        recipe.build(consumer, toRL("shapeless/craft_" + Objects.requireNonNull(method.getResult().asItem().getRegistryName()).getPath()));
    }

    private void buildSeparatingMethods(IGSeparatorMethod method, Consumer<IFinishedRecipe> consumer){
        SeparatorRecipeBuilder recipe = method.getBuilder();
        recipe.build(consumer, toRL("shapeless/craft_" + Objects.requireNonNull(method.getName())));
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
