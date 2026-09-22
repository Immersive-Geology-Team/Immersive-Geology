package com.igteam.immersivegeology.core.registration;

import blusunrize.immersiveengineering.api.MultiblockHandler;
import com.igteam.immersivegeology.common.block.IGGenericBlock;
import com.igteam.immersivegeology.common.block.multiblocks.IGMultiblockBlock;
import com.igteam.immersivegeology.common.block.multiblocks.entity.TileEntityBallmill;
import com.igteam.immersivegeology.common.block.multiblocks.structure.IGBallmillStructure;
import com.igteam.immersivegeology.common.block.multiblocks.structure.IGBloomeryStructure;
import com.igteam.immersivegeology.common.block.multiblocks.entity.TileEntityBloomery;
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
						case SLAB, SHEETMETAL_SLAB ->
						{
							if(existing) continue;
							blockCount += registerSlab(blockCategory, material);
						}
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
		registerMultiblock(IGBallmillStructure.INSTANCE, IGBallmillStructure.NAME,
				TileEntityBallmill::new, TileEntityBallmill.class);
		registerMultiblock(IGBloomeryStructure.INSTANCE, IGBloomeryStructure.NAME,
				TileEntityBloomery::new, TileEntityBloomery.class,
				com.igteam.immersivegeology.common.gui.IGGuiHandler.BLOOMERY);
		registerMultiblock(
				com.igteam.immersivegeology.common.block.multiblocks.structure.IGRevFurnaceStructure.INSTANCE,
				com.igteam.immersivegeology.common.block.multiblocks.structure.IGRevFurnaceStructure.NAME,
				com.igteam.immersivegeology.common.block.multiblocks.entity.TileEntityRevFurnace::new,
				com.igteam.immersivegeology.common.block.multiblocks.entity.TileEntityRevFurnace.class,
				com.igteam.immersivegeology.common.gui.IGGuiHandler.REVERBERATION_FURNACE);
	}

	private static void registerMultiblock(
			com.igteam.immersivegeology.common.block.multiblocks.structure.IGMultiblockStructure structure,
			String name,
			java.util.function.Supplier<net.minecraft.tileentity.TileEntity> tileFactory,
			Class<? extends net.minecraft.tileentity.TileEntity> tileClass)
	{
		registerMultiblock(structure, name, tileFactory, tileClass, -1);
	}

	private static void registerMultiblock(
			com.igteam.immersivegeology.common.block.multiblocks.structure.IGMultiblockStructure structure,
			String name,
			java.util.function.Supplier<net.minecraft.tileentity.TileEntity> tileFactory,
			Class<? extends net.minecraft.tileentity.TileEntity> tileClass,
			int guiId)
	{
		IGMultiblockBlock block = new IGMultiblockBlock(structure, tileFactory, guiId);
		registerBlock(name, block);
		registerItem(name, new net.minecraft.item.ItemBlock(block));

		net.minecraftforge.fml.common.registry.GameRegistry.registerTileEntity(
				tileClass, new ResourceLocation(IGLib.MODID, name));

		MultiblockHandler.registerMultiblock(structure);

		IGLib.IG_LOGGER.info("- Registered multiblock: {}", name);
	}

	private static int registerSlab(BlockCategoryFlags category, MaterialInterface<?> material)
	{
		String key = category.getRegistryKey(material);
		String doubleKey = "double_"+key;

		com.igteam.immersivegeology.common.block.IGSlabBlock single =
				new com.igteam.immersivegeology.common.block.IGSlabBlock(category, material, false);
		com.igteam.immersivegeology.common.block.IGSlabBlock doubled =
				new com.igteam.immersivegeology.common.block.IGSlabBlock(category, material, true);

		single.setSingleSlab(single);
		doubled.setSingleSlab(single);

		registerBlock(key, single);
		registerBlock(doubleKey, doubled);
		registerItem(key, new net.minecraft.item.ItemSlab(single, single, doubled));

		return 2;
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
		boolean rotationsOk = com.igteam.immersivegeology.common.block.multiblocks.structure
				.IGStructureFormer.transformsAreProperRotations();
		if(!rotationsOk)
			IGLib.IG_LOGGER.error("- Multiblock transforms are not a proper rotation group with identity; no template can form");
		else
			IGLib.IG_LOGGER.info("- Multiblock transforms verified: 4 proper rotations, identity present");

		boolean facesOk = com.igteam.immersivegeology.common.block.multiblocks.shim.util
				.RelativeBlockFace.selfTest();
		if(!facesOk)
			IGLib.IG_LOGGER.error("- RelativeBlockFace mapping is not self-consistent; capabilities will face the wrong way");

		boolean levelOk = true;
		for(net.minecraft.util.EnumFacing f : net.minecraft.util.EnumFacing.HORIZONTALS)
		{
			com.igteam.immersivegeology.common.block.multiblocks.shim.env.IGMultiblockLevel probe =
					new com.igteam.immersivegeology.common.block.multiblocks.shim.env.IGMultiblockLevel(
							null, net.minecraft.util.math.BlockPos.ORIGIN,
							new com.igteam.immersivegeology.common.block.multiblocks.shim.util
									.MultiblockOrientation(f, false));
			for(int x = 0; x <= 6; x++)
				for(int z = 0; z <= 6; z++)
				{
					net.minecraft.util.math.BlockPos cell = new net.minecraft.util.math.BlockPos(x, 0, z);
					if(!probe.toAbsolute(cell).equals(
							com.igteam.immersivegeology.common.block.multiblocks.structure
									.IGStructureFormer.rotateOffset(cell, f)))
						levelOk = false;
				}
		}
		if(!levelOk)
			IGLib.IG_LOGGER.error("- IGMultiblockLevel.toAbsolute disagrees with the structure former; outputs and capability lookups will target the wrong blocks");

		for(com.igteam.immersivegeology.common.block.multiblocks.structure.IGMultiblockStructure structure :
				java.util.List.of(IGBallmillStructure.INSTANCE, IGBloomeryStructure.INSTANCE,
						com.igteam.immersivegeology.common.block.multiblocks.structure.IGRevFurnaceStructure.INSTANCE))
		{
			net.minecraft.block.state.IBlockState trigger = structure.getTriggerState();
			net.minecraft.item.ItemStack[][][] manual = structure.getStructureManual();
			int empty = 0;
			for(net.minecraft.item.ItemStack[][] layer : manual)
				for(net.minecraft.item.ItemStack[] row : layer)
					for(net.minecraft.item.ItemStack stack : row)
						if(stack==null||stack.isEmpty()) empty++;

			boolean accepts = trigger!=null&&structure.isBlockTrigger(trigger);
			boolean registered = blusunrize.immersiveengineering.api.MultiblockHandler.getMultiblocks()
					.contains(structure);

			IGLib.IG_LOGGER.info("- {}: trigger {} at {} accepted={} registered={} dims=[{}][{}][{}] air={}",
					structure.getUniqueName(), trigger, structure.getTriggerOffset(), accepts, registered,
					manual.length,
					manual.length > 0?manual[0].length: 0,
					manual.length > 0&&manual[0].length > 0?manual[0][0].length: 0,
					empty);

			if(trigger==null||!accepts||!registered)
				IGLib.IG_LOGGER.error("- {} will NOT form", structure.getUniqueName());
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
