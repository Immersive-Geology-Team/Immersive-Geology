package com.igteam.immersivegeology.core.material.helper.flags;

import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.tag.IGTags;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import javax.print.DocFlavor.READER;

public enum ItemCategoryFlags implements IFlagType<ItemCategoryFlags> {
    INGOT(1),
    WIRE(1),
    GEAR(1),
    ROD(1),
    CLAY(0),
    POWDER(2),
    GRIT (2),
    FUEL(2),
    SLAG(2),
    PELLET(2),
    OXIDE_PELLET(2),
    POWDERED_SLAG(2),
    PLATE(1),
    POOR_ORE(0),
    NORMAL_ORE(0),
    RICH_ORE(0),
    NUGGET(1),
    CRYSTAL(3),
    COMPOUND_DUST(3),
    CRUSHED_ORE(2),
    METAL_OXIDE(3),
    DIRTY_CRUSHED_ORE(2),
    BUCKET(3),
    CLEAN_FLASK(3),
    CLOUDY_FLASK(3),
    MECHANICAL_COMPONENT(1),
    HAMMER(4),
    DRILL_HEAD(4),
    MISC(4),
    BLUEPRINT(4),
    SKIN_COMPONENT(4),
    TOOL_HOE(4),
    SEDIMENT(0);

    private final int groupOrdinal;

    ItemCategoryFlags(int group){
        groupOrdinal = group;
    }

    @Override
    public ItemCategoryFlags getValue() {
        return this;
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return ItemSubGroup.values()[groupOrdinal];
    }

    public String getTagPrefix()
    {
        switch(this){
            case INGOT, POWDER, GRIT, GEAR,NUGGET,PLATE,ROD,WIRE,PELLET:
                return "s";
            default:
                return "";
        }
    }

	public int getVariations()
	{
		return switch(this)
		{
			case INGOT -> 7;
			case GEAR, NUGGET -> 6;
            case CRUSHED_ORE -> 4;
            case RICH_ORE, NORMAL_ORE, POOR_ORE -> 6;
            case PLATE, DIRTY_CRUSHED_ORE -> 2;
			default -> 1;
		};
	}

    public boolean hasPalette()
    {
        return switch(this)
        {
            case INGOT, GEAR, CRUSHED_ORE, POOR_ORE, NORMAL_ORE, RICH_ORE, NUGGET,
                 DIRTY_CRUSHED_ORE, PLATE, SLAG, GRIT, POWDER, METAL_OXIDE, COMPOUND_DUST, DRILL_HEAD, TOOL_HOE -> true;
            default -> false;
        };
    }

    public TagKey<Item> getCategoryTag()
    {
        return IGTags.ITEM_CATEGORY_FLAGS.get(this);
    }

}
