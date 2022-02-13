package igteam.immersive_geology.processing.helper;

import igteam.immersive_geology.processing.IGProcessingStage;

public class IGProcessingMethod {

    protected RecipeMethod recipeType;

    protected IGProcessingMethod(RecipeMethod method, IGProcessingStage stage){
        recipeType = method;
        stage.addMethod(this);
    }

    public RecipeMethod getRecipeType(){
        return recipeType;
    }

}
