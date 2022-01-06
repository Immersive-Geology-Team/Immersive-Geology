package igteam.immersive_geology.processing.helper;

import igteam.immersive_geology.processing.IGProcessingStage;

public class IGProcessingMethod {

    protected RecipeMethod recipeType;

    protected IGProcessingMethod(RecipeMethod method){
        recipeType = method;
    }

    public RecipeMethod getRecipeType(){
        return recipeType;
    }

    public void build(IGProcessingStage stage) {
        stage.addMethod(this);
    }
}
