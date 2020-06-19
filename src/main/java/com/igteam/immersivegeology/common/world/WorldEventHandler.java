package com.igteam.immersivegeology.common.world;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.IGContent;
import com.igteam.immersivegeology.common.blocks.IGBaseBlock;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.util.IGBlockGrabber;
import com.igteam.immersivegeology.common.world.chunk.WorldChunkChecker;
import com.igteam.immersivegeology.common.world.layer.BiomeLayerData;
import com.igteam.immersivegeology.common.world.layer.WorldLayerData;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
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
	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onRenderFog(EntityViewRenderEvent.FogDensity event) {
		if (event.getInfo().getFluidState().isTagged(FluidTags.WATER)) {
			GlStateManager.fogMode(GlStateManager.FogMode.LINEAR);
			event.setDensity(0.5f);
			event.setCanceled(true);
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

				//this.replaceStone(chunk);

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

				IGBaseBlock replaceBlock = IGBlockGrabber.grabBlock(MaterialUseType.ROCK,
						EnumMaterials.Rhyolite.material);

				for (BiomeLayerData b : data.worldLayerData) {
					for (int x = 0; x < 16; x++) {
						for (int z = 0; z < 16; z++) {
							for (int y = 0; y < 16; ++y) {
								Biome biome = chunk.getBiome(new BlockPos(x, y, z));
								float trueY = (yPos + y);
								if (b.getLbiome() == biome) {
									int lc = b.getLayerCount();
									for (int l = lc; l > 0; l--) {
										int totalHeight = 256;
										if ((trueY < (totalHeight * l) / lc) && (trueY >= (((totalHeight * l) / lc)
												- ((totalHeight * l) / lc) / l))) {
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
