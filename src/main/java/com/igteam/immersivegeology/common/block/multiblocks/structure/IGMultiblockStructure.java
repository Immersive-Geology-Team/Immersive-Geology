package com.igteam.immersivegeology.common.block.multiblocks.structure;

import blusunrize.immersiveengineering.api.MultiblockHandler.IMultiblock;
import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class IGMultiblockStructure implements IMultiblock
{
	private final String name;
	private final ResourceLocation templateLocation;
	private final BlockPos masterOffset;
	private final BlockPos triggerOffset;

	private IGStructureTemplate template;
	private ItemStack[][][] structureManual;
	private IngredientStack[] totalMaterials;
	private IBlockState triggerState;

	protected IGMultiblockStructure(String name, ResourceLocation templateLocation,
									BlockPos masterOffset, BlockPos triggerOffset)
	{
		this.name = name;
		this.templateLocation = templateLocation;
		this.masterOffset = masterOffset;
		this.triggerOffset = triggerOffset;
	}

	public abstract IBlockState getPartState();

	protected IGStructureTemplate getTemplate()
	{
		if(template==null) template = IGStructureTemplate.load(templateLocation);
		return template;
	}

	public BlockPos getMasterOffset()
	{
		return masterOffset;
	}

	public BlockPos getTriggerOffset()
	{
		return triggerOffset;
	}

	public int[] getStructureDimensions()
	{
		IGStructureTemplate loaded = getTemplate();
		if(loaded==null) return new int[]{1, 1, 1};
		return new int[]{loaded.sizeY(), loaded.sizeZ(), loaded.sizeX()};
	}

	@Override
	public String getUniqueName()
	{
		return IGLib.MODID+":"+name;
	}

	@Override
	public boolean isBlockTrigger(IBlockState state)
	{
		IBlockState expected = getTriggerState();
		if(expected==null) return false;

		if(state.getBlock()!=expected.getBlock()) return false;
		return state.getBlock().getMetaFromState(state)==expected.getBlock().getMetaFromState(expected);
	}

	public IBlockState getTriggerState()
	{
		if(triggerState!=null) return triggerState;

		IGStructureTemplate loaded = getTemplate();
		if(loaded==null) return null;

		String triggerId = loaded.getBlockId(triggerOffset.getX(), triggerOffset.getY(), triggerOffset.getZ());
		triggerState = loaded.getBlockState(triggerOffset.getX(), triggerOffset.getY(), triggerOffset.getZ());

		if(triggerState==null)
			IGLib.IG_LOGGER.error("Multiblock {} has no resolvable trigger block at {} (template says {})",
					name, triggerOffset, triggerId);

		return triggerState;
	}

	@Override
	public ItemStack[][][] getStructureManual()
	{
		if(structureManual!=null) return structureManual;

		IGStructureTemplate loaded = getTemplate();
		if(loaded==null) return structureManual = new ItemStack[0][0][0];

		structureManual = new ItemStack[loaded.sizeY()][loaded.sizeZ()][loaded.sizeX()];
		for(int y = 0; y < loaded.sizeY(); y++)
			for(int z = 0; z < loaded.sizeZ(); z++)
				for(int x = 0; x < loaded.sizeX(); x++)
					structureManual[y][z][x] = IGBlockMapping.toStack(loaded.getBlockState(x, y, z));
		return structureManual;
	}

	@Override
	public IngredientStack[] getTotalMaterials()
	{
		if(totalMaterials!=null) return totalMaterials;

		Map<String, IngredientStack> totals = new LinkedHashMap<>();
		for(ItemStack[][] layer : getStructureManual())
			for(ItemStack[] row : layer)
				for(ItemStack stack : row)
				{
					if(stack==null||stack.isEmpty()) continue;
					String key = stack.getItem().getRegistryName()+"@"+stack.getMetadata();
					IngredientStack existing = totals.get(key);
					if(existing==null) totals.put(key, new IngredientStack(stack.copy()));
					else existing.inputSize++;
				}

		List<IngredientStack> list = new ArrayList<>(totals.values());
		return totalMaterials = list.toArray(new IngredientStack[0]);
	}

	@Override
	public boolean createStructure(World world, BlockPos pos, EnumFacing side, EntityPlayer player)
	{
		return IGStructureFormer.tryForm(this, world, pos, player);
	}

	@Override
	public boolean overwriteBlockRender(ItemStack stack, int iterator)
	{
		return false;
	}

	@Override
	public float getManualScale()
	{
		return 12;
	}

	@Override
	public boolean canRenderFormedStructure()
	{
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderFormedStructure()
	{
	}
}
