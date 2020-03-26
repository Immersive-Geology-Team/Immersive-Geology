package com.igteam.immersivegeology.common;

import com.igteam.immersivegeology.api.materials.MaterialRegistry;
import com.igteam.immersivegeology.common.blocks.*;
import com.igteam.immersivegeology.common.event.MaterialRegistryEvent;
import com.igteam.immersivegeology.common.util.IGLogger;
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

	}

	@SubscribeEvent
	public void registerMaterials(MaterialRegistryEvent event)
	{
		IGLogger.info("registering materials");
		for(EnumMetals metal : EnumMetals.values())
		{
			MaterialRegistry.addMaterial(metal.metal);
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
