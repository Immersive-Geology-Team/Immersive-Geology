package com.igteam.immersivegeology.core.material.helper.flags;

import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.core.material.helper.material.IStoneType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.util.BlockRenderLayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public interface IFlagType<T extends Enum<T>>
{
	T getValue();

	static List<IFlagType<?>> getAllRegistryFlags()
	{
		List<IFlagType<?>> list = new ArrayList<IFlagType<?>>();
		list.addAll(Arrays.asList(BlockCategoryFlags.values()));
		list.addAll(Arrays.asList(ItemCategoryFlags.values()));
		return list;
	}

	default String getName()
	{
		return getValue().name().toLowerCase(Locale.ROOT);
	}

	default String getRegistryKey(MaterialHelper material)
	{
		return getName()+"_"+material.getName().toLowerCase(Locale.ROOT);
	}

	default String getRegistryKey(MaterialHelper ore, MaterialHelper stone)
	{
		return stonePrefix(stone)+getName()+"_"+ore.getName().toLowerCase(Locale.ROOT)
				+"_"+stone.getName().toLowerCase(Locale.ROOT);
	}

	default String getRegistryKey(MaterialInterface<?> material)
	{
		return getRegistryKey(material.instance());
	}

	default String getRegistryKey(MaterialInterface<?> ore, MaterialInterface<?> stone)
	{
		return getRegistryKey(ore.instance(), stone.instance());
	}

	default String getRegistryKey(MaterialInterface<?> material, BlockCategoryFlags blockCategory)
	{
		return getName()+"_"+material.getName().toLowerCase(Locale.ROOT)
				+"_"+blockCategory.getName().toLowerCase(Locale.ROOT);
	}

	static String stonePrefix(Object stone)
	{
		if(stone instanceof IStoneType) return ((IStoneType)stone).getRegistryPrefix();

		String prefix = "";
		if(stone instanceof MaterialHelper)
		{
			MaterialHelper helper = (MaterialHelper)stone;
			for(ModFlags modflag : ModFlags.values())
				if(helper.hasFlag(modflag)) prefix = modflag.getName()+"_";
		}
		return prefix;
	}

	default String getOreDictName(MaterialHelper material)
	{
		String base = getOreDictBase();
		if(base.isEmpty()) return "";
		String name = material.getName();
		return base+Character.toUpperCase(name.charAt(0))+name.substring(1);
	}

	default String getOreDictBase()
	{
		String[] parts = getValue().name().toLowerCase(Locale.ROOT).split("_");
		StringBuilder builder = new StringBuilder(parts[0]);
		for(int i = 1; i < parts.length; i++)
			builder.append(Character.toUpperCase(parts[i].charAt(0))).append(parts[i].substring(1));
		return builder.toString();
	}

	default ItemSubGroup getSubGroup()
	{
		return ItemSubGroup.values()[0];
	}

	default BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.SOLID;
	}
}
