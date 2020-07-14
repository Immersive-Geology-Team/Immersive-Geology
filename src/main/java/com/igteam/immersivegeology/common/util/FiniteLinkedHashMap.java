package com.igteam.immersivegeology.common.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class FiniteLinkedHashMap<K, V> extends LinkedHashMap<K, V>
{
	private final int maxSize;

	public FiniteLinkedHashMap(int maxSize)
	{
		super(maxSize);
		this.maxSize = maxSize;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest)
	{
		return size() > maxSize;
	}
}