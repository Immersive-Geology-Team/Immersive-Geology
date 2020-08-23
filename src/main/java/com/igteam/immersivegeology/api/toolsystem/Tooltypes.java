package com.igteam.immersivegeology.api.toolsystem;

import blusunrize.immersiveengineering.common.items.HammerItem;
import blusunrize.immersiveengineering.common.items.ScrewdriverItem;
import blusunrize.immersiveengineering.common.items.WirecutterItem;
import com.igteam.immersivegeology.ImmersiveGeology;
import net.minecraftforge.common.ToolType;

/**
 * @author Pabilo8
 * @author CrimsonTwilight
 * @since 21.08.2020
 */
public class Tooltypes
{
	public static final ToolType HAMMER_TOOL = HammerItem.HAMMER_TOOL;
	public static final ToolType ADVANCED_HAMMER_TOOL = getTool("advanced_hammer");

	public static final ToolType WRENCH_TOOL = getTool("wrench");
	public static final ToolType ADVANCED_WRENCH_TOOL = getTool("advanced_wrench");

	public static final ToolType SCREWDIVER_TOOL = ScrewdriverItem.SCREWDRIVER_TOOL;
	public static final ToolType ADVANCED_SCREWDIVER_TOOL = getTool("advanced_screwdiver");

	public static final ToolType WIRECUTTER_TOOL = WirecutterItem.CUTTER_TOOL;
	public static final ToolType ADVANCED_CUTTER_TOOL = getTool("advanced_cutter");

	//Crafting tools
	//public static final ToolType FORGE_HAMMER_TOOL = getTool("forge_hammer"); This is the same as the regular Hammer
	public static final ToolType SCISSORS_TOOL = getTool("scissor");
	public static final ToolType CHISEL_TOOL = getTool("chisel");
	public static final ToolType FILE_TOOL = getTool("file");
	public static final ToolType PINCERS_TOOL = getTool("pincers");
	public static final ToolType SAW_TOOL = getTool("saw");

	public static final ToolType CROWBAR_TOOL = getTool("crowbar");
	public static final ToolType SCYTHE_TOOL = getTool("scythe");

	public static final ToolType PICKAXE_TOOL = ToolType.PICKAXE;
	public static final ToolType AXE_TOOL = ToolType.AXE;
	public static final ToolType SHOVEL_TOOL = ToolType.SHOVEL;
	public static final ToolType HOE_TOOL = ToolType.get("hoe");

	//The regular sword
	public static final ToolType BROADSWORD_TOOL = ToolType.get("sword");
	public static final ToolType SABRE_TOOL = getTool("sabre");
	public static final ToolType RAPIER_TOOL = getTool("rapier");
	public static final ToolType CUTLASS_TOOL = getTool("cutlass");
	public static final ToolType KATANA_TOOL = getTool("katana");
	public static final ToolType MACE_TOOL = getTool("mace");
	public static final ToolType BAYONET_TOOL = getTool("bayonet");

	private static ToolType getTool(String name)
	{
		return ToolType.get(ImmersiveGeology.MODID+"_"+name);
	}
}
