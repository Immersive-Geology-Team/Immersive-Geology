package com.igteam.immersive_geology.api.materials;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.materials.helper.*;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.helper.processing.IGMaterialProcess;
import com.igteam.immersive_geology.api.materials.helper.processing.IGProcessingMethod;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialFluidBase;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialStoneBase;
import com.igteam.immersive_geology.api.materials.material_data.fluids.slurry.MaterialSlurryWrapper;
import com.igteam.immersive_geology.core.config.IGOreConfig;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.block.AbstractBlock;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by Pabilo8 on 25-03-2020.
 * This class represent a single material (both compound and single chemical element one).
 * Extend it or its children classes like MaterialMetalBase or MaterialStoneBase and then register
 * them in the MaterialRegistry class.
 * <p>
 * Materials can have blocks and items, all of which are provided by Immersive Geology.
 */
public abstract class Material
{

	protected ArrayList<IGProcessingMethod> inheritedProcessingMethods;

	public Material(){
		 inheritedProcessingMethods = new ArrayList<>();
	}

	private IGOreConfig oreConfiguration;

	public void setConfiguration(IGOreConfig config){
		this.oreConfiguration = config;
	}

	public MaterialStoneBase.EnumStoneType getStoneType(){
		return null;
	}
	/**
	 * @return material name
	 */
	public String getName()
	{
		return "missingno";
	}

	/**
	 * @return material mod ID
	 */
	@Nonnull
	public String getModID()
	{
		return IGLib.MODID;
	}

	/**
	 * @return whether is made of any real-word materials
	 */
	public boolean isFromPeriodicTable()
	{
		return getElements().size() > 0;
	}

	/**
	 * @return the chemical elements this material is composed of
	 */
	public abstract LinkedHashSet<ElementProportion> getElements();

	/**
	 * @return rarity - like common, uncommon, rare, epic, etc.
	 */
	@Nonnull
	public abstract Rarity getRarity();

	@Nonnull
	public abstract MaterialTypes getMaterialType();
	
	/**
	 * @return Used to help world generation where metal types have a native way of spawning
	 */
	@Nullable
	public abstract MaterialTypes getMaterialSubType();

	//Temperature properties

	/**
	 * @param state the state the material should be in
	 *              There are 3 states:
	 *              GAS - temperature is material's boiling
	 *              LIQUID - temperature is material's melting point
	 *              SOLID - temperature is material's solidified temperature (default for most)
	 * @return default temperature of the state in Kelvin
	 */
	public float getTemperatureFromState(@Nonnull MaterialState state)
	{
		switch(state)
		{
			//Can be any number, 0 celsius as default
			case SOLID:
				return 273.15f;
			case LIQUID:
				return getBoilingPoint();
		}
		//Gas
		return getMeltingPoint();
	}

	/**
	 * @param temperature of the material in Kelvin
	 * @return the state the material should be in,
	 * as described in {@link #getTemperatureFromState(MaterialState state)}
	 */
	public MaterialState getStateFromTemperature(float temperature)
	{
		if(temperature >= getBoilingPoint())
			return MaterialState.GAS;
		else if(temperature >= getMeltingPoint())
			return MaterialState.LIQUID;
		else
			return MaterialState.SOLID;
	}

	/**
	 * @return the boiling point of the material (in kelvin)
	 */
	public abstract int getBoilingPoint();

	/**
	 * @return the melting point of the material (in kelvin)
	 */
	public abstract int getMeltingPoint();

	/**
	 * @return material default color (used as a fallback) when in certain temperature
	 * the same as normal as default
	 */
	public abstract int getColor(int temperature);

	//Block Properties

	/**
	 * @return block hardness
	 */
	public abstract float getHardness();

	/**
	 * @return block mining speed
	 */
	public abstract float getMiningResistance();

	/**
	 * @return resistance against explosions
	 */
	public abstract float getBlastResistance();

	/**
	 * @return how dense is the block compared to vanilla stone (stone=1)
	 */
	public abstract float getDensity();

	/**
	 * @return tool level needed to mine material's blocks
	 */
	public int getBlockHarvestLevel()
	{
		return 0;
	}

	/**
	 * @return a block material (like Material.IRON)
	 */
	public net.minecraft.block.material.Material getBlockMaterial() {
		return net.minecraft.block.material.Material.ROCK;
	};

	/**
	 * @param useType the subtype checked (iterates trough all)
	 * @return if has this subtype
	 */
	public boolean hasSubtype(MaterialUseType useType)
	{
		return false;
	}

	/**
	 * @param useType the subtype checked (iterates trough all)
	 * @return a path to the texture
	 */
	@Nullable
	public String getSpecialSubtypeModelName(MaterialUseType useType)
	{
		return null;
	}

	/**
	 * @return how many uses do tools made of this material have
	 */

	private static AbstractBlock.Properties block_props = AbstractBlock.Properties.create(net.minecraft.block.material.Material.ROCK);

	public AbstractBlock.Properties getMaterialBlockProperties(){
		return block_props;
	}

	public Item.Properties getMaterialItemProperties(){
		return new Item.Properties().group(ImmersiveGeology.IGGroup).rarity(getRarity());
	}

	public IGOreConfig getGenerationConfig() {
		return this.oreConfiguration;
	}

	public boolean hasAdditionalTags(){
		return false;
	};

	public ArrayList<String> getTagList(){
		return null;
	}

	public float getMinDrops(){
		return 1F;
	};

	public float getMaxDrops(){
		return 1F;
	};
	/**
	 *
	 * @return the tool tier / material, look at {@link net.minecraft.item.ItemTier}
	 */
	//TODO: Add material tools
	//@Nullable
	//public abstract IItemTier getToolTier();

	public CrystalFamily getCrystalFamily(){
		return CrystalFamily.CUBIC; //default cubic
	}

    public abstract MaterialEnum getProcessedType();
	public abstract MaterialEnum getSecondaryType();

	public int getViscosity(){
		return 1000;
	}

	//Check to see if this material already exists, used to prevent recipe duplication.
	public abstract boolean preExists();

	/**
	 * Only Call once otherwise duplicates may ensue
	 * @return
	 */
	public IGMaterialProcess getProcessingMethod(){
		//Using a set prevents duplicate elements... Just in case
		Set<IGProcessingMethod> methods = new LinkedHashSet<>();
		methods.addAll(inheritedProcessingMethods);
		return new IGMaterialProcess(methods.toArray(new IGProcessingMethod[methods.size()]));
	}

	public boolean hasSlurry(){
		return false;
	}

	public String getDisplayName(){
		return I18n.format("material." + IGLib.MODID + "." + this.getName().toLowerCase());
	}
}