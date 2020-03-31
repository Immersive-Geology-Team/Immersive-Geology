package com.igteam.immersivegeology.common;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.materials.MaterialUseType.UseCategory;
import com.igteam.immersivegeology.common.blocks.*;
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

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

import static com.igteam.immersivegeology.ImmersiveGeology.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IGContent
{
	public static List<Block> registeredIGBlocks = new ArrayList<>();
	public static List<Item> registeredIGItems = new ArrayList<>();
	public static List<Class<? extends TileEntity>> registeredIGTiles = new ArrayList<>();
	public static List<Fluid> registeredIGFluids = new ArrayList<>();

	public static HashMap<MaterialUseType, IGMaterialItem> materialSubItemCache = new HashMap<>();
	public static HashMap<MaterialUseType, IGBlockMaterialItem> materialSubBlockItemCache = new HashMap<>();
	public static HashMap<MaterialUseType, IGBaseBlock> materialSubBlockCache = new HashMap<>();

	public static void modConstruction()
	{

		Block storage = null;
		Block sheetmetal = null;

		for(MaterialUseType m : MaterialUseType.values())
		{
			if(m.getCategory()==UseCategory.RESOURCE_ITEM)
				addItemForType(m, m.createItem());
			//TODO: adding blocks/tools to the cache
		}
		
		for(MaterialUseType m : MaterialUseType.values())
		{
			if(m.getCategory()==UseCategory.BLOCK || m.getCategory()==UseCategory.MATERIAL_BLOCK) {
				addBlockForType(m, m.createMaterialBlock());
			}
			//TODO: adding blocks/tools to the cache
		}
		
		for(EnumMaterials m : EnumMaterials.values())
		{
			Material material = m.material;

			//Item, blocks here
			for(Entry<MaterialUseType, IGMaterialItem> materialItem : materialSubItemCache.entrySet())
			{
				if(material.hasSubtype(materialItem.getKey()))
					materialItem.getValue().addAllowedMaterial(material);
			}
			
			for(Entry<MaterialUseType, IGBlockMaterialItem> materialItem : materialSubBlockItemCache.entrySet())
			{
				if(material.hasSubtype(materialItem.getKey()))
					materialItem.getValue().addAllowedMaterial(material);
			}
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
	public static void registerBlockItems(RegistryEvent.Register<Item> event)
	{
		//checkNonNullNames(registeredIGItems);
		for(Block b : registeredIGBlocks) {
			if(b!=null) {
				if (b instanceof IIGBlock) {
				event.getRegistry().register(((IIGBlock) b).getItemBlock());
				}
			}
		}
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
		//Moved, because of IE items not being registered when constructing our mod
		//Removed, because of favoring our own material system
		/*for(EnumMaterials m : EnumMaterials.values())
		{
			Material material = m.material;

			//If IE already contains the metal
			if(m.material.getModID().equals(ImmersiveEngineering.MODID))
			{
				blusunrize.immersiveengineering.common.blocks.EnumMetals ieMetal = null;
				try
				{
					ieMetal = blusunrize.immersiveengineering.common.blocks.EnumMetals.valueOf(material.getName().toUpperCase(Locale.ENGLISH));
				} catch(IllegalArgumentException e)
				{
					IELogger.warn(String.format("Someone thinks that %s is an IE metal, let him think again...", m));
				}

				if(ieMetal!=null)
				{
					IGMaterialItem ingot = getItemForType(MaterialUseType.INGOT);
					IGMaterialItem plate = getItemForType(MaterialUseType.PLATE);
					IGMaterialItem dust = getItemForType(MaterialUseType.DUST);
					IGMaterialItem nugget = getItemForType(MaterialUseType.NUGGET);

					//In case someone else breaks our registry
					//Which won't happen, but why not have this

					if(ingot!=null)
						ingot.addReplacementItem(material, Metals.ingots.get(ieMetal));
					if(plate!=null)
						plate.addReplacementItem(material, Metals.plates.get(ieMetal));
					if(dust!=null)
						dust.addReplacementItem(material, Metals.dusts.get(ieMetal));
					if(nugget!=null)
						nugget.addReplacementItem(material, Metals.nuggets.get(ieMetal));
				}
			}
		}*/

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


	private static void addBlockForType(MaterialUseType m, IGMaterialBlock b) {
		if(m.getCategory() == UseCategory.MATERIAL_BLOCK) {
			materialSubBlockItemCache.putIfAbsent(m, b.getItemBlockMaterial());
		}
		materialSubBlockCache.putIfAbsent(m, b);
	}
	
	public static void addItemForType(MaterialUseType type, IGMaterialItem item)
	{
		materialSubItemCache.putIfAbsent(type, item);
	}
	
	@Nullable
	public static IGMaterialItem getItemForType(MaterialUseType type)
	{
		return materialSubItemCache.get(type);
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
