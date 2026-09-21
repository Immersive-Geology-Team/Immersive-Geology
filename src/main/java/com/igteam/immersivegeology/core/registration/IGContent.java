package com.igteam.immersivegeology.core.registration;

import com.igteam.immersivegeology.common.block.IGGenericBlock;
import com.igteam.immersivegeology.common.block.helper.OreBlockMeta;
import com.igteam.immersivegeology.common.block.ore.IGOreBlock;
import com.igteam.immersivegeology.common.item.IGGenericBlockItem;
import com.igteam.immersivegeology.common.item.IGGenericItem;
import com.igteam.immersivegeology.common.item.IGOreItemBlock;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.data.stone.IGStoneTypes;
import com.igteam.immersivegeology.core.material.helper.material.IStoneType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = IGLib.MODID)
public class IGContent
{
	private static final List<Block> BLOCKS = new ArrayList<>();
	private static final List<Item> ITEMS = new ArrayList<>();

	private static final Map<String, Block> BLOCK_REGISTRY_MAP = new LinkedHashMap<>();
	private static final Map<String, Item> ITEM_REGISTRY_MAP = new LinkedHashMap<>();

	private static boolean built = false;

	public static Block getBlock(String key)
	{
		return BLOCK_REGISTRY_MAP.get(key);
	}

	public static Item getItem(String key)
	{
		return ITEM_REGISTRY_MAP.get(key);
	}

	public static List<Item> getRegisteredItems()
	{
		return ITEMS;
	}

	public static List<Block> getRegisteredBlocks()
	{
		return BLOCKS;
	}

	public static Block registerBlock(String key, Block block)
	{
		block.setRegistryName(new ResourceLocation(IGLib.MODID, key));
		block.setTranslationKey(IGLib.MODID+"."+key);
		block.setCreativeTab(IGCreativeTabs.IG_BASE_TAB);
		BLOCKS.add(block);
		BLOCK_REGISTRY_MAP.put(key, block);
		return block;
	}

	public static Item registerItem(String key, Item item)
	{
		item.setRegistryName(new ResourceLocation(IGLib.MODID, key));
		item.setTranslationKey(IGLib.MODID+"."+key);
		item.setCreativeTab(IGCreativeTabs.IG_BASE_TAB);
		ITEMS.add(item);
		ITEM_REGISTRY_MAP.put(key, item);
		return item;
	}

	private static void buildContent()
	{
		if(built) return;
		built = true;

		int blockCount = 0;
		int itemCount = 0;

		for(MaterialInterface<?> material : IGLib.getGeologyMaterials())
		{
			for(IFlagType<?> flag : material.getFlags())
			{
				boolean existing = material.instance().checkExistingImplementation(flag);

				if(flag instanceof BlockCategoryFlags blockCategory)
				{
					switch(blockCategory)
					{
						case ORE_BLOCK -> blockCount += registerOreBlocks(material);
						case DEFAULT_BLOCK, STORAGE_BLOCK, SHEETMETAL_BLOCK, DUST_BLOCK, GEODE_BLOCK,
							 ENGINEERING_BLOCK, ADVANCED_ENGINEERING_BLOCK, SCAFFOLDING ->
						{
							if(existing) continue;
							String key = blockCategory.getRegistryKey(material);
							IGGenericBlock block = new IGGenericBlock(blockCategory, material);
							registerBlock(key, block);
							registerItem(key, new IGGenericBlockItem(block));
							blockCount++;
						}
						default ->
						{
						}
					}
					continue;
				}

				if(flag instanceof ItemCategoryFlags itemCategory)
				{
					if(existing) continue;
					if(isOreItem(itemCategory)) continue;

					registerItem(itemCategory.getRegistryKey(material), new IGGenericItem(itemCategory, material));
					itemCount++;
				}
			}
		}

		IGLib.IG_LOGGER.info("- Built {} material blocks and {} material items", blockCount, itemCount);
	}

	private static boolean isOreItem(ItemCategoryFlags category)
	{
		return category==ItemCategoryFlags.POOR_ORE
				||category==ItemCategoryFlags.NORMAL_ORE
				||category==ItemCategoryFlags.RICH_ORE;
	}

	private static int registerOreBlocks(MaterialInterface<?> material)
	{
		int registered = 0;
		for(IStoneType stone : IGStoneTypes.all())
		{
			if(!stone.isStoneTypeValid()) continue;
			if(!material.instance().acceptableStoneType(stone)) continue;
			if(stone.excludesOre(material.instance())) continue;

			String key = BlockCategoryFlags.ORE_BLOCK.getRegistryKey(material.instance(), stone.instance());
			IGOreBlock block = new IGOreBlock(material, stone);
			registerBlock(key, block);
			registerItem(key, new IGOreItemBlock(block));
			registered++;
		}
		return registered;
	}

	private static void registerOreDictionary()
	{
		int entries = 0;
		for(Map.Entry<String, Item> entry : ITEM_REGISTRY_MAP.entrySet())
		{
			Item item = entry.getValue();
			if(!(item instanceof IGGenericItem generic)) continue;

			String name = generic.getCategory().getOreDictName(generic.getMaterial().instance());
			if(name.isEmpty()) continue;

			OreDictionary.registerOre(name, item);
			entries++;
		}
		IGLib.IG_LOGGER.info("- Registered {} OreDictionary entries", entries);
	}

	@SubscribeEvent
	public static void onBlockRegistry(RegistryEvent.Register<Block> event)
	{
		buildContent();
		for(Block block : BLOCKS) event.getRegistry().register(block);
		IGLib.IG_LOGGER.info("- Registered {} blocks", BLOCKS.size());
	}

	@SubscribeEvent
	public static void onItemRegistry(RegistryEvent.Register<Item> event)
	{
		for(Item item : ITEMS) event.getRegistry().register(item);
		IGLib.IG_LOGGER.info("- Registered {} items", ITEMS.size());
		registerOreDictionary();
	}
}
