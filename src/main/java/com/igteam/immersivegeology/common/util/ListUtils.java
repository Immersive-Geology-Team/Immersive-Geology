package com.igteam.immersivegeology.common.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {

	public static<T> List[] partition(List<T> list, int n) {
		int size = list.size();
		
		int m = size / n;
		if(size % n != 0) {
			m++;
		}
		
		List<T>[] partition = new ArrayList[m];
		for(int i = 0; i < m; i++) {
			int fromIndex = i * n;
			int toIndex = (i * n + m < size) ? (i*n + n) : size;
			partition[i] = new ArrayList(list.subList(fromIndex, toIndex));
		}
		
		return partition;
	}
	
}
