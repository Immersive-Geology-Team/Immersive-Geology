package com.igteam.immersivegeology.common.block.ore;

import com.igteam.immersivegeology.common.block.helper.MineralWeathering;
import com.igteam.immersivegeology.common.block.helper.OreBlockMeta;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.core.material.helper.material.IStoneType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class IGOreBlock extends Block
{
	public static final PropertyEnum<OreRichness> RICHNESS = PropertyEnum.create("richness", OreRichness.class);
	public static final PropertyEnum<MineralWeathering> WEATHERING = PropertyEnum.create("weathering", MineralWeathering.class);

	private static final float WEATHER_CHANCE = 0.2F;

	private final MaterialInterface<?> oreMaterial;
	private final IStoneType stoneType;
	private final boolean weathers;

	public IGOreBlock(MaterialInterface<?> oreMaterial, IStoneType stoneType)
	{
		super(stoneType.instance().getHostState().getMaterial());
		this.oreMaterial = oreMaterial;
		this.stoneType = stoneType;
		this.weathers = oreMaterial.canTarnish();

		setHardness(3.0F);
		setResistance(5.0F);
		setDefaultState(blockState.getBaseState()
				.withProperty(RICHNESS, OreRichness.NORMAL)
				.withProperty(WEATHERING, MineralWeathering.PRISTINE));
		setTickRandomly(weathers);
	}

	public MaterialInterface<?> getOreMaterial()
	{
		return oreMaterial;
	}

	public IStoneType getStoneType()
	{
		return stoneType;
	}

	public boolean weathers()
	{
		return weathers;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[]{RICHNESS, WEATHERING});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState()
				.withProperty(RICHNESS, OreBlockMeta.richness(meta))
				.withProperty(WEATHERING, OreBlockMeta.weathering(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return OreBlockMeta.pack(state.getValue(RICHNESS), state.getValue(WEATHERING));
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return getMetaFromState(state);
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		for(OreRichness richness : OreRichness.values())
		{
			items.add(new ItemStack(this, 1, OreBlockMeta.pack(richness, MineralWeathering.PRISTINE)));
			if(weathers)
				for(MineralWeathering weathering : MineralWeathering.values())
					if(weathering!=MineralWeathering.PRISTINE)
						items.add(new ItemStack(this, 1, OreBlockMeta.pack(richness, weathering)));
		}
	}

	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		if(world.isRemote||!weathers) return;

		MineralWeathering current = state.getValue(WEATHERING);
		MineralWeathering next = current.next();
		if(next==current) return;
		if(!isExposed(world, pos)) return;
		if(rand.nextFloat() >= WEATHER_CHANCE) return;

		world.setBlockState(pos, state.withProperty(WEATHERING, next), 2);
	}

	private boolean isExposed(World world, BlockPos pos)
	{
		for(net.minecraft.util.EnumFacing facing : net.minecraft.util.EnumFacing.values())
			if(world.isAirBlock(pos.offset(facing))) return true;
		return false;
	}
}
