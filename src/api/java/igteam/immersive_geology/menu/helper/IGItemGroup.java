package igteam.immersive_geology.menu.helper;

import igteam.immersive_geology.item.IGItemType;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.materials.pattern.MiscPattern;
import igteam.immersive_geology.menu.ItemSubGroup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class IGItemGroup extends CreativeModeTab {
    private static final ResourceLocation CMB_TEXTURES = new ResourceLocation("immersive_geology", "textures/gui/ig_tab_menu.png");
    private static final ResourceLocation CMT_TEXTURES = new ResourceLocation("immersive_geology", "textures/gui/ig_tabs.png");

    public static ItemSubGroup selectedGroup = ItemSubGroup.natural;
    public static final IGItemGroup IGGroup = new IGItemGroup("immersive_geology");

    public IGItemGroup(String label) {
        super(label);
        setBackgroundImage(CMB_TEXTURES);
    }

    @Override
    public int getLabelColor() {
        return 0xffd700;
    }

    @Override
    public ResourceLocation getTabsImage() {
        return CMT_TEXTURES;
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(Items.RAW_GOLD);
    }

    public static void updateSubGroup(ItemSubGroup group) {
        selectedGroup = group;
    }

    public static ItemSubGroup getCurrentSubGroup() {
        return selectedGroup;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillItemList(NonNullList<ItemStack> items) {
        HashMap<MaterialPattern, ArrayList<Item>> itemMap = new HashMap<>();
        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            if(item instanceof IGItemType type){
                if(type.getSubGroup() == selectedGroup) {
                    MaterialPattern pattern = type.getPattern();
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
        }
        ArrayList<MaterialPattern> allPatternList = new ArrayList<>();
        allPatternList.addAll(Arrays.stream(ItemPattern.values()).toList());
        allPatternList.addAll(Arrays.stream(BlockPattern.values()).toList());
        allPatternList.addAll(Arrays.stream(MiscPattern.values()).toList());

        for (MaterialPattern pattern : allPatternList)
        {
            if(itemMap.containsKey(pattern)){
                ArrayList<Item> list = itemMap.get(pattern);
                for (Item item : list) {
                    item.fillItemCategory(this, items);
                }
            }
        }
    }
}
