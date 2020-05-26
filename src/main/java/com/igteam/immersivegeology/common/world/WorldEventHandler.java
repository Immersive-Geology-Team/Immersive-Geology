package com.igteam.immersivegeology.common.world;

import java.util.Collection;
import java.util.HashMap;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.blocks.EnumMaterials;
import com.igteam.immersivegeology.common.blocks.IGBaseBlock;
import com.igteam.immersivegeology.common.util.IGBlockGrabber;
import com.igteam.immersivegeology.common.world.biome.BiomeLayerData;
import com.igteam.immersivegeology.common.world.biome.WorldLayerData;
import com.igteam.immersivegeology.common.world.chunk.WorldChunkChecker;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public class WorldEventHandler {

	private boolean worldDone = false;
	private int seed;

	public static WorldLayerData data;

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onWorldLoad(WorldEvent.Load event) {
		if (EffectiveSide.get() == LogicalSide.SERVER) {
			if (0 == event.getWorld().getDimension().getType().getId()) {
				if (!worldDone) {
					worldDone = true;
					data = new WorldLayerData(); // TODO for some reason blocks passed through this end up null
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onChunkPopulate(ChunkEvent.Load event) {

		if (!worldDone || event.getWorld() == null || event.getChunk() == null)
			return;

		if (EffectiveSide.get() == LogicalSide.SERVER) {

			WorldType type = event.getWorld().getWorldInfo().getGenerator();

			if (!WorldChunkChecker.hasAlreadyBeenIGfied(event.getChunk())) {
				IChunk chunk = event.getChunk();

				this.replaceStone(chunk);

				((Chunk) chunk).setModified(true);
				((Chunk) chunk).markDirty();
				WorldChunkChecker.setDone(event.getChunk());
			}
		}
	}

	private void replaceStone(IChunk chunk) {

		int xPos = chunk.getPos().x * 16;
		int zPos = chunk.getPos().z * 16;

		for (ChunkSection storage : chunk.getSections()) {
			if (storage != null && !storage.isEmpty()) {
				int yPos = storage.getYLocation();

				Biome biome = chunk.getBiome(new BlockPos(8, 8, 8));
				IGBaseBlock replaceBlock = IGBlockGrabber.grabBlock(MaterialUseType.ROCK,
						EnumMaterials.Rock_Limestone.material);

				for (BiomeLayerData b : data.worldLayerData) {
					if (b.getLbiome() == biome) {
						int lc = b.getLayerCount();
						int totalHeight = 256;
						for (int l = lc; l > 0; l--) {
							for (int x = 0; x < 16; x++) {
								for (int z = 0; z < 16; z++) {
									for (int y = 0; y < 16; ++y) {

										float trueY = (yPos + y);

										if (trueY < (totalHeight * l) / lc) { //while it does 'work' it just replaces all blocks, checking for 'stone' doesn't stop it...
											replaceBlock = b.getLayerBlock(l);

											Block oldBlock = storage.getBlockState(x, y, z).getBlock();

											// setting the block in here isn't super efficient, but it works.
											int nh = (int) replaceBlock.getDefaultHardness();

											nh = (int) (12 - (Math.pow(Math.E, 2.6) * Math.log(trueY / 135)));


											if (oldBlock == Blocks.STONE) {
												storage.setBlockState(x, y, z,
														replaceBlock.getDefaultState()
																.with(IGBaseBlock.NATURAL, Boolean.valueOf(true))
																.with(IGBaseBlock.HARDNESS,
																		Integer.valueOf(
																				(int) Math.min(256, Math.max(0, nh)))),
														true);
											}
										}
									}
								}
							}

						}
					}
				}
			}
		}
	}

}
