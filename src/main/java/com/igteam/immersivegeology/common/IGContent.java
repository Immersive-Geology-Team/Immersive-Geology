package com.igteam.immersivegeology.common;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.materials.MaterialUseType.UseCategory;
import com.igteam.immersivegeology.common.blocks.*;
import com.igteam.immersivegeology.common.items.IGMaterialResourceItem;
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
		//Item, blocks here
		// cycle through item Types
		for(MaterialUseType materialItem : MaterialUseType.values())
		{
			//cycle through materials
			for(EnumMaterials m : EnumMaterials.values())
			{
				Material material = m.material;
				//check if that material is allowed to make this item type.
				if(material.hasSubtype(materialItem)) {
					//check if this type is an ITEM not a BLOCK type.
					if(materialItem.getCategory() != UseCategory.BLOCK && materialItem.getCategory() != UseCategory.MATERIAL_BLOCK) {
						registeredIGItems.add(new IGMaterialResourceItem(material, materialItem)); 
					} else if (materialItem.getCategory() == UseCategory.MATERIAL_BLOCK) {
						//registeredIGBlocks.add(new IGMaterialBlock(materialItem));
					}
				}
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
}
