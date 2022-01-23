package igteam.immersive_geology.processing.methods;

import igteam.immersive_geology.materials.data.MaterialBase;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IGProcessingMethod;
import igteam.immersive_geology.processing.helper.RecipeMethod;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;

public class IGCraftingMethod extends IGProcessingMethod {
    public IGCraftingMethod(IGProcessingStage stage) {
        super(RecipeMethod.Crafting, stage);
    }

    private String[] patterns;
    private Tag<Item>[] shapelessInputs;
    private ItemLike result;
    private int amount;
    private String recipeGroup;
    private String criterionName;
    private Tag<Item> criterionTriggerItem;

    /**
     * @param patternLines - input up to 3, 3 character long strings
     * @return
     */
    public IGCraftingMethod crafting(ItemLike result, int amount, String... patternLines){
        this.patterns = patternLines;
        this.result = result;
        this.amount = amount;
        return this;
    }

    public IGCraftingMethod shapeless(ItemLike result, int amount, Tag<Item>... inputs) {
        this.result = result;
        this.amount = amount;
        this.shapelessInputs = inputs;
        return this;
    }

    public IGCraftingMethod finializeRecipe(String group, String criterionName, Tag<Item> trigger){
        this.recipeGroup = group;
        this.criterionName = criterionName;
        this.criterionTriggerItem = trigger;
        return this;
    }

    public ItemLike getResult() {
        return result;
    }

    public int getResultAmount() {
        return amount;
    }

    public Tag<Item>[] getInputTags() {
        return shapelessInputs;
    }

    public String getRecipeGroup() {
        return recipeGroup;
    }

    public Tag<Item> getCriterionTrigger() {
        return criterionTriggerItem;
    }

    public String getCriterionName() {
        return criterionName;
    }

    public String[] getPatterns() {
        return patterns;
    }
}
