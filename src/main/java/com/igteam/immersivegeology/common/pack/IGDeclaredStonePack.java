/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.pack;

import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An in-memory pack holding the resources for configuration-declared rock types.
 * <p>
 * Data generation runs when the mod is built and cannot know what a pack developer will declare, so these files
 * are produced on start instead. Check {@link IGDeclaredStoneAssets} for more info on that.
 */
public class IGDeclaredStonePack implements PackResources
{
	public static final String PACK_ID = IGLib.MODID+":declared_stone_types";

	private final PackLocationInfo location;
	private final PackType packType;
	private Map<ResourceLocation, byte[]> resources;
	private Set<String> namespaces;

	public IGDeclaredStonePack(PackLocationInfo location, PackType packType)
	{
		this.location = location;
		this.packType = packType;
	}

	/**
	 * Built on first access, as the pack is created while the pack repository is being
	 * assembled, and reading the ore blocks out of the registry before registration has finished would find
	 * nothing, so we need to wait until something actually asks for a file.
	 */
	private synchronized Map<ResourceLocation, byte[]> resources()
	{
		if(resources==null)
		{
			IGDeclaredStoneAssets.Assets assets = IGDeclaredStoneAssets.shared();
			resources = packType==PackType.CLIENT_RESOURCES?assets.client: assets.server;
			namespaces = resources.keySet().stream()
					.map(ResourceLocation::getNamespace)
					.collect(Collectors.toUnmodifiableSet());
		}
		return resources;
	}

	@Nullable
	@Override
	public IoSupplier<InputStream> getRootResource(String... elements)
	{
		return null;
	}

	@Nullable
	@Override
	public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location)
	{
		byte[] content = resources().get(location);
		return content==null?null: () -> new ByteArrayInputStream(content);
	}

	@Override
	public void listResources(PackType type, String namespace, String path, ResourceOutput output)
	{
		String prefix = path.endsWith("/")?path: path+"/";
		for(Map.Entry<ResourceLocation, byte[]> entry : resources().entrySet())
		{
			ResourceLocation location = entry.getKey();
			if(!location.getNamespace().equals(namespace)) continue;
			if(!location.getPath().startsWith(prefix)) continue;

			byte[] content = entry.getValue();
			output.accept(location, () -> new ByteArrayInputStream(content));
		}
	}

	@Override
	public Set<String> getNamespaces(PackType type)
	{
		resources();
		return namespaces;
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer)
	{
		if(!PackMetadataSection.TYPE.getMetadataSectionName().equals(deserializer.getMetadataSectionName())) return null;
		// Must be this pack's own format, not always the client one: a data pack advertising the resource pack
		// version can be judged incompatible and dropped by minecraft.
		return (T)new PackMetadataSection(
				Component.literal("Ore blocks for stone types declared in the Immersive Geology configuration"),
				SharedConstants.getCurrentVersion().getPackVersion(packType));
	}

	@Override
	public PackLocationInfo location()
	{
		return location;
	}

	@Override
	public void close()
	{
	}
}
