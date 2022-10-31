package igteam.immersive_geology.generators.recipes;

import com.igteam.immersive_geology.core.lib.IGLib;
import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IGProcessingMethod;
import igteam.immersive_geology.processing.methods.IGCraftingMethod;
import igteam.immersive_geology.processing.methods.IGSeparatorMethod;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
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
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        for(MaterialInterface metal : MetalEnum.values()){
            for(IGProcessingStage stage : metal.getStages()){
                logger.log(Level.INFO, "Building for " + stage.getStageName() + " in Material: " + metal.getName());
                for(IGProcessingMethod method : stage.getMethods()){
                    switch (method.getRecipeType()){
                        case Crafting -> buildCraftingMethods((IGCraftingMethod) method, consumer);
                        case Separator -> buildSeparatingMethods((IGSeparatorMethod) method, consumer);
                    }
                }
            }
        }
    }

    private void buildCraftingMethods(IGCraftingMethod method, Consumer<FinishedRecipe> consumer){
        ShapelessRecipeBuilder recipe = ShapelessRecipeBuilder.shapeless(method.getResult(), method.getResultAmount());

        for (TagKey<Item> tag : method.getInputTags()) {
            recipe.requires(tag);
        }

        recipe.group(method.getRecipeGroup()).unlockedBy(method.getCriterionName(), has(method.getCriterionTrigger()));
        recipe.save(consumer, toRL("shapeless/craft_" + Objects.requireNonNull(method.getResult().asItem().getRegistryName()).getPath()));
    }

    private void buildSeparatingMethods(IGSeparatorMethod method, Consumer<FinishedRecipe> consumer){

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
