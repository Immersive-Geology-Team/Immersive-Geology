package com.igteam.immersivegeology.client.manual;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.api.ManualPageMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.structure.IGBallmillStructure;
import com.igteam.immersivegeology.core.lib.IGLib;
import blusunrize.lib.manual.ManualPages;

public final class IGManualEntries
{
	private IGManualEntries()
	{
	}

	public static void register()
	{
		ManualHelper.addEntry("ballmill", ManualHelper.CAT_HEAVYMACHINES,
				new ManualPages.Text(ManualHelper.getManual(), "ballmill0"),
				new ManualPageMultiblock(ManualHelper.getManual(), "ballmill1", IGBallmillStructure.INSTANCE));

		IGLib.IG_LOGGER.info("- Registered test IE manual entry: ballmill");
	}
}
