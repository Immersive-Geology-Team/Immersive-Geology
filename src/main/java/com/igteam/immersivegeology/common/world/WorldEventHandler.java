package com.igteam.immersivegeology.common.world;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.common.network.ChunkDataPacket;
import com.igteam.immersivegeology.common.network.PacketHandler;
import com.igteam.immersivegeology.common.world.chunk.data.ChunkData;
import com.igteam.immersivegeology.common.world.chunk.data.ChunkDataCapability;
import com.igteam.immersivegeology.common.world.chunk.data.ChunkDataProvider;
import com.igteam.immersivegeology.common.world.climate.ClimateIG;
import com.igteam.immersivegeology.common.world.layer.WorldLayerData;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.IProfiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.PacketDistributor;

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

	@SubscribeEvent
	public static void onChunkWatchWatch(ChunkWatchEvent.Watch event) {
		ChunkPos pos = event.getPos();
		if (pos != null) {
			ChunkData.get(event.getWorld().getChunk(pos.asBlockPos())).ifPresent(data -> {
				// Update server side climate cache
				ClimateIG.update(pos, data.getRegionalTemp(), data.getRainfall());

				// Update client side data
				PacketHandler.get().send(PacketDistributor.PLAYER.with(event::getPlayer),
						new ChunkDataPacket(pos.x, pos.z, data));
			});
		}
	}

	@OnlyIn(value = Dist.CLIENT)
	@SubscribeEvent
	public void onGameRender(RenderGameOverlayEvent.Post event) {
		Minecraft mc = Minecraft.getInstance();
		IProfiler profiler = mc.getProfiler();
		ItemStack main = mc.player.getHeldItemMainhand();
		ItemStack offhand = mc.player.getHeldItemOffhand();

		RayTraceResult pos = mc.objectMouseOver;

		if (pos != null) {
			BlockPos bpos = pos.getType() == RayTraceResult.Type.BLOCK ? ((BlockRayTraceResult) pos).getPos() : null;
			BlockState state = bpos != null ? mc.world.getBlockState(bpos) : null;
			Block block = state == null ? null : state.getBlock();
			TileEntity tile = bpos != null ? mc.world.getTileEntity(bpos) : null;

			if (event.getType() == ElementType.ALL) {
				profiler.startSection("ig-debug-info");
				int w = mc.mainWindow.getScaledWidth();
				int h = mc.mainWindow.getScaledHeight();
				ChunkPos cpos = new ChunkPos(mc.player.chunkCoordX, mc.player.chunkCoordZ);
				ChunkDataProvider chunkDataProvider = ChunkDataProvider.get(mc.world);
				if (chunkDataProvider != null) {
					mc.fontRenderer.drawStringWithShadow(
							"Regional Temp: " + String.valueOf(chunkDataProvider.get(cpos).getRegionalTemp()),
							w / 2 + 30, h / 2 + 10, 0xFFAA00);
					mc.fontRenderer.drawStringWithShadow(
							"Average Temp: " + String.valueOf(chunkDataProvider.get(cpos).getAvgTemp()), w / 2 + 30,
							h / 2 + 20, 0xFFAA00);
					mc.fontRenderer.drawStringWithShadow(
							"Regional Rainfall: " + String.valueOf(chunkDataProvider.get(cpos).getRainfall()),
							w / 2 + 30, h / 2 + 30, 0xFFAA00);
				}
				profiler.endSection();
			}
		}
	}

	/*
	 * TODO Better Water fog for deep sea
	 * 
	 * @OnlyIn(Dist.CLIENT)
	 * 
	 * @SubscribeEvent public void onRenderFog(EntityViewRenderEvent.FogDensity
	 * event) { if (event.getInfo().getFluidState().isTagged(FluidTags.WATER)) {
	 * GlStateManager.fogMode(GlStateManager.FogMode.LINEAR);
	 * event.setDensity(0.5f); event.setCanceled(true); } }
	 */

	/*
	 * Part of old stone replace method
	 * 
	 * @SubscribeEvent(priority = EventPriority.LOWEST) public void
	 * onChunkPopulate(ChunkEvent.Load event) {
	 * 
	 * 
	 * if (!worldDone || event.getWorld() == null || event.getChunk() == null)
	 * return;
	 * 
	 * if (EffectiveSide.get() == LogicalSide.SERVER) {
	 * 
	 * WorldType type = event.getWorld().getWorldInfo().getGenerator();
	 * 
	 * if (!WorldChunkChecker.hasAlreadyBeenIGfied(event.getChunk())) { IChunk chunk
	 * = event.getChunk();
	 * 
	 * //this.replaceStone(chunk);
	 * 
	 * ((Chunk) chunk).setModified(true); ((Chunk) chunk).markDirty();
	 * WorldChunkChecker.setDone(event.getChunk()); } } }
	 */

	@SubscribeEvent
	public static void onAttachCapabilitiesChunk(AttachCapabilitiesEvent<Chunk> event) {
		World world = event.getObject().getWorld();
		if (world.getWorldType() == ImmersiveGeology.getWorldType()) {
			// Add the rock data to the chunk capability, for long term storage
			ChunkData data;
			ChunkDataProvider chunkDataProvider = ChunkDataProvider.get(world);
			if (chunkDataProvider != null) {
				data = chunkDataProvider.get(event.getObject());
			} else {
				data = new ChunkData();
			}

			event.addCapability(ChunkDataCapability.KEY, data);
		}
	}

	/*
	 * Old Layer Method
	 * 
	 * private void replaceStone(IChunk chunk) {
	 * 
	 * int xPos = chunk.getPos().x * 16; int zPos = chunk.getPos().z * 16;
	 * 
	 * for (ChunkSection storage : chunk.getSections()) { if (storage != null &&
	 * !storage.isEmpty()) { int yPos = storage.getYLocation();
	 * 
	 * IGBaseBlock replaceBlock = IGBlockGrabber.grabBlock(MaterialUseType.ROCK,
	 * EnumMaterials.Rhyolite.material);
	 * 
	 * for (BiomeLayerData b : data.worldLayerData) { for (int x = 0; x < 16; x++) {
	 * for (int z = 0; z < 16; z++) { for (int y = 0; y < 16; ++y) { Biome biome =
	 * chunk.getBiome(new BlockPos(x, y, z)); float trueY = (yPos + y); if
	 * (b.getLbiome() == biome) { int lc = b.getLayerCount(); for (int l = lc; l >
	 * 0; l--) { int totalHeight = 256; if ((trueY < (totalHeight * l) / lc) &&
	 * (trueY >= (((totalHeight * l) / lc) - ((totalHeight * l) / lc) / l))) {
	 * replaceBlock = b.getLayerBlock(l);
	 * 
	 * Block oldBlock = storage.getBlockState(x, y, z).getBlock();
	 * 
	 * // setting the block in here isn't super efficient, but it works. int nh =
	 * (int) replaceBlock.getDefaultHardness();
	 * 
	 * nh = (int) (12 - (Math.pow(Math.E, 2.6) * Math.log(trueY / 135)));
	 * 
	 * if (oldBlock == Blocks.STONE) { storage.setBlockState(x, y, z,
	 * replaceBlock.getDefaultState() .with(IGBaseBlock.NATURAL,
	 * Boolean.valueOf(true)) .with(IGBaseBlock.HARDNESS, Integer.valueOf( (int)
	 * Math.min(256, Math.max(0, nh)))), true); } } } } } } } } } } }
	 */
}
