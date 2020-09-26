package com.igteam.immersivegeology.common.blocks.plant;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IColouredBlock;
import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.materials.MaterialUtils;
import com.igteam.immersivegeology.api.util.IGMathHelper;
import com.igteam.immersivegeology.api.util.IGRegistryGrabber;
import com.igteam.immersivegeology.common.IGContent;
import com.igteam.immersivegeology.common.blocks.IGBaseBlock;
import com.igteam.immersivegeology.common.blocks.IGBlockMaterialItem;
import com.igteam.immersivegeology.common.blocks.IIGOreBlock;
import com.igteam.immersivegeology.common.blocks.property.IGProperties;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pabilo8 on 26-03-2020.
 */
public class IGRockMossBlock extends IGBaseBlock implements IColouredBlock
{
	public Material[] materials;
	public MaterialUseType subtype;

	public IGRockMossBlock(Material... materials)
	{
		this("", MaterialUseType.MOSS_ROCK, materials);
	}

	public IGRockMossBlock(String sub, MaterialUseType subtype, Material... materials)
	{
		super(MaterialUtils.generateMaterialName("block", subtype, materials),
				Properties.create((subtype.getMaterial()==null?net.minecraft.block.material.Material.ROCK: subtype.getMaterial())), IGBlockMaterialItem.class, subtype.getSubGroup());

		this.materials = materials;
		this.subtype = subtype;
		this.name = MaterialUtils.generateMaterialName("block", subtype, materials);
		this.setBlockLayer(BlockRenderLayer.CUTOUT);
		if(itemBlock!=null)
		{
			try
			{
				this.itemSubGroup = subtype.getSubGroup();
				this.itemBlock = new IGBlockMaterialItem(this, new Item.Properties().group(ImmersiveGeology.IG_ITEM_GROUP), subtype.getSubGroup());

				this.itemBlock.setRegistryName(new ResourceLocation(ImmersiveGeology.MODID, name));
				IGContent.addItemBlockForBlock(name,this.itemBlock);
			} catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}

		if(itemBlock instanceof IGBlockMaterialItem)
		{
			((IGBlockMaterialItem)itemBlock).materials = this.materials;
			((IGBlockMaterialItem)itemBlock).subtype = this.subtype;
		}
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

	public MaterialUseType getUseType() {
		return this.subtype;
	}

	@Override
	public boolean canSustainPlant(BlockState p_canSustainPlant_1_, IBlockReader p_canSustainPlant_2_, BlockPos p_canSustainPlant_3_, Direction p_canSustainPlant_4_, IPlantable p_canSustainPlant_5_) {
		return true;
	}

	// TODO: 15.07.2020 move rocks to their own class, like metals have
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
	{
		ItemStack tool = player.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
		if(!player.isCreative()&&!player.isSpectator()&&this.canHarvestBlock(state, worldIn, pos, player))
		{
			if(subtype.equals(MaterialUseType.ROCK)&&!tool.isEmpty())
			{
				boolean silk = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0;
				if(state.get(IGProperties.NATURAL)&&!silk)
				{
					List<ItemStack> blockDrops = new ArrayList<>();
					int level = tool.getHarvestLevel(ToolType.PICKAXE, player, state);
					int effectiveLevel = Math.max(level-this.materials[0].getBlockHarvestLevel(), 0);
					int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, tool);
					int dropAmount = Math.max(Math.min(8, 1+effectiveLevel), Math.min(8, Math.round((float)(IGMathHelper.randInt(2+effectiveLevel+fortune, effectiveLevel))*(1+player.getLuck()))));
					blockDrops.add(new ItemStack(IGRegistryGrabber.getIGItem(MaterialUseType.CHUNK, this.materials[0]), dropAmount));
					for(ItemStack item : blockDrops)
					{
						worldIn.addEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), item));
					}
				}
				else
				{
					if(!(this instanceof IIGOreBlock)) {
						worldIn.addEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(this.itemBlock)));
					}
				}
			}
			else
			{
				worldIn.addEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(this.itemBlock)));
			}
		}
		super.onBlockHarvested(worldIn, pos, state, player);
	}

	@Nullable
	@Override
	public ToolType getHarvestTool(BlockState state)
	{
		return ToolType.PICKAXE;
	}

	@Override
	public int getHarvestLevel(BlockState state)
	{
		return materials[0].getBlockHarvestLevel();
	}

	@Override
	public boolean hasCustomBlockColours()
	{
		return true;
	}

	@Override
	public int getRenderColour(BlockState blockState, @Nullable IBlockReader iBlockReader, @Nullable BlockPos blockPos, int pass)
	{
		return materials[MathHelper.clamp(pass,0,materials.length-1)].getColor(0);
	}

	/**
	 * @return the base material
	 */
	public Material getMaterial()
	{
		return materials[0];
	}

	public boolean hasMultipartModel()
	{
		return false;
	}
}
