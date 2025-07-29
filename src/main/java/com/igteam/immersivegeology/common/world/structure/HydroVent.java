/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.world.structure;

import com.igteam.immersivegeology.common.world.IGStructureTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.Optional;
import java.util.Random;

public class HydroVent extends Structure
{
	public static final Codec<HydroVent> CODEC = RecordCodecBuilder.<HydroVent>mapCodec(instance ->
			instance.group(
					HydroVent.settingsCodec(instance),
					StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
					Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
					Codec.intRange(-61, 80).fieldOf("y_min").forGetter(structure -> structure.ymin),
					Codec.intRange(-31, 110).fieldOf("y_max").forGetter(structure -> structure.ymax),
					Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter)
			).apply(instance, HydroVent::new)
	).codec();

	private final Holder<StructureTemplatePool> startPool;
	private final int size;
	private final int ymin;
	private final int ymax;
	private final int maxDistanceFromCenter;

	public HydroVent(StructureSettings settings,
					 Holder<StructureTemplatePool> startPool,
					 int size,
					 int ymin,
					 int ymax,
					 int maxDistanceFromCenter) {
		super(settings);
		this.startPool = startPool;
		this.size = size;
		this.ymin = ymin;
		this.ymax = ymax;
		this.maxDistanceFromCenter = maxDistanceFromCenter;
	}

	@Override
	public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
		ChunkPos chunkPos = context.chunkPos();
		BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ());

		int height = context.chunkGenerator().getFirstOccupiedHeight(
				blockPos.getX(),
				blockPos.getZ(),
				Types.OCEAN_FLOOR_WG,
				context.heightAccessor(),
				context.randomState()
		);

		BlockPos structurePos = new BlockPos(blockPos.getX(), height, blockPos.getZ());

		// Use JigsawPlacement with minimal parameters for simple structures
		return JigsawPlacement.addPieces(
				context,
				this.startPool,
				Optional.empty(), // No specific jigsaw name needed
				this.size,
				structurePos,
				false, // useExpansionHack
				Optional.empty(), // No heightmap projection needed
				this.maxDistanceFromCenter
		);
	}

	@Override
	public StructureType<?> type() {
		return IGStructureTypes.SO2_VENT_TYPE.get();
	}
}
