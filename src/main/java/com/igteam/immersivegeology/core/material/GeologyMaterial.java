package com.igteam.immersivegeology.core.material;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.configuration.ConfigurationHelper;
import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.common.world.features.helper.noise.IGGenerationType;
import com.igteam.immersivegeology.core.material.helper.material.IGBlockProperties;
import com.igteam.immersivegeology.core.material.helper.material.IGTag;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGRecipeStage;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeChain;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGStageProvider;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.Set;

public abstract class GeologyMaterial implements MaterialHelper
{
	protected String name;
	protected String unserialized_name;

	private final LinkedHashSet<IFlagType<?>> materialDataFlags = new LinkedHashSet<IFlagType<?>>();
	private final Map<ModFlags, Map<IFlagType<?>, MaterialHelper>> existingImplementations =
			new HashMap<ModFlags, Map<IFlagType<?>, MaterialHelper>>();

	protected Object materialRarity = null;

	public void setRarity(Object rarity)
	{
		this.materialRarity = rarity;
	}

	public Object getRarity()
	{
		return materialRarity;
	}

	protected Set<StoneFormation> acceptableStoneTypes = new HashSet<StoneFormation>();

	public GeologyMaterial()
	{
		String className = getClass().getSimpleName().replace("Material", "");
		this.unserialized_name = className;
		this.name = className.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.ROOT);
		initializeFlags();
	}

	public void initializeFlags()
	{
		for(ItemCategoryFlags flag : ConfigurationHelper.defaultItemFlags(this)) addFlags(flag);
		for(BlockCategoryFlags flag : ConfigurationHelper.defaultBlockFlags(this)) addFlags(flag);
	}

	protected void addFlags(IFlagType<?>... flags)
	{
		materialDataFlags.addAll(Arrays.asList(flags));
	}

	protected void removeMaterialFlags(IFlagType<?>... flags)
	{
		materialDataFlags.removeAll(Arrays.asList(flags));
	}

	@Override
	public boolean hasFlag(IFlagType<?> flag)
	{
		return materialDataFlags.contains(flag);
	}

	@Override
	public Set<IFlagType<?>> getFlags()
	{
		return materialDataFlags;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public String getUnserializedName()
	{
		return unserialized_name;
	}

	protected BiFunction<IFlagType<?>, Integer, Integer> colorFunction = materialColorFunction();
	protected BiPredicate<IFlagType<?>, Integer> applyColorTint = (flag, index) -> true;

	protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction()
	{
		return (flag, index) -> 0xffffffff;
	}

	public void initializeColorTint(BiPredicate<IFlagType<?>, Integer> predicate)
	{
		this.applyColorTint = predicate;
	}

	@Override
	public int getColor(IFlagType<?> flag, int secondaryColors)
	{
		if(colorFunction==null) colorFunction = materialColorFunction();
		return applyColorTint.test(flag, secondaryColors)
				?colorFunction.apply(flag, secondaryColors)
				: 0xFFFFFFFF;
	}

	protected final List<java.util.function.Supplier<Object>> validMultiblocks = new java.util.ArrayList<>();

	public List<java.util.function.Supplier<Object>> getValidMultiblocks()
	{
		return validMultiblocks;
	}

	public IGTag getFluidTag()
	{
		return getFluidTag(BlockCategoryFlags.FLUID);
	}

	public IGTag getFluidTag(BlockCategoryFlags flag)
	{
		return getFluidTag(flag, new MaterialHelper[0]);
	}

	public IGTag getFluidTag(BlockCategoryFlags flag, MaterialInterface<?>... materials)
	{
		MaterialHelper[] helpers = new MaterialHelper[materials.length];
		for(int i = 0; i < materials.length; i++) helpers[i] = materials[i].instance();
		return getFluidTag(flag, helpers);
	}

	protected IGRecipeChain directBlasting = new IGRecipeChain(this, "direct_blasting", 0);
	protected IGRecipeChain sulphideElectrowining = new IGRecipeChain(this, "sulphide_electrowining", 1);

	private final LinkedHashSet<IGRecipeStage> stageSet = new LinkedHashSet<>();

	public Set<IGRecipeChain> getRecipeChains()
	{
		return Collections.emptySet();
	}

	public void setupRecipeStages()
	{
	}

	public Set<IGRecipeStage> getMaterialStageSet()
	{
		return stageSet;
	}

	public void addStage(IGRecipeStage stage)
	{
		stageSet.add(stage);
	}

	public void buildRecipe()
	{
		setupRecipeStages();
		IGStageProvider.add(this, stageSet);
	}

	private float asocialMaterialChance = 1f;

	public void setAsocialMaterialChance(float chance)
	{
		this.asocialMaterialChance = chance;
	}

	public double getAssociateMaterialChance()
	{
		return asocialMaterialChance;
	}

	private final Set<com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGShimTypes.Pair<
			java.util.function.Function<Integer, com.igteam.immersivegeology.core.material.helper.material.MaterialHelper>, Integer>>
			associateMaterials = new java.util.LinkedHashSet<>();

	public void addGenerationFriend(
			java.util.function.Function<Integer, com.igteam.immersivegeology.core.material.helper.material.MaterialHelper> material,
			int chance)
	{
		associateMaterials.add(
				com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGShimTypes.Pair.of(material, chance));
	}

	public Set<com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGShimTypes.Pair<
			java.util.function.Function<Integer, com.igteam.immersivegeology.core.material.helper.material.MaterialHelper>, Integer>>
			getAssociateMaterialSet()
	{
		return associateMaterials;
	}

	public com.igteam.immersivegeology.common.item.IGGenericDrillHead.DrillHeadProps drillHeadInstance()
	{
		return null;
	}

	public double getMinDownfall()
	{
		return 0d;
	}

	public double getMaxDownfall()
	{
		return 1d;
	}

	public long seed()
	{
		return 0L;
	}

	public Object getConfig()
	{
		return null;
	}

	public void entityInside(net.minecraft.block.state.IBlockState state, net.minecraft.world.World level,
			net.minecraft.util.math.BlockPos pos, net.minecraft.entity.Entity entity)
	{
	}

	public double getMinSpawnTemp()
	{
		return 0d;
	}

	public double getMaxSpawnTemp()
	{
		return 2d;
	}

	public com.igteam.immersivegeology.core.lib.shim.MCShims.Properties getFluidProperties(IFlagType<?> flag)
	{
		return new com.igteam.immersivegeology.core.lib.shim.MCShims.Properties();
	}

	public Set<MaterialInterface<?>> getGeneratedMaterials()
	{
		return java.util.Collections.emptySet();
	}

	public com.igteam.immersivegeology.core.lib.shim.MCShims.ITier getToolTier()
	{
		return null;
	}

	public int getToolDamage()
	{
		return 3;
	}

	public int getToolSpeed()
	{
		return 3;
	}

	public boolean hasCustomTexture(BlockCategoryFlags blockCategoryFlags)
	{
		return false;
	}

	private int burnTime = 0;

	public void setBurntime(int ticks)
	{
		this.burnTime = ticks;
	}

	public int getBurntime()
	{
		return burnTime;
	}

	public boolean canFormMB(com.igteam.immersivegeology.core.lib.shim.IGMultiblockRefs.IMultiblock multiblock)
	{
		for(java.util.function.Supplier<Object> supplier : validMultiblocks)
			if(supplier.get().equals(multiblock)) return true;
		return false;
	}

	public float getNoiseProbability()
	{
		return 1.0f;
	}

	public IGBlockProperties getProperties(IFlagType<?> flag)
	{
		return IGBlockProperties.of(Material.ROCK);
	}

	public List<String> getAcceptableDimensions()
	{
		return Collections.singletonList("minecraft:overworld");
	}

	public boolean useColumnBlockStyle(IFlagType<?> flag)
	{
		return false;
	}

	public Set<com.igteam.immersivegeology.core.material.helper.HazardTypes> getHazards()
	{
		return Collections.emptySet();
	}

	public ResourceLocation getTextureLocation(IFlagType<?> flag)
	{
		return new ResourceLocation(IGLib.MODID,
				(flag instanceof ItemCategoryFlags?"item": "block")+"/colored/"+getName()+"/"+flag.getName());
	}

	public CrystalFamily getCrystalFamily()
	{
		return CrystalFamily.CUBIC;
	}

	public int getPaletteVariation(ItemCategoryFlags flag)
	{
		return 1;
	}

	public int getPaletteVariation(BlockCategoryFlags flag)
	{
		return 1;
	}

	@Override
	public boolean acceptableStoneType(MaterialStone stone)
	{
		return stone!=null&&acceptableStoneTypes.contains(stone.getStoneFormation());
	}

	public boolean isValidStoneFormation(StoneFormation formation)
	{
		return acceptableStoneTypes.contains(formation);
	}

	public Set<StoneFormation> getValidStoneFormations()
	{
		return acceptableStoneTypes;
	}

	public Map<ModFlags, Map<IFlagType<?>, MaterialHelper>> getExistingImplementationMap()
	{
		return existingImplementations;
	}

	@Override
	public boolean checkExistingImplementation(IFlagType<?> flag)
	{
		for(ModFlags mod : ModFlags.values())
			if(mod.isLoaded()&&checkExistingImplementation(mod, flag)) return true;
		return false;
	}

	@Override
	public boolean checkExistingImplementation(ModFlags mod, IFlagType<?> flag)
	{
		Map<IFlagType<?>, MaterialHelper> map = existingImplementations.get(mod);
		return mod.isLoaded()&&map!=null&&map.containsKey(flag);
	}

	public boolean hasExistingFlag(IFlagType<?> flag)
	{
		for(Map<IFlagType<?>, MaterialHelper> map : existingImplementations.values())
			if(map.containsKey(flag)) return true;
		return false;
	}

	@Override
	public void addExistingFlag(ModFlags mod, ItemCategoryFlags... flags)
	{
		putExisting(mod, flags);
	}

	@Override
	public void addExistingFlag(ModFlags mod, BlockCategoryFlags... flags)
	{
		putExisting(mod, flags);
	}

	private void putExisting(ModFlags mod, IFlagType<?>... flags)
	{
		Map<IFlagType<?>, MaterialHelper> map = existingImplementations.get(mod);
		if(map==null)
		{
			map = new HashMap<IFlagType<?>, MaterialHelper>();
			existingImplementations.put(mod, map);
		}
		for(IFlagType<?> flag : flags) map.put(flag, this);
	}

	@Override
	public MaterialInterface<?> getPrimaryProduct()
	{
		java.util.LinkedHashSet<MaterialInterface<?>> set = getDerivedMaterials();
		if(set.isEmpty()) return null;
		return set.stream().toList().get(0);
	}

	@Override
	public MaterialInterface<?> getSecondaryProduct()
	{
		java.util.LinkedHashSet<MaterialInterface<?>> set = getDerivedMaterials();
		if(set.size() < 2) return null;
		return set.stream().toList().get(1);
	}

	@Override
	public MaterialInterface<?> getTraceProduct(int index)
	{
		return null;
	}
}
