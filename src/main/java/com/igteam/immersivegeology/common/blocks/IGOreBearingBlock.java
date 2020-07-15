package com.igteam.immersivegeology.common.blocks;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.util.IGMathHelper;
import com.igteam.immersivegeology.common.IGRegistryGrabber;
import com.igteam.immersivegeology.common.blocks.property.IGProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.IBlockColor;
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
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import java.util.ArrayList;
import java.util.List;

public class IGOreBearingBlock extends IGMaterialBlock implements IBlockColor
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

		this.setBlockLayer(BlockRenderLayer.CUTOUT_MIPPED);
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
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		super.fillStateContainer(builder);
		builder.add(ORE_RICHNESS);
	}

	@Override
	public int getColor(BlockState p_getColor_1_, IEnviromentBlockReader p_getColor_2_, BlockPos p_getColor_3_, int index)
	{
		return index==0?getOreMaterial().getColor(0): getOreMaterial().getColor(0);
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
	{
		ItemStack tool = player.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
		if(!player.isCreative()&&!player.isSpectator()&&this.canHarvestBlock(state, worldIn, pos, player))
		{
			if(type==MaterialUseType.ORE_BEARING_ROCK&&!tool.isEmpty())
			{
				boolean silk = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0;
				if(!silk)
				{
					List<ItemStack> blockDrops = new ArrayList<>();
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

					for(ItemStack item : blockDrops)
					{
						worldIn.addEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), item));
					}

				}
				else
				{
					worldIn.addEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(this.itemBlock)));
				}
			}
			else
			{
				worldIn.addEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(this.itemBlock)));
			}
		}

		super.onBlockHarvested(worldIn, pos, state, player);
	}

	public Material getOreMaterial()
	{
		return materials[0];
	}

}
