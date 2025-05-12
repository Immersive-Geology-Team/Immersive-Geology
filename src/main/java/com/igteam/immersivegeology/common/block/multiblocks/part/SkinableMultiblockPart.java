/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.part;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockPartBlock;
import com.igteam.immersivegeology.common.block.multiblocks.IGTemplateMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.skins.helpers.IIGMultiSkinHelper;
import com.igteam.immersivegeology.common.block.multiblocks.skins.helpers.IMultiSkinBlock;
import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.config.IGServerConfig.Machines.MachineConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property.Value;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Optional;

public abstract class SkinableMultiblockPart<S extends IMultiblockState, T extends Enum<T> & IIGMultiSkinHelper & StringRepresentable>
		extends MultiblockPartBlock<S> implements IMultiSkinBlock<T>
{
	private final EnumProperty<T> skinProperty;
	private final Class<T> skinClass;
	private final String textureDir;

	protected SkinableMultiblockPart(Properties props,
									 MultiblockRegistration<S> reg,
									 EnumProperty<T> skinProperty,
									 Class<T> skinClass,
									 String textureDir)
	{
		super(props, reg);
		this.skinProperty = skinProperty;
		this.skinClass    = skinClass;
		this.textureDir   = textureDir;
		this.registerDefaultState(
				this.defaultBlockState()
						.setValue(IEProperties.MIRRORED, false)
						.setValue(skinProperty, skinClass.getEnumConstants()[0])
		);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(IEProperties.MIRRORED);
	}

	@Override
	public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston)
	{
		String multiblock = skinClass.getEnumConstants()[0].multiblockName();
		MachineConfig config = IGServerConfig.MACHINES.machines.get(multiblock);
		if(config != null) {
			BlockState state = pState.setValue(skinProperty, skinClass.getEnumConstants()[config.default_skin_ordinal.get()]);
			pLevel.setBlock(pPos, state, 3);
		}
		super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
	}

	@Override
	public EnumProperty<T> getSkinProperty() {
		return skinProperty;
	}

	@Override
	public Class<T> getSkinClass() {
		return skinClass;
	}

	public String getTextureDir() {
		return textureDir;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Enum<T> & IIGMultiSkinHelper & StringRepresentable> boolean setSkin(
			IMultiblockLevel level,
			IGTemplateMultiblock template,
			Enum<?> skinValue
	) {
		Block block = template.getBlock();
		if (!(block instanceof IMultiSkinBlock<?> raw)) return false;
		IMultiSkinBlock<T> skinnable = (IMultiSkinBlock<T>) raw;

		EnumProperty<T> prop = skinnable.getSkinProperty();
		Level rawLevel = level.getRawLevel();

		for (StructureTemplate.StructureBlockInfo info : template.getStructure(level.getRawLevel())) {
			BlockPos pos = info.pos();
			BlockState current = level.getBlockState(pos);

			if (!current.is(block)) continue;

			BlockPos wpos = level.toAbsolute(pos);
			rawLevel.addParticle(
					ParticleTypes.POOF,
					wpos.getX()+0.5f, wpos.getY()+0.5f, wpos.getZ()+0.5f,
					0, 0.0625, 0);

			if(current.getValue(prop).equals(skinValue)) return false;
			Optional<Value<T>> value = prop.getAllValues().filter(p -> p.value().equals(skinValue)).findFirst();
			if(value.isEmpty()) continue;
			BlockState updated = current.setValue(prop, value.get().value());
			level.setBlock(pos, updated);
		}
		return true;
	}
}
