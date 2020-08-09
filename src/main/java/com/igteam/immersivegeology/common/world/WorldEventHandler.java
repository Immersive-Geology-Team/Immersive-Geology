package com.igteam.immersivegeology.common.world;

import java.util.Arrays;
import java.util.Random;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.common.blocks.IGMaterialBlock;
import com.igteam.immersivegeology.common.blocks.IGOreBearingBlock;
import com.igteam.immersivegeology.common.blocks.property.IGProperties;
import com.igteam.immersivegeology.common.network.ChunkDataPacket;
import com.igteam.immersivegeology.common.network.PacketHandler;
import com.igteam.immersivegeology.common.world.chunk.data.ChunkData;
import com.igteam.immersivegeology.common.world.chunk.data.ChunkDataCapability;
import com.igteam.immersivegeology.common.world.chunk.data.ChunkDataProvider;
import com.igteam.immersivegeology.common.world.climate.ClimateIG;
import com.muddykat.noise.NoiseGenTester;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.IProfiler;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.network.PacketDistributor;

public class WorldEventHandler
{

	private boolean worldDone = false;
	private int seed;
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onWorldLoad(WorldEvent.Load event)
	{
		if(EffectiveSide.get()==LogicalSide.SERVER)
		{
			if(0==event.getWorld().getDimension().getType().getId())
			{
				if(!worldDone)
				{
					worldDone = true;
					
				}
			}
		}
		
		//NoiseGenTester gen = new NoiseGenTester();
 		//gen.generate(event.getWorld().getSeed());
	}

	@SubscribeEvent
	public static void onChunkWatchWatch(ChunkWatchEvent.Watch event)
	{
		ChunkPos pos = event.getPos();
		if(pos!=null)
		{
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
	public void onGameRender(RenderGameOverlayEvent.Post event)
	{
		Minecraft mc = Minecraft.getInstance();
		IProfiler profiler = mc.getProfiler();
		ItemStack main = mc.player.getHeldItemMainhand();
		ItemStack offhand = mc.player.getHeldItemOffhand();

		RayTraceResult pos = mc.objectMouseOver;

		if(pos!=null)
		{
			BlockPos bpos = pos.getType()==RayTraceResult.Type.BLOCK?((BlockRayTraceResult)pos).getPos(): null;
			BlockState state = bpos!=null?mc.world.getBlockState(bpos): null;
			Block block = state==null?null: state.getBlock();
			TileEntity tile = bpos!=null?mc.world.getTileEntity(bpos): null;
			if(event.getType()==ElementType.ALL)
			{
				profiler.startSection("ig-debug-info");
				int w = mc.mainWindow.getScaledWidth();
				int h = mc.mainWindow.getScaledHeight();
				ChunkPos cpos = new ChunkPos(mc.player.chunkCoordX, mc.player.chunkCoordZ);
				ChunkDataProvider chunkDataProvider = ChunkDataProvider.get(mc.world);
				if(chunkDataProvider!=null)
				{
					//mc.fontRenderer.drawStringWithShadow(
					//		"Regional Temp: " + String.valueOf(chunkDataProvider.get(cpos).getRegionalTemp()),
					//		w / 2 + 30, h / 2 + 10, 0xFFAA00);
//					mc.fontRenderer.drawStringWithShadow(
//							"Average Temp: " + String.valueOf(chunkDataProvider.get(cpos).getAvgTemp()), w / 2 + 30,
//							h / 2 + 20, 0xFFAA00);
//					mc.fontRenderer.drawStringWithShadow(
//							"Regional Rainfall: " + String.valueOf(chunkDataProvider.get(cpos).getRainfall()),
//							w / 2 + 30, h / 2 + 30, 0xFFAA00);
				}
				profiler.endSection();
			}
		}
	}


	@SubscribeEvent
	public void onBlockBreaking(PlayerEvent.BreakSpeed event)
	{
		float original = event.getOriginalSpeed();

		Block block = event.getState().getBlock();
		if(block instanceof IGMaterialBlock)
		{
			IGMaterialBlock replaceBlock = (IGMaterialBlock)block;

			if(event.getState().get(IGProperties.NATURAL))
			{
				//depends of primary block material
				double nh = replaceBlock.getMaterial().getHardness();
				int y = event.getPos().getY();
				double max = Math.max(1, y);
				double ns = (((0.3/Math.pow(Math.E, 8))*Math.pow(max, Math.E*0.75))*(original/8))/nh;
				event.setNewSpeed((float)ns);
			}
		}
		if(block instanceof IGOreBearingBlock)
		{
			IGOreBearingBlock replaceBlock = (IGOreBearingBlock)block;

			if(event.getState().get(IGProperties.NATURAL))
			{
				//depends of primary block material
				double nh = replaceBlock.getMaterial().getHardness();
				System.out.println("Ore start Hard: "+nh);
				int y = event.getPos().getY();
				double max = Math.max(1, y);
				double ns = (((0.3/Math.pow(Math.E, 8))*Math.pow(max, Math.E*0.75))*(original/8))/nh;
				System.out.println("Ore End Hard: "+ns);
				event.setNewSpeed((float)ns);
			}
		}
	}

	@SubscribeEvent
	public static void onAttachCapabilitiesChunk(AttachCapabilitiesEvent<Chunk> event)
	{
		World world = event.getObject().getWorld();
		if(world.getWorldType()==ImmersiveGeology.getWorldType())
		{
			// Add the rock data to the chunk capability, for long term storage
			ChunkData data;
			ChunkDataProvider chunkDataProvider = ChunkDataProvider.get(world);
			if(chunkDataProvider!=null)
			{
				data = chunkDataProvider.get(event.getObject());
			}
			else
			{
				data = new ChunkData();
			}

			event.addCapability(ChunkDataCapability.KEY, data);
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
}
