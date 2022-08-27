package igteam.api.processing.helper;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.api.IGApi;
import igteam.api.processing.IGProcessingStage;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

public abstract class IGProcessingMethod {

    protected RecipeMethod recipeType;
    protected ResourceLocation location;
    protected String name = "N/A";

    protected IGProcessingMethod(RecipeMethod method, IGProcessingStage stage){
        recipeType = method;
        stage.addMethod(this);
    }

    public RecipeMethod getRecipeType(){
        return recipeType;
    }

    public abstract ResourceLocation getLocation();

    private static final HashMap<String, Integer> PATH_COUNT = new HashMap<>();
    protected ResourceLocation toRL(String s)
    {
        if(!s.contains("/"))
            s = "crafting/"+s;
        if(PATH_COUNT.containsKey(s))
        {
            int count = PATH_COUNT.get(s)+1;
            PATH_COUNT.put(s, count);
            return new ResourceLocation(IGApi.MODID, s+count);
        }
        PATH_COUNT.put(s, 1);
        return new ResourceLocation(IGApi.MODID, s);
    }

    public void clearRecipePath(){
        PATH_COUNT.clear();
    }

    public ITag<?> getGenericInput(){
        return null;
    }

    public abstract ItemStack getGenericOutput();

    public abstract String getName();
}
