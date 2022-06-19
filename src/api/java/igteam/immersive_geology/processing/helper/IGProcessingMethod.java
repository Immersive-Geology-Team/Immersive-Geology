package igteam.immersive_geology.processing.helper;

import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.processing.IGProcessingStage;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

public abstract class IGProcessingMethod {

    protected RecipeMethod recipeType;
    protected ResourceLocation location;

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
}
