package igteam.immersive_geology.processing.methods;

import blusunrize.immersiveengineering.api.EnumMetals;
import blusunrize.immersiveengineering.api.IEApi;
import blusunrize.immersiveengineering.api.IEEnums;
import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.common.items.IEItems;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IGProcessingMethod;
import igteam.immersive_geology.processing.helper.RecipeMethod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;

public class IGBlastingMethod extends IGProcessingMethod {
    public IGBlastingMethod(IGProcessingStage stage) {
        super(RecipeMethod.Blasting, stage);
    }
    private ItemStack output, slag;
    private ITag<Item> input;

    private String name;

    public void create(String method_name, ItemStack output, ITag<Item> input){
        this.input = input;
        this.output = output;
        this.name = method_name;
        this.slag = new ItemStack(IEItems.Ingredients.slag);
    }

    public void create(String method_name, ItemStack output, ITag<Item> input, ItemStack slag){
        this.input = input;
        this.output = output;
        this.name = method_name;
        this.slag = slag;
    }

    public ItemStack getOutput() {
        return output;
    }

    public ITag<Item> getInput() {
        return input;
    }

    public ItemStack getSlag() {
        return slag;
    }
    public String getMethodName() {
        return name;
    }
}
