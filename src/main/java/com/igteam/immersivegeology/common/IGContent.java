package com.igteam.immersivegeology.common;

import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.IEBlocks;
import blusunrize.immersiveengineering.common.items.IEItems;
import com.igteam.immersivegeology.common.blocks.*;
import com.igteam.immersivegeology.common.items.IGBaseItem;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.lang.reflect.Field;
import java.util.*;

import static com.igteam.immersivegeology.ImmersiveGeology.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IGContent
{
	public static List<Block> registeredIGBlocks = new ArrayList<>();
	public static List<Item> registeredIGItems = new ArrayList<>();
	public static List<Class<? extends TileEntity>> registeredIGTiles = new ArrayList<>();
	public static List<Fluid> registeredIGFluids = new ArrayList<>();

	public static void modConstruction()
	{

		Block.Properties storageProperties = Block.Properties.create(Material.IRON).hardnessAndResistance(5, 10);
		Block.Properties sheetmetalProperties = Block.Properties.create(Material.IRON).hardnessAndResistance(3, 10);
		for(EnumMetals m : EnumMetals.values())
		{
			String name = m.getName();
			Block storage;
			Block sheetmetal;
			Item nugget;
			Item ingot;
			Item plate;
			Item dust;
			Item tiny_dust = new IGBaseItem("tiny_dust_"+name);

			if(m.isIGMetal())
			{
				storage = new IGBaseBlock("storage_"+name, storageProperties, IGBlockItem.class);
				sheetmetal = (IGBaseBlock)new IGBaseBlock("sheetmetal_"+name, sheetmetalProperties, IGBlockItem.class);
				addSlabFor((IGBaseBlock)sheetmetal);
				nugget = new IGBaseItem("nugget_"+name);
				ingot = new IGBaseItem("ingot_"+name);
				plate = new IGBaseItem("plate_"+name);
				dust = new IGBaseItem("dust_"+name);
				addSlabFor((IGBaseBlock)storage);
			}
			else
			{
				Enum COPPER = blusunrize.immersiveengineering.common.blocks.EnumMetals.COPPER;
				Enum ALUMINUM = blusunrize.immersiveengineering.common.blocks.EnumMetals.ALUMINUM;
				Enum LEAD = blusunrize.immersiveengineering.common.blocks.EnumMetals.LEAD;
				Enum SILVER = blusunrize.immersiveengineering.common.blocks.EnumMetals.SILVER;
				Enum NICKEL = blusunrize.immersiveengineering.common.blocks.EnumMetals.NICKEL;
				Enum URANIUM = blusunrize.immersiveengineering.common.blocks.EnumMetals.URANIUM;
				Enum CONSTANTAN = blusunrize.immersiveengineering.common.blocks.EnumMetals.CONSTANTAN;
				Enum ELECTRUM = blusunrize.immersiveengineering.common.blocks.EnumMetals.ELECTRUM;
				Enum STEEL = blusunrize.immersiveengineering.common.blocks.EnumMetals.STEEL;
				Enum IRON = blusunrize.immersiveengineering.common.blocks.EnumMetals.IRON;
				Enum GOLD = blusunrize.immersiveengineering.common.blocks.EnumMetals.GOLD;
				switch (name) {
					case "copper":
						storage = IEBlocks.Metals.storage.get(COPPER);
						sheetmetal = IEBlocks.Metals.sheetmetal.get(COPPER);
						nugget = IEItems.Metals.nuggets.get(COPPER);
						plate = IEItems.Metals.plates.get(COPPER);
						dust = IEItems.Metals.dusts.get(COPPER);
						break;
					case "aluminum":
						storage = IEBlocks.Metals.storage.get(ALUMINUM);
						sheetmetal = IEBlocks.Metals.sheetmetal.get(ALUMINUM);
						nugget = IEItems.Metals.nuggets.get(ALUMINUM);
						plate = IEItems.Metals.plates.get(ALUMINUM);
						dust = IEItems.Metals.dusts.get(ALUMINUM);

						break;
					case "lead":
						storage = IEBlocks.Metals.storage.get(LEAD);
						sheetmetal = IEBlocks.Metals.sheetmetal.get(LEAD);
						nugget = IEItems.Metals.nuggets.get(LEAD);
						plate = IEItems.Metals.plates.get(LEAD);
						dust = IEItems.Metals.dusts.get(LEAD);

						break;
					case "silver":
						storage = IEBlocks.Metals.storage.get(SILVER);
						sheetmetal = IEBlocks.Metals.sheetmetal.get(SILVER);
						nugget = IEItems.Metals.nuggets.get(SILVER);
						plate = IEItems.Metals.plates.get(SILVER);
						dust = IEItems.Metals.dusts.get(SILVER);

						break;
					case "nickel":
						storage = IEBlocks.Metals.storage.get(NICKEL);
						sheetmetal = IEBlocks.Metals.sheetmetal.get(NICKEL);
						nugget = IEItems.Metals.nuggets.get(NICKEL);
						plate = IEItems.Metals.plates.get(NICKEL);
						dust = IEItems.Metals.dusts.get(NICKEL);

						break;
					case "uranium":
						storage = IEBlocks.Metals.storage.get(URANIUM);
						sheetmetal = IEBlocks.Metals.sheetmetal.get(URANIUM);
						nugget = IEItems.Metals.nuggets.get(URANIUM);
						plate = IEItems.Metals.plates.get(URANIUM);
						dust = IEItems.Metals.dusts.get(URANIUM);

						break;
					case "constantan":
						storage = IEBlocks.Metals.storage.get(CONSTANTAN);
						sheetmetal = IEBlocks.Metals.sheetmetal.get(CONSTANTAN);
						nugget = IEItems.Metals.nuggets.get(CONSTANTAN);
						plate = IEItems.Metals.plates.get(CONSTANTAN);
						dust = IEItems.Metals.dusts.get(CONSTANTAN);

						break;
					case "electrum":
						storage = IEBlocks.Metals.storage.get(ELECTRUM);
						sheetmetal = IEBlocks.Metals.sheetmetal.get(ELECTRUM);
						nugget = IEItems.Metals.nuggets.get(ELECTRUM);
						plate = IEItems.Metals.plates.get(ELECTRUM);
						dust = IEItems.Metals.dusts.get(ELECTRUM);

						break;
					case "steel":
						storage = IEBlocks.Metals.storage.get(STEEL);
						sheetmetal = IEBlocks.Metals.sheetmetal.get(STEEL);
						nugget = IEItems.Metals.nuggets.get(STEEL);
						plate = IEItems.Metals.plates.get(STEEL);
						dust = IEItems.Metals.dusts.get(STEEL);

						break;
					case "iron":
						storage = IEBlocks.Metals.storage.get(IRON);
						sheetmetal = IEBlocks.Metals.sheetmetal.get(IRON);
						nugget = IEItems.Metals.nuggets.get(IRON);
						plate = IEItems.Metals.plates.get(IRON);
						dust = IEItems.Metals.dusts.get(IRON);

						break;
					case "gold":
						storage = IEBlocks.Metals.storage.get(GOLD);
						sheetmetal = IEBlocks.Metals.sheetmetal.get(GOLD);
						nugget = IEItems.Metals.nuggets.get(GOLD);
						plate = IEItems.Metals.plates.get(GOLD);
						dust = IEItems.Metals.dusts.get(GOLD);

						break;
				}
			}
		}

	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		checkNonNullNames(registeredIGBlocks);
		for(Block block : registeredIGBlocks)
			event.getRegistry().register(block);
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		checkNonNullNames(registeredIGItems);
		for(Item item : registeredIGItems)
			event.getRegistry().register(item);
		registerOres();
	}

	private static <T extends IForgeRegistryEntry<T>> void checkNonNullNames(Collection<T> coll)
	{
		int numNull = 0;
		for(T b : coll)
			if(b.getRegistryName()==null)
			{
				++numNull;
			}
		if(numNull > 0)
			System.exit(1);
	}

	@SubscribeEvent
	public static void registerFluids(RegistryEvent.Register<Fluid> event)
	{
		checkNonNullNames(registeredIGFluids);
		for(Fluid fluid : registeredIGFluids)
			event.getRegistry().register(fluid);
	}

	private static <T extends Block & IIGBlock> BlockIGSlab addSlabFor(T b)
	{
		BlockIGSlab<T> ret = new BlockIGSlab<>(
				"slab_"+b.getRegistryName().getPath(),
				Block.Properties.from(b),
				IGBlockItem.class,
				b
		);
		IGBlocks.toSlab.put(b, ret);
		return ret;
	}

	@SubscribeEvent
	public static void registerTEs(RegistryEvent.Register<TileEntityType<?>> event)
	{

	}

	public static void registerRecipes()
	{

	}

	public static void registerOres()
	{

	}

	public static void init()
	{

	}

	public static void postInit()
	{

	}

	public static <T extends TileEntity> void registerTile(Class<T> tile, RegistryEvent.Register<TileEntityType<?>> event, Block... valid)
	{
		String s = tile.getSimpleName();
		s = s.substring(0, s.indexOf("TileEntity")).toLowerCase(Locale.ENGLISH);
		Set<Block> validSet = new HashSet<>(Arrays.asList(valid));
		TileEntityType<T> type = new TileEntityType<>(() -> {
			try
			{
				return tile.newInstance();
			} catch(InstantiationException|IllegalAccessException e)
			{
				e.printStackTrace();
			}
			return null;
		}, validSet, null); //TODO where do I get a Type<T> from?
		type.setRegistryName(MODID, s);
		event.getRegistry().register(type);
		try
		{
			Field typeField = tile.getField("TYPE");
			typeField.set(null, type);
		} catch(NoSuchFieldException|IllegalAccessException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		registeredIGTiles.add(tile);
	}

	//TODO: Absolutely needed TODO
    /* TODO
    public static void addConfiguredWorldgen(Block state, String name, OreConfig config)
    {
        if(config!=null&&config.veinSize.get() > 0)
            IGWorldGen.addOreGen(name, state.getDefaultState(), config.veinSize.get(),
                    config.minY.get(),
                    config.maxY.get(),
                    config.veinsPerChunk.get(),
                    config.spawnChance.get());
    } */

}
