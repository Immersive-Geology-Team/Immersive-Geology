package igteam.api.processing.methods;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.api.IGApi;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGProcessingMethod;
import igteam.api.processing.helper.RecipeMethod;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

import java.util.*;

import static java.util.Arrays.asList;

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

    private boolean isShapeless = false;

    /**
     * @param patternLines - input up to 3, 3 character long strings
     * @return
     */
    public IGCraftingMethod shaped(Item result, int amount, String... patternLines){
        this.patterns = patternLines;
        this.result = result;
        this.amount = amount;
        this.isShapeless = false;
        return this;
    }

    public IGCraftingMethod shapeless(Item result, int amount, ITag<Item>... inputs) {
        this.result = result;
        this.amount = amount;
        this.shapelessInputs = inputs;
        this.isShapeless = true;
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
        String inputLoc = "";
        if(isShapeless) {
            if (Arrays.stream(getInputTags()).findFirst().isPresent()) {
                inputLoc = Objects.requireNonNull(Arrays.stream(getInputTags()).findFirst().get().toString());
                int startPos = inputLoc.indexOf('/') + 1;
                inputLoc = inputLoc.substring(startPos).replace("]", "");
            }
        } else {
            List<Item> shapedArray = asList(getCharacterInputMap().values().toArray(new Item[getCharacterInputMap().size()]));
            if (!shapedArray.isEmpty()) {
                StringBuilder locBuilder = new StringBuilder();
                for (Item i : shapedArray) {
                    locBuilder.append(i.getRegistryName().getPath() + "_");
                }
                inputLoc = locBuilder.toString() + "shaped";
            }
        }

        String type = isShapeless ? "shapeless" : "shaped";
        return toRL(type + "/craft_" + inputLoc + "_" + Objects.requireNonNull(getResult().getRegistryName()).getPath());
    }

    @Override
    public String getName() {
        String inputLoc = "";
        if(isShapeless) {
            if (Arrays.stream(getInputTags()).findFirst().isPresent()) {
                inputLoc = Objects.requireNonNull(Arrays.stream(getInputTags()).findFirst().get().toString());
                int startPos = inputLoc.indexOf('/') + 1;
                inputLoc = inputLoc.substring(startPos).replace("]", "");
            }
        } else {
            List<Item> shapedArray = asList(getCharacterInputMap().values().toArray(new Item[getCharacterInputMap().size()]));
            if (!shapedArray.isEmpty()) {
                StringBuilder locBuilder = new StringBuilder();
                for (Item i : shapedArray) {
                    locBuilder.append(i.getRegistryName().getPath() + "_");
                }
                inputLoc = locBuilder.toString() + "shaped";
            }
        }
        return "craft_" + inputLoc + "_" + Objects.requireNonNull(getResult().getRegistryName()).getPath();
    }

    protected HashMap<Character, Item> inputMap = new HashMap<>();

    public IGCraftingMethod setInputToCharacter(Character c, Item item) {
        inputMap.putIfAbsent(c, item);
        return this;
    }

    public boolean isShaped() {
        return !isShapeless;
    }

    public HashMap<Character, Item> getCharacterInputMap() {
        return inputMap;
    }

}
