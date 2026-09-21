package com.igteam.immersivegeology.core.registration;

import blusunrize.immersiveengineering.api.MultiblockHandler;
import com.igteam.immersivegeology.common.block.IGGenericBlock;
import com.igteam.immersivegeology.common.block.multiblocks.IGMultiblockBlock;
import com.igteam.immersivegeology.common.block.multiblocks.entity.TileEntityBallmill;
import com.igteam.immersivegeology.common.block.multiblocks.structure.IGBallmillStructure;
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

		registerMultiblocks();

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

	private static void registerMultiblocks()
	{
		IGMultiblockBlock ballmill = new IGMultiblockBlock(IGBallmillStructure.INSTANCE, TileEntityBallmill::new);
		registerBlock(IGBallmillStructure.NAME, ballmill);
		registerItem(IGBallmillStructure.NAME, new net.minecraft.item.ItemBlock(ballmill));

		net.minecraftforge.fml.common.registry.GameRegistry.registerTileEntity(
				TileEntityBallmill.class, new ResourceLocation(IGLib.MODID, "ballmill"));

		MultiblockHandler.registerMultiblock(IGBallmillStructure.INSTANCE);

		IGLib.IG_LOGGER.info("- Registered multiblock: {}", IGBallmillStructure.NAME);
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

	public static void verifyMultiblocks()
	{
		com.igteam.immersivegeology.common.block.multiblocks.structure.IGMultiblockStructure structure =
				IGBallmillStructure.INSTANCE;
		net.minecraft.block.state.IBlockState trigger = structure.getTriggerState();
		IGLib.IG_LOGGER.info("- Multiblock {} trigger at {} resolves to {}",
				structure.getUniqueName(), structure.getTriggerOffset(), trigger);

		int unresolved = 0;
		net.minecraft.item.ItemStack[][][] manual = structure.getStructureManual();
		for(net.minecraft.item.ItemStack[][] layer : manual)
			for(net.minecraft.item.ItemStack[] row : layer)
				for(net.minecraft.item.ItemStack stack : row)
					if(stack==null||stack.isEmpty()) unresolved++;
		IGLib.IG_LOGGER.info("- Multiblock {} manual: {} layers, {} empty/air slots",
				structure.getUniqueName(), manual.length, unresolved);

		// Prove the hammer path: IE passes the clicked block's state straight to isBlockTrigger
		if(trigger!=null)
		{
			boolean accepts = structure.isBlockTrigger(trigger);
			IGLib.IG_LOGGER.info("- Multiblock {} isBlockTrigger(trigger state) = {}",
					structure.getUniqueName(), accepts);
			if(!accepts)
				IGLib.IG_LOGGER.error("- Multiblock {} will NOT form: trigger state rejected by its own check",
						structure.getUniqueName());

			boolean registered = blusunrize.immersiveengineering.api.MultiblockHandler.getMultiblocks()
					.contains(structure);
			IGLib.IG_LOGGER.info("- Multiblock {} present in IE registry = {}",
					structure.getUniqueName(), registered);
		}
	}

	@SubscribeEvent
	public static void onItemRegistry(RegistryEvent.Register<Item> event)
	{
		for(Item item : ITEMS) event.getRegistry().register(item);
		IGLib.IG_LOGGER.info("- Registered {} items", ITEMS.size());
		registerOreDictionary();
		verifyMultiblocks();
	}
}
