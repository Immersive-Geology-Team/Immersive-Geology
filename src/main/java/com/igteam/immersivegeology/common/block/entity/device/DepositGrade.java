/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.entity.device;

public enum DepositGrade
{
	UNVERIFIED(0, 0xFF4A4A4A),

	DEPLETED(1, 0xFF6B4A2A),
	POOR(25, 0xFFB4553C),
	NORMAL(150, 0xFFD9A441),
	RICH(500, 0xFF63D66A),
	EMPTY(0, 0x00000000);

	public final int minimumCount;
	public final int colour;

	DepositGrade(int minimumCount, int colour)
	{
		this.minimumCount = minimumCount;
		this.colour = colour;
	}

	public static DepositGrade of(int blockCount)
	{
		if(blockCount <= 0) return EMPTY;
		DepositGrade best = DEPLETED;
		for(DepositGrade grade : values())
		{
			if(grade!=UNVERIFIED&&grade!=EMPTY&&blockCount >= grade.minimumCount) best = grade;
		}
		return best;
	}

	public static DepositGrade byId(int id)
	{
		DepositGrade[] all = values();
		return id >= 0&&id < all.length?all[id]: UNVERIFIED;
	}

	public boolean hasDeposit()
	{
		return this!=UNVERIFIED&&this!=EMPTY;
	}
}
