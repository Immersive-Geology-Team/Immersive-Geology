package igteam.immersive_geology.common.crafting.recipes;

import igteam.api.processing.recipe.*;
import igteam.immersive_geology.ImmersiveGeology;
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
public class RecipeReloadListener implements IResourceManagerReloadListener {
    private final DataPackRegistries dataPackRegistries;

    public RecipeReloadListener(DataPackRegistries dataPackRegistries) {
        this.dataPackRegistries = dataPackRegistries;
    }

    static void lists(RecipeManager recipeManager) {
        Collection<IRecipe<?>> recipes = recipeManager.getRecipes();
        if (recipes.size() == 0) {
            return;
        }

        ImmersiveGeology.getNewLogger().info("Loading Gravity Separator Recipes.");
        SeparatorRecipe.recipes = filterRecipes(recipes, SeparatorRecipe.class, SeparatorRecipe.TYPE);

        ImmersiveGeology.getNewLogger().info("Loading Chemical Vat Recipes.");
        VatRecipe.recipes = filterRecipes(recipes, VatRecipe.class, VatRecipe.TYPE);

        ImmersiveGeology.getNewLogger().info("Loading Bloomery Recipes");
        BloomeryRecipe.recipes = filterRecipes(recipes, BloomeryRecipe.class, BloomeryRecipe.TYPE);

        ImmersiveGeology.getNewLogger().info("Loading Crystalizer Recipes.");
        CrystalRecipe.recipes = filterRecipes(recipes, CrystalRecipe.class, CrystalRecipe.TYPE);

        ImmersiveGeology.getNewLogger().info("Loading Rotary Kiln Recipes.");
        CalcinationRecipe.recipes = filterRecipes(recipes, CalcinationRecipe.class, CalcinationRecipe.TYPE);

        ImmersiveGeology.getNewLogger().info("Loading Reverberation Furnace Recipes.");
        ReverberationRecipe.recipes = filterRecipes(recipes, ReverberationRecipe.class, ReverberationRecipe.TYPE);

        ImmersiveGeology.getNewLogger().info("Loading Hydrojet Cutter Recipes.");
        HydrojetRecipe.recipes = filterRecipes(recipes, HydrojetRecipe.class, HydrojetRecipe.TYPE);

    }

    static <R extends IRecipe<?>> Map<ResourceLocation, R> filterRecipes(Collection<IRecipe<?>> recipes, Class<R> recipeClass, IRecipeType<R> recipeType) {
        return recipes.stream()
                .filter(iRecipe -> iRecipe.getType() == recipeType)
                .map(recipeClass::cast)
                .collect(Collectors.toMap(recipe -> recipe.getId(), recipe -> recipe));
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        if (dataPackRegistries != null) {
            lists(dataPackRegistries.getRecipeManager());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void OnRecipesUpdated(RecipesUpdatedEvent event) {
        if (!Minecraft.getInstance().isSingleplayer()) {
            lists(event.getRecipeManager());
        }
    }
}