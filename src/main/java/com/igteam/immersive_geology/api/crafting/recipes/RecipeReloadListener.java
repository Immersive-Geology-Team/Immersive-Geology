package com.igteam.immersive_geology.api.crafting.recipes;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.crafting.recipes.recipe.SeparatorRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class RecipeReloadListener  implements IResourceManagerReloadListener {

    private final DataPackRegistries dataPackRegistries;

    public RecipeReloadListener(DataPackRegistries dataPackRegistries){
        this.dataPackRegistries = dataPackRegistries;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager){
        if(dataPackRegistries != null){
            lists(dataPackRegistries.getRecipeManager());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void recipesUpdated(RecipesUpdatedEvent event){
        if(!Minecraft.getInstance().isSingleplayer()){
            lists(event.getRecipeManager());
        }
    }

    static void lists(RecipeManager recipeManager){
        Collection<IRecipe<?>> recipes = recipeManager.getRecipes();
        if(recipes.size() == 0){
            return;
        }

        ImmersiveGeology.getNewLogger().info("Loading Gravity Separator Recipes.");
        SeparatorRecipe.recipes = filterRecipes(recipes, SeparatorRecipe.class, SeparatorRecipe.TYPE);
    }

    static <R extends IRecipe<?>> Map<ResourceLocation, R> filterRecipes(Collection<IRecipe<?>> recipes, Class<R> recipeClass, IRecipeType<R> recipeType){
        return recipes.stream()
                .filter(iRecipe -> iRecipe.getType() == recipeType)
                .map(recipeClass::cast)
                .collect(Collectors.toMap(recipe -> recipe.getId(), recipe -> recipe));
    }
}