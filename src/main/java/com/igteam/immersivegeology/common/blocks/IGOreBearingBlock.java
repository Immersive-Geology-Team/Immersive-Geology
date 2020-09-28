package com.igteam.immersivegeology.common.blocks;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.materials.ToolUseType;
import com.igteam.immersivegeology.api.toolsystem.Tooltypes;
import com.igteam.immersivegeology.api.util.IGMathHelper;
import com.igteam.immersivegeology.api.util.IGRegistryGrabber;
import com.igteam.immersivegeology.common.blocks.property.IGProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import java.util.ArrayList;
import java.util.List;

public class IGOreBearingBlock extends IGMaterialBlock implements IIGOreBlock
{
	protected int defaultRichness = 1;
	public static final IntegerProperty ORE_RICHNESS = IGProperties.ORE_RICHNESS;

	public IGOreBearingBlock(Material material, MaterialUseType type, Material oreMat)
	{
		this(material, type, "", oreMat);
	}

	public IGOreBearingBlock(Material material, MaterialUseType type, String sub, Material oreMat)
	{
		super(sub, type, material, oreMat);

		this.setBlockLayer(BlockRenderLayer.CUTOUT);
		//set state variables
		this.setDefaultState(this.stateContainer.getBaseState()
				.with(NATURAL, Boolean.FALSE)
				.with(ORE_RICHNESS, defaultRichness));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		super.fillStateContainer(builder);
		builder.add(ORE_RICHNESS);
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
	{
		ItemStack tool = player.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
		if(!player.isCreative()&&!player.isSpectator()&&this.canHarvestBlock(state, worldIn, pos, player))
		{
			if(subtype==MaterialUseType.ORE_BEARING&&tool.getToolTypes().contains(ToolType.PICKAXE))
			{
				boolean silk = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0;
				if(!silk)
				{
					List<ItemStack> blockDrops = new ArrayList<>();
					if(tool.getToolTypes().contains(Tooltypes.PICKAXE_TOOL))
					{
						int level = tool.getHarvestLevel(ToolType.PICKAXE, player, state);
						int effectiveLevel = Math.max(level-getMaterial().getBlockHarvestLevel(), 0);
						int richness = state.get(IGProperties.ORE_RICHNESS)+1;
						int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, tool);
						int stoneAmount = Math.max(Math.min(8, 1+effectiveLevel), Math.min(8, Math.round((float)(IGMathHelper.randInt(2+fortune, effectiveLevel))*(1+player.getLuck()))));
						int oreMax = richness+IGMathHelper.randInt(fortune);
						int oreAmount = stoneAmount >= oreMax?oreMax: stoneAmount;
						stoneAmount -= oreAmount;

						blockDrops.add(new ItemStack(IGRegistryGrabber.getIGItem(MaterialUseType.ORE_CHUNK, getMaterial(), getOreMaterial()), oreAmount));

						blockDrops.add(new ItemStack(IGRegistryGrabber.getIGItem(MaterialUseType.CHUNK, getMaterial()), stoneAmount));
					}
					else if(tool.getToolTypes().contains(Tooltypes.HAMMER_TOOL))
					{
						int level = tool.getHarvestLevel(Tooltypes.HAMMER_TOOL, player, state);
						int effectiveLevel = Math.max(level-getMaterial().getBlockHarvestLevel(), 0);
						int richness = state.get(IGProperties.ORE_RICHNESS)+1;
						int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, tool);
						int stoneAmount = Math.max(Math.min(8, 1+effectiveLevel), Math.min(8, Math.round((float)(IGMathHelper.randInt(2+fortune, effectiveLevel))*(1+player.getLuck()))));
						int oreMax = richness+IGMathHelper.randInt(fortune);
						int oreAmount = stoneAmount >= oreMax?oreMax: stoneAmount;
						stoneAmount -= oreAmount;

						blockDrops.add(new ItemStack(IGRegistryGrabber.getIGItem(MaterialUseType.ORE_BIT, getMaterial(), getOreMaterial()), oreAmount));

						blockDrops.add(new ItemStack(IGRegistryGrabber.getIGItem(MaterialUseType.ROCK_BIT, getMaterial()), stoneAmount));
					}


					for(ItemStack item : blockDrops)
					{
						worldIn.addEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), item));
					}
				}
				else
				{
					//TODO find out how to store state in item (this.asItem())?
					worldIn.addEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(this.itemBlock)));
				}
			}
			else
			{
				worldIn.addEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(this.itemBlock)));
			}
		}
	}

	public Material getOreMaterial()
	{
		return materials[1];
	}

	@Override
	public boolean hasMultipartModel()
	{
		return true;
	}

}
