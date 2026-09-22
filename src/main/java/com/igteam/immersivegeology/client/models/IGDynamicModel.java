package com.igteam.immersivegeology.client.models;

import blusunrize.immersiveengineering.client.ClientUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class IGDynamicModel
{
	private final ResourceLocation location;
	private final String group;
	private IBakedModel baked;

	public IGDynamicModel(String path, String group)
	{
		this.location = new ResourceLocation(IGLib.MODID, path);
		this.group = group;
	}

	public void reset()
	{
		baked = null;
	}

	public IBakedModel get()
	{
		if(baked!=null) return baked;
		try
		{
			OBJModel model = (OBJModel)OBJLoader.INSTANCE.loadModel(location)
					.process(ImmutableMap.of("flip-v", "true"));
			baked = model.bake(new OBJModel.OBJState(ImmutableList.of(group), true, ModelRotation.X0_Y0),
					DefaultVertexFormats.ITEM, ClientUtils::getSprite);
		}
		catch(Exception e)
		{
			IGLib.IG_LOGGER.error("Failed to load OBJ model {} (group {})", location, group, e);
		}
		return baked;
	}
}
