package com.igteam.immersivegeology.common.block.multiblocks.recipe;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.registration.IGContent;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class IGBloomeryRecipes
{
	private static final int BASE_COAL_TIME = 500;
	private static final int BASE_LIGNITE_TIME = 150;
	private static final int BASE_BITUMINOUS_TIME = 500;
	private static final int BASE_ANTHRACITE_TIME = 600;
	private static final int BASE_CHARCOAL_TIME = 1200;
	private static final int BASE_COAL_COKE_TIME = 1400;
	private static final int NORMAL_QUALITY_MULTIPLIER = 2;

	private IGBloomeryRecipes()
	{
	}

	public static void register()
	{
		int recipes = 0;
		recipes += addSmelt(MetalEnum.Copper, 2, 200);
		recipes += addSmelt(MetalEnum.Gold, 2, 200);
		recipes += addSmelt(MetalEnum.Silver, 2, 200);
		recipes += addSmelt(MetalEnum.Lead, 2, 200);
		recipes += addSmelt(MineralEnum.Hematite, 4, 1100);
		recipes += addSmelt(MineralEnum.Magnetite, 4, 1200);
		recipes += addSmelt(MineralEnum.Cuprite, 2, 400);
		recipes += addSmelt(MineralEnum.Cassiterite, 2, 400);
		recipes += addSmelt(MineralEnum.Sphalerite, 1, 400);

		int fuels = 0;
		fuels += addVanillaFuel("charcoal", BASE_CHARCOAL_TIME);
		fuels += addVanillaFuel("coal", BASE_COAL_TIME);
		fuels += addOreFuel("fuelCoke", BASE_COAL_COKE_TIME);
		fuels += addMineralFuel(MineralEnum.Lignite, BASE_LIGNITE_TIME);
		fuels += addMineralFuel(MineralEnum.Bituminous, BASE_BITUMINOUS_TIME);
		fuels += addMineralFuel(MineralEnum.Anthracite, BASE_ANTHRACITE_TIME);

		IGLib.IG_LOGGER.info("- Registered {} bloomery recipes and {} bloomery fuels", recipes, fuels);
	}

	private static int addSmelt(MaterialInterface<?> material, int inputAmount, int time)
	{
		String inputKey = ItemCategoryFlags.CRUSHED_ORE.getRegistryKey(material);
		Item input = IGContent.getItem(inputKey);
		if(input==null)
		{
			IGLib.IG_LOGGER.warn("- bloomery skip: no crushed ore {}", inputKey);
			return 0;
		}

		MaterialInterface<?> product;
		try
		{
			product = material.instance().getPrimaryProduct();
		}
		catch(Exception e)
		{
			product = null;
		}
		if(product==null) product = material;

		ItemStack result = ingotFor(product);
		if(result.isEmpty()&&product!=material) result = ingotFor(material);
		if(result.isEmpty())
		{
			IGLib.IG_LOGGER.warn("- bloomery skip: no ingot for {} (primary product of {})",
					product.getName(), material.getName());
			return 0;
		}

		BloomeryRecipe.addRecipe(result, new ItemStack(input, inputAmount), time);
		return 1;
	}

	private static ItemStack ingotFor(MaterialInterface<?> material)
	{
		String oreName = ItemCategoryFlags.INGOT.getOreDictName(material.instance());
		if(!oreName.isEmpty())
		{
			java.util.List<ItemStack> ores = net.minecraftforge.oredict.OreDictionary.getOres(oreName);
			if(!ores.isEmpty())
			{
				ItemStack template = ores.get(0).copy();
				template.setCount(1);
				return template;
			}
		}

		Item item = IGContent.getItem(ItemCategoryFlags.INGOT.getRegistryKey(material));
		return item==null?ItemStack.EMPTY: new ItemStack(item, 1);
	}

	private static int addVanillaFuel(String name, int burnTime)
	{
		Item item = Item.getByNameOrId("minecraft:"+name);
		if(item==null) return 0;
		BloomeryFuel.addFuel(new ItemStack(item, 1, name.equals("charcoal")?1: 0), burnTime);
		return 1;
	}

	private static int addOreFuel(String oreName, int burnTime)
	{
		if(net.minecraftforge.oredict.OreDictionary.getOres(oreName).isEmpty()) return 0;
		BloomeryFuel.addFuel(oreName, burnTime);
		return 1;
	}

	private static int addMineralFuel(MaterialInterface<?> mineral, int baseTime)
	{
		int added = 0;
		Item ore = IGContent.getItem(ItemCategoryFlags.NORMAL_ORE.getRegistryKey(mineral));
		if(ore!=null)
		{
			BloomeryFuel.addFuel(new ItemStack(ore), baseTime*NORMAL_QUALITY_MULTIPLIER);
			added++;
		}
		Block block = IGContent.getBlock(BlockCategoryFlags.STORAGE_BLOCK.getRegistryKey(mineral));
		if(block!=null)
		{
			BloomeryFuel.addFuel(new ItemStack(block), baseTime*NORMAL_QUALITY_MULTIPLIER*10);
			added++;
		}
		return added;
	}
}
