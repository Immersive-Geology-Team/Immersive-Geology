package com.igteam.immersivegeology.client.pack;

import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IGGeneratedPack implements IResourcePack
{
	private final Map<String, byte[]> resources = new HashMap<>();

	public void put(String path, byte[] data)
	{
		resources.put(path, data);
	}

	public void put(String path, String text)
	{
		resources.put(path, text.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}

	public boolean has(String path)
	{
		return resources.containsKey(path);
	}

	public int size()
	{
		return resources.size();
	}

	public void dumpTo(java.io.File root)
	{
		for(java.util.Map.Entry<String, byte[]> entry : resources.entrySet())
		{
			String path = entry.getKey().replace(':', '/');
			java.io.File target = new java.io.File(root, path);
			target.getParentFile().mkdirs();
			try(java.io.FileOutputStream out = new java.io.FileOutputStream(target))
			{
				out.write(entry.getValue());
			} catch(java.io.IOException ignored)
			{
			}
		}
	}

	public void clear()
	{
		resources.clear();
	}

	private String key(ResourceLocation location)
	{
		return location.getNamespace()+":"+location.getPath();
	}

	@Override
	public InputStream getInputStream(ResourceLocation location) throws IOException
	{
		byte[] data = resources.get(key(location));
		if(data==null) throw new IOException("Not generated: "+location);
		return new ByteArrayInputStream(data);
	}

	@Override
	public boolean resourceExists(ResourceLocation location)
	{
		return resources.containsKey(key(location));
	}

	@Override
	public Set<String> getResourceDomains()
	{
		return Collections.singleton(IGLib.MODID);
	}

	@Override
	public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer serializer, String section)
	{
		return null;
	}

	@Override
	public BufferedImage getPackImage()
	{
		return null;
	}

	@Override
	public String getPackName()
	{
		return "Immersive Geology Generated Assets";
	}

	public static InputStream openExisting(ResourceLocation location) throws IOException
	{
		return Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream();
	}
}
