package igteam.immersive_geology.processing.methods;

import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IGProcessingMethod;
import igteam.immersive_geology.processing.helper.RecipeMethod;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class IGCraftingMethod extends IGProcessingMethod {
    public IGCraftingMethod(IGProcessingStage stage) {
        super(RecipeMethod.Crafting, stage);
    }

    private String[] patterns;
    private ITag<Item>[] shapelessInputs;
    private Item result;
    private int amount;
    private String recipeGroup;
    private String criterionName;
    private ITag<Item> criterionTriggerItem;

    /**
     * @param patternLines - input up to 3, 3 character long strings
     * @return
     */
    public IGCraftingMethod crafting(Item result, int amount, String... patternLines){
        this.patterns = patternLines;
        this.result = result;
        this.amount = amount;
        return this;
    }

    public IGCraftingMethod shapeless(Item result, int amount, ITag<Item>... inputs) {
        this.result = result;
        this.amount = amount;
        this.shapelessInputs = inputs;
        return this;
    }

    public IGCraftingMethod finializeRecipe(String group, String criterionName, ITag<Item> trigger){
        this.recipeGroup = group;
        this.criterionName = criterionName;
        this.criterionTriggerItem = trigger;
        return this;
    }

    public Item getResult() {
        return result;
    }

    public int getResultAmount() {
        return amount;
    }

    public ITag<Item>[] getInputTags() {
        return shapelessInputs;
    }

    public String getRecipeGroup() {
        return recipeGroup;
    }

    public ITag<Item> getCriterionTrigger() {
        return criterionTriggerItem;
    }

    public String getCriterionName() {
        return criterionName;
    }

    public String[] getPatterns() {
        return patterns;
    }

    @Override
    public ResourceLocation getLocation() {
        return toRL("shapeless/craft_" + Objects.requireNonNull(getResult().asItem().getRegistryName()).getPath());
    }
}
