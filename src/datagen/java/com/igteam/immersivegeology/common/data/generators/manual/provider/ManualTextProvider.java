/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators.manual.provider;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ManualTextProvider
{
	protected ResourceLocation location;
	protected String name, title, subtitle;
	protected int priority;

	protected final StringBuilder finalString;
	protected LinkedHashMap<String, String> boundPages = new LinkedHashMap<>();

	public ManualTextProvider(ResourceLocation location) {
		this.location = location;
		this.name = location.getPath().toLowerCase();

		this.finalString = new StringBuilder();
	}

	public ManualTextProvider attachPage(String anchor, String text) {
		boundPages.put(anchor, text);
		return this;
	}

	public ResourceLocation getLocation() {
		return location;
	}

	public String getResult() {
		finalString.append(title + "\n");

		finalString.append(subtitle + "\n");

		List<String> keyList = new ArrayList<>(boundPages.keySet());

		for (String key : keyList) {
			String line = "<&" + key + "> " + boundPages.get(key) + "\n";
			finalString.append(line);
		}

		return finalString.toString();
	}

	public ManualTextProvider setTitle(String title, String subtitle) {
		this.title = title;
		this.subtitle = subtitle;
		return this;
	}
}
