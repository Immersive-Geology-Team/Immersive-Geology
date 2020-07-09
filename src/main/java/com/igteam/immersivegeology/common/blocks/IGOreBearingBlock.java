package com.igteam.immersivegeology.common.blocks;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialStoneBase;
import com.igteam.immersivegeology.common.blocks.helpers.IOverlayColor;
import com.igteam.immersivegeology.common.blocks.property.IGProperties;
import com.igteam.immersivegeology.common.materials.EnumOreBearingMaterials;
import com.igteam.immersivegeology.common.util.BlockstateGenerator;
import com.igteam.immersivegeology.common.util.ItemJsonGenerator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;

public class IGOreBearingBlock extends IGBaseBlock implements IOverlayColor, IBlockColor {

	protected Material material;
	protected MaterialUseType type;
	protected int defaultRichness = 1;
	public static final IntegerProperty ORE_RICHNESS = IGProperties.ORE_RICHNESS;
	
	public static final IntegerProperty ORE_TYPE = IGProperties.ORE_TYPE;
	
	
	public IGOreBearingBlock(Material material, MaterialUseType type) {
		this(material, type, "");
	}

	public IGOreBearingBlock(Material material, MaterialUseType type, String sub) {
		super(sub + "block_" + type.getName() + "_" + material.getName(),
				Properties.create(
						(type.getMaterial() == null ? net.minecraft.block.material.Material.ROCK : type.getMaterial())),
				IGBlockMaterialItem.class, type.getSubGroup());

		
		this.setBlockLayer(BlockRenderLayer.CUTOUT_MIPPED);
		this.material = material;
		this.type = type;
		if(itemBlock instanceof IGBlockMaterialItem)
		{
			((IGBlockMaterialItem)itemBlock).material=this.material;
			((IGBlockMaterialItem)itemBlock).subtype=this.type;
		}
		
		if (type.equals(MaterialUseType.ORE_BEARING)) {
			if (material instanceof MaterialStoneBase) {
				MaterialStoneBase rockMat = (MaterialStoneBase) material;
				BlockstateGenerator.generateOreBearingBlock(material, type, rockMat.getStoneType());
				ItemJsonGenerator.generateOreBearingBlockItem(material, type, rockMat.getStoneType());
			}
		}
		
		this.setDefaultState(this.stateContainer.getBaseState()
		.with(NATURAL, Boolean.valueOf(false))
		.with(ORE_RICHNESS, Integer.valueOf((int)(defaultRichness)))
		.with(HARDNESS, Integer.valueOf((int)(defaultHardness)))
		.with(ORE_TYPE, EnumOreBearingMaterials.Casserite.ordinal()));
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		// TODO Auto-generated method stub
		return BlockRenderType.MODEL;
	}
	
	@Override
	public BlockRenderLayer getRenderLayer() {
		// TODO This is currently mostly a marker for culling, the actual layer is
		// determined by canRenderInLayer
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer) {
		// TODO Auto-generated method stub
		
		return super.canRenderInLayer(state, layer);
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(ORE_RICHNESS);
		builder.add(ORE_TYPE);
	} 

	@Override
	public int getOverlayColor(BlockState state) {
		// TODO Auto-generated method stub
		int mat = state.get(IGProperties.ORE_TYPE);
		return EnumOreBearingMaterials.values()[mat].getColor();
	}

	@Override
	public String getOverlayName() {
		return "normal";
	}

	@Override
	public int getColor(BlockState p_getColor_1_, IEnviromentBlockReader p_getColor_2_, BlockPos p_getColor_3_,
			int index) {
			return material.getColor(0);
	}
}
