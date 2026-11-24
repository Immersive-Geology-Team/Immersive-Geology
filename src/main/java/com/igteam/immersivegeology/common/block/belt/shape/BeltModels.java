/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt.shape;

import com.igteam.immersivegeology.common.block.belt.helper.BeltRun;
import com.igteam.immersivegeology.common.block.belt.helper.EndergateRole;
import com.igteam.immersivegeology.common.block.belt.helper.BeltStyle;

public final class BeltModels
{
	public static final String BASIC = "basic";
	public static final String COVERED = "covered";
	public static final String REVERSE_SUFFIX = "_reverse";
	public static final String CAPPED_SUFFIX = "_capped";
	public static final String OUTLET_SUFFIX = "_outlet";

	private BeltModels()
	{
	}

	public static String family(BeltStyle style)
	{
		return style.covered()?COVERED: BASIC;
	}

	public static String piece(BeltStyle style, BeltShape shape, BeltRun run, boolean capped)
	{
		String suffix = shape.isDownhill()?REVERSE_SUFFIX: "";
		BeltShape uphill = shape.uphill();
		BeltRun uphillRun = shape.isDownhill()?run.reversed(): run;
		String base = style.covered()?coveredPiece(uphill, uphillRun): basicPiece(uphill);
		if(capped&&uphill==BeltShape.RAMP&&!uphillRun.coveredBehind())
			base += CAPPED_SUFFIX;
		return family(style)+"/"+base+suffix;
	}

	public static String endergatePiece(BeltStyle style, EndergateRole role, BeltShape inputs)
	{
		String suffix = inputs==BeltShape.STRAIGHT?"": "_"+inputs.getSerializedName();
		return (style.covered()?"endergate_covered/": "endergate/")+role.getSerializedName()+suffix;
	}

	public static String splitterPiece(BeltStyle style)
	{
		return style.covered()?"splitter/splitter_covered": "splitter/splitter";
	}

	public static String droppingPiece(BeltStyle style, BeltShape shape)
	{
		return (style.covered()?"dropping_covered/": "dropping/")+shape.getSerializedName();
	}

	public static String extractionPiece(BeltStyle style)
	{
		return style.covered()?"extraction/extraction_covered": "extraction/extraction";
	}

	public static String poweredPiece(BeltStyle style, BeltShape shape)
	{
		return (style.covered()?"powered_covered/": "powered/")+shape.getSerializedName();
	}

	public static String riserPiece(BeltStyle style, RiserShape shape, BeltRun run, boolean outlet)
	{
		String family = style.covered()?"rising_covered": "rising";
		if(!style.covered())
			return family+"/"+shape.getSerializedName()+(outlet?OUTLET_SUFFIX: "");
		if(shape!=RiserShape.STRAIGHT)
			return family+"/"+shape.getSerializedName();
		return family+"/"+switch(run)
		{
			case SINGLE -> "straight";
			case START -> "straight_back";
			case MIDDLE -> "straight_mid";
			case END -> "straight_front";
		};
	}

	private static String basicPiece(BeltShape shape)
	{
		return switch(shape)
		{
			case VALLEY_RISE -> "valley";
			case VALLEY_RISE_END -> "valley_end";
			default -> shape.getSerializedName();
		};
	}

	private static String coveredPiece(BeltShape shape, BeltRun run)
	{
		return switch(shape)
		{
			case STRAIGHT -> switch(run)
			{
				case SINGLE -> "straight";
				case START -> "straight_back";
				case MIDDLE -> "straight_mid";
				case END -> "straight_front";
			};
			case RAMP_START -> run.coveredBehind()?"ramp_start_empty": "ramp_start";
			case RAMP -> switch(run)
			{
				case SINGLE -> "ramp";
				case START -> "ramp_front";
				case MIDDLE -> "ramp_mid";
				case END -> "ramp_back";
			};
			case RAMP_END -> switch(run)
			{
				case SINGLE -> "ramp_end";
				case END -> "ramp_end_front";
				case START, MIDDLE -> "ramp_end_mid_back";
			};
			case TO_RISER_LEFT, TO_RISER_RIGHT -> "straight";
			case TO_RISER_LEFT_FROM_RIGHT -> "from_right";
			case TO_RISER_RIGHT_FROM_LEFT -> "from_left";
			case VALLEY_RISE -> coveredPiece(BeltShape.RAMP, run);
			case VALLEY_RISE_END -> coveredPiece(BeltShape.RAMP_END, run);
			default -> shape.getSerializedName();
		};
	}
}
