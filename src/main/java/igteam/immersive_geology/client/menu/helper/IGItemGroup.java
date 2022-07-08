package igteam.immersive_geology.client.menu.helper;

import igteam.immersive_geology.common.item.IGGenericBlockItem;
import igteam.api.IGApi;
import igteam.api.block.IGBlockType;
import igteam.api.item.IGItemType;
import igteam.api.materials.pattern.BlockPattern;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.materials.pattern.MaterialPattern;
import igteam.api.materials.pattern.FluidPattern;
import igteam.api.menu.ItemSubGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class IGItemGroup extends ItemGroup {
    private static final ResourceLocation CMB_TEXTURES = new ResourceLocation("immersive_geology", "textures/gui/ig_tab_menu.png");
    private static final ResourceLocation CMT_TEXTURES = new ResourceLocation("immersive_geology", "textures/gui/ig_tabs.png");

    public static ItemSubGroup selectedGroup = ItemSubGroup.natural;
    public static final IGItemGroup IGGroup = new IGItemGroup("immersive_geology");

    public IGItemGroup(String label) {
        super(label);
        setBackgroundImage(CMB_TEXTURES);
    }

    @Override
    public ITextComponent getGroupName() {
        return new TranslationTextComponent("itemGroup.immersive_geology." + selectedGroup.name());
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(Items.GOLD_INGOT);
    }

    @Override
    public int getLabelColor() {
        return 0xffd700;
    }

    @Override
    public ResourceLocation getTabsImage() {
        return CMT_TEXTURES;
    }

    public static void updateSubGroup(ItemSubGroup group) {
        selectedGroup = group;
    }

    Logger log = IGApi.getNewLogger();
    public static ItemSubGroup getCurrentSubGroup() {
        return selectedGroup;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fill(NonNullList<ItemStack> items) {
        HashMap<MaterialPattern, ArrayList<Item>> itemMap = new HashMap<>();
        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            if(item instanceof IGItemType){
                IGItemType type = (IGItemType) item;
                MaterialPattern pattern = type.getPattern();
                if(type.getSubGroup() == selectedGroup && pattern != ItemPattern.block_item) {
                    if (itemMap.containsKey(pattern)) {
                        ArrayList<Item> list = itemMap.get(pattern);
                        list.add(item);
                        itemMap.replace(pattern, list);
                    } else {
                        ArrayList<Item> list = new ArrayList<>();
                        list.add(item);
                        itemMap.put(pattern, list);
                    }
                }
            }

            if(item instanceof IGGenericBlockItem){
                IGBlockType blockType = ((IGGenericBlockItem) item).getIGBlockType();
                MaterialPattern blockPattern = blockType.getPattern();
                if(blockPattern.getSubGroup() == selectedGroup){
                    if (itemMap.containsKey(blockPattern)) {
                        ArrayList<Item> list = itemMap.get(blockPattern);
                        list.add(item);
                        itemMap.replace(blockPattern, list);
                    } else {
                        ArrayList<Item> list = new ArrayList<>();
                        list.add(item);
                        itemMap.put(blockPattern, list);
                    }
                }
            }
        }

        ArrayList<MaterialPattern> allPatternList = new ArrayList<>();
        allPatternList.addAll(Arrays.asList(ItemPattern.values()));
        allPatternList.addAll(Arrays.asList(BlockPattern.values()));
        allPatternList.addAll(Arrays.asList(FluidPattern.values()));

        for (MaterialPattern pattern : allPatternList)
        {
            if(itemMap.containsKey(pattern)){
                ArrayList<Item> list = itemMap.get(pattern);
                for (Item item : list) {
                    item.fillItemGroup(this, items);
                }
            }
        }
    }
}
