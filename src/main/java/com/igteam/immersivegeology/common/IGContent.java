package com.igteam.immersivegeology.common;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.common.blocks.IEBlocks;
import blusunrize.immersiveengineering.common.items.IEItems;
import blusunrize.immersiveengineering.common.util.IELogger;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialMetalBase;
import com.igteam.immersivegeology.common.blocks.*;
import com.igteam.immersivegeology.common.blocks.metal.IGSheetmetalBlock;
import com.igteam.immersivegeology.common.blocks.metal.IGStorageBlock;
import com.igteam.immersivegeology.common.items.IGMaterialItem;
import net.minecraft.block.Block;
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
		//Block.Properties storageProperties = Block.Properties.create(Material.IRON).hardnessAndResistance(5, 10);
		//Block.Properties sheetmetalProperties = Block.Properties.create(Material.IRON).hardnessAndResistance(3, 10);
		for(EnumMetals m : EnumMetals.values())
		{
			MaterialMetalBase material = m.metal;

			Block storage = null;
			Block sheetmetal = null;
			Item nugget = null;
			Item ingot = null;
			Item plate = null;
			Item dust = null;
			Item rod = null;
			Item gear = null;
			Item tiny_dust = null;
			Item wire = null;

			if(m.metal.getModID().equals(ImmersiveEngineering.MODID))
			{
				blusunrize.immersiveengineering.common.blocks.EnumMetals ieMetal = null;
				try
				{
					ieMetal = blusunrize.immersiveengineering.common.blocks.EnumMetals.valueOf(material.getName().toUpperCase(Locale.ENGLISH));
				} catch(IllegalArgumentException e)
				{
					IELogger.warn(String.format("Someone thinks that %s is an IE metal, let him think again..."));
				}

				if(ieMetal!=null)
				{
					if(IEBlocks.Metals.storage.containsKey(ieMetal))
						storage = IEBlocks.Metals.storage.get(ieMetal);
					if(IEBlocks.Metals.sheetmetal.containsKey(ieMetal))
						sheetmetal = IEBlocks.Metals.sheetmetal.get(ieMetal);

					if(IEItems.Metals.ingots.containsKey(ieMetal))
						ingot = IEItems.Metals.ingots.get(ieMetal);
					if(IEItems.Metals.nuggets.containsKey(ieMetal))
						nugget = IEItems.Metals.nuggets.get(ieMetal);
					if(IEItems.Metals.plates.containsKey(ieMetal))
						plate = IEItems.Metals.plates.get(ieMetal);
					if(IEItems.Metals.dusts.containsKey(ieMetal))
						dust = IEItems.Metals.dusts.get(ieMetal);
				}
			}

			if(storage==null)
			{
				storage = new IGStorageBlock(material);
				addSlabFor((IGBaseBlock)storage);
			}
			if(sheetmetal==null)
			{
				sheetmetal = new IGSheetmetalBlock(material);
				addSlabFor((IGBaseBlock)sheetmetal);
			}

			if(ingot==null&&material.hasSubtype(MaterialUseType.INGOT))
				ingot = new IGMaterialItem(material, MaterialUseType.INGOT);

			if(nugget==null&&material.hasSubtype(MaterialUseType.NUGGET))
				nugget = new IGMaterialItem(material, MaterialUseType.NUGGET);

			if(plate==null&&material.hasSubtype(MaterialUseType.PLATE))
				plate = new IGMaterialItem(material, MaterialUseType.PLATE);

			if(dust==null&&material.hasSubtype(MaterialUseType.DUST))
				dust = new IGMaterialItem(material, MaterialUseType.DUST);

			if(rod==null&&material.hasSubtype(MaterialUseType.ROD))
				rod = new IGMaterialItem(material, MaterialUseType.ROD);

			if(gear==null&&material.hasSubtype(MaterialUseType.GEAR))
				gear = new IGMaterialItem(material, MaterialUseType.GEAR);

			if(tiny_dust==null&&material.hasSubtype(MaterialUseType.TINY_DUST))
				tiny_dust = new IGMaterialItem(material, MaterialUseType.TINY_DUST);

			if(wire==null&&material.hasSubtype(MaterialUseType.WIRE))
				wire = new IGMaterialItem(material, MaterialUseType.WIRE);

		}

	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		//checkNonNullNames(registeredIGBlocks);
		for(Block block : registeredIGBlocks)
			if(block!=null)
				event.getRegistry().register(block);
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		//checkNonNullNames(registeredIGItems);
		for(Item item : registeredIGItems)
			if(item!=null)
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
