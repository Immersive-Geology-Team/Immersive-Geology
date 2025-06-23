/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.event;

import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class VeinScanTask {
	public final CommandSourceStack source;
	public final ServerLevel level;
	public final MaterialInterface<?> type;
	public final int radius;
	private final ChunkPos origin;
	private final List<ChunkPos> chunksToCheck;
	private int currentIndex = 0;

	public VeinScanTask(CommandSourceStack source, @NotNull ServerLevel level, MaterialInterface<?> type, int radius) {
		this.source = source;
		this.level = level;
		this.type = type;
		this.radius = radius;

		ServerPlayer player = source.getPlayer();
		assert player!=null;
		this.origin = player.chunkPosition();

		this.chunksToCheck = new ArrayList<>();

		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				chunksToCheck.add(new ChunkPos(origin.x + x, origin.z + z));
			}
		}
	}

	public boolean isComplete() {
		return currentIndex >= chunksToCheck.size();
	}

	public ChunkPos nextChunk() {
		return chunksToCheck.get(currentIndex++);
	}
}