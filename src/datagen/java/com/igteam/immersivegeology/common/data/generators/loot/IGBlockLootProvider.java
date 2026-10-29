// NOTICE: This file includes code adapted from Immersive Engineering.
// This code is used in accordance with the terms of the Blu's License of Common Sense,
// which requires disclosure of significant code usage.
// For more details, refer to the source at [https://github.com/BluSunrize/ImmersiveEngineering/tree/1.20.1].
//
// The original code has been modified to fit the requirements of this project.
// -\('-')/- ~Muddykat

package com.igteam.immersivegeology.common.data.generators.loot;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.common.util.loot.BEDropLootEntry;
import blusunrize.immersiveengineering.common.util.loot.DropInventoryLootEntry;
import blusunrize.immersiveengineering.data.loot.LootUtils;
import com.igteam.immersivegeology.common.block.entity.crate.IGCrateEntityType;
import com.igteam.immersivegeology.common.block.entity.device.IGMetalDetector;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.common.item.IGMetalDetectorItem;
import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.common.block.ore.IGCrystalBlock;
import com.igteam.immersivegeology.common.block.ore.IGEvaporateMineralBlock;
import com.igteam.immersivegeology.common.block.structural.IGSlabBlock;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MiscEnum;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.critereon.*;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootPool.Builder;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class IGBlockLootProvider implements LootTableSubProvider
{
	private final Set<ResourceKey<LootTable>> generatedTables = new HashSet<>();
	private BiConsumer<ResourceKey<LootTable>, LootTable.Builder> out;
	private final HolderLookup.Provider provider;

	public IGBlockLootProvider(HolderLookup.Provider provider)
	{
		this.provider = provider;
		this.HAS_SILK = new EnchantmentPredicate(provider.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH), MinMaxBounds.Ints.atLeast(1));
	}

	private ResourceKey<LootTable> toTableLoc(ResourceLocation in) {
		return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(in.getNamespace(), "blocks/" + in.getPath()));
	}

	@Override
	public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> out)
	{
		IGLib.IG_LOGGER.info("Started Registration of Immersive Geology Block Loot");
		this.out = out;

		registerOres();
		registerMultiblocks();
		registerSlabs();
		registerCrates();
		registerDevices();
		registerAllRemainingAsDefault();

		IGLib.IG_LOGGER.info("Finished Registration of Immersive Geology Block Loot");
	}

	private void registerOres()
	{
		for(DeferredHolder<Block, Block> block_object : IGRegistrationHolder.getBlockRegistryMap().values())
		{
			if(block_object.isBound())
			{
				Block block = block_object.get();
				if(block instanceof IOreBlock ore)
				{
					this.registerOreSpecial(block_object, ore.getItemDrop(), ore.getExtraDrops());
				}
				if(block instanceof IGCrystalBlock crystal)
				{
					this.register(block_object, this.singleItem(crystal.getItemDrop()));
				}
				if(block instanceof IGEvaporateMineralBlock mineral)
				{
					this.registerOre(block_object, mineral.getItemDrop());
				}
				continue;
			}
			IGLib.IG_LOGGER.warn("Failed to access Registry Object");
		}
	}


	private void registerMultiblocks()
	{
		this.registerMultiblock(IGMultiblockProvider.BLOOMERY);
		this.registerMultiblock(IGMultiblockProvider.BALLMILL);
		this.registerMultiblock(IGMultiblockProvider.PELLETIZER);
		this.registerMultiblock(IGMultiblockProvider.REVERBERATION_FURNACE);
		this.registerMultiblock(IGMultiblockProvider.BULK_BLAST_FURNACE);
		this.registerMultiblock(IGMultiblockProvider.ROTARYKILN);
		this.registerMultiblock(IGMultiblockProvider.CHEMICAL_REACTOR);
		this.registerMultiblock(IGMultiblockProvider.GRAVITY_SEPARATOR);
		this.registerMultiblock(IGMultiblockProvider.CRYSTALLIZER);
		this.registerMultiblock(IGMultiblockProvider.COREDRILL);
		this.registerMultiblock(IGMultiblockProvider.CENTRIFUGE);
		this.registerMultiblock(IGMultiblockProvider.SMALL_CHEMICAL_REACTOR);
		this.registerMultiblock(IGMultiblockProvider.FOUNDRY);
		this.registerMultiblock(IGMultiblockProvider.STEAM_TURBINE);
		this.registerMultiblock(IGMultiblockProvider.GEOTHERMAL_EXCHANGER);
		this.registerMultiblock(IGMultiblockProvider.ALTERNATOR);
	}

	EnchantmentPredicate HAS_SILK;

	private void registerCrates()
	{
		for(DeferredHolder<Block, ? extends Block> block : IGRegistrationHolder.getBlockRegister().getEntries())
		{
			if(block.get() instanceof IGCrateEntityType type)
			{
				if(type.getMaterial(MaterialTexture.base).equals(MiscEnum.RustyMetal))
				{
					LootTable.Builder loot = LootTable.lootTable()
							.withPool(
									dropInv().when(InvertedLootItemCondition.invert(MatchTool.toolMatches(ItemPredicate.Builder.item()
											.withSubPredicate(
													ItemSubPredicates.ENCHANTMENTS,
													ItemEnchantmentsPredicate.enchantments(List.of(HAS_SILK))))
									)))
							.withPool(
									singleItemRandomAmount(MiscEnum.RustyMetal.getItem(ItemCategoryFlags.PLATE), 1,8)
											.when(InvertedLootItemCondition.invert(MatchTool.toolMatches(ItemPredicate.Builder.item()
													.withSubPredicate(
															ItemSubPredicates.ENCHANTMENTS,
															ItemEnchantmentsPredicate.enchantments(List.of(HAS_SILK))))
											)))
							.withPool(tileDrop().when(MatchTool.toolMatches(ItemPredicate.Builder.item()
									.withSubPredicate(
											ItemSubPredicates.ENCHANTMENTS,
											ItemEnchantmentsPredicate.enchantments(List.of(HAS_SILK))))
							));

					this.register(block, loot);
				}
				else
				{
					this.register(block, tileDrop());
				}
			}
		}
	}

	private void registerSlabs() {
		for(DeferredHolder<Block, ? extends Block> block : IGRegistrationHolder.getBlockRegister().getEntries())
		{
			if(block.get() instanceof IGSlabBlock slab)
			{
				if(slab.getMaterial(MaterialTexture.base).instance().checkExistingImplementation(slab.getFlag()))
					continue;
				LootItemConditionalFunction.Builder<?> doubleSlabFunction = SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)).when(this.propertyIs(block, SlabBlock.TYPE, SlabType.DOUBLE));
				LootTable.Builder lootBuilder;
				if(slab.getMaterial(MaterialTexture.base).equals(MiscEnum.RustyMetal))
				{
					lootBuilder = LootTable.lootTable().withPool(this.singleItemRandomAmount(MiscEnum.RustyMetal.getItem(ItemCategoryFlags.PLATE), 1, 2).apply(doubleSlabFunction));
				}
				else
				{
					lootBuilder = LootTable.lootTable().withPool(this.singleItem(slab).apply(doubleSlabFunction));
				}

				this.register(block, lootBuilder);
			}
		}
	}

	private void registerAllRemainingAsDefault() {
		for(DeferredHolder<Block, ? extends Block> b : IGRegistrationHolder.getBlockRegister().getEntries())
		{
			if(b.get() instanceof IGBlockType block)
			{
				if(!this.generatedTables.contains(this.toTableLoc(b.getId())))
				{
					if(block.getMaterial(MaterialTexture.base) == null )
					{
						this.registerSelfDropping(b);
						continue;
					}
					if(block.getMaterial(MaterialTexture.base).instance().checkExistingImplementation(block.getFlag()))
						continue;
					this.registerSelfDropping(b);
				}
			}
		}
	}


	/**
	 * Blocks that are neither ores nor multiblocks nor built from a material, so
	 * {@link #registerAllRemainingAsDefault()} - which only looks at {@link IGBlockType} - never reaches them.
	 * Without an entry here they simply vanish when mined.
	 */
	private void registerDevices() {
		// Both halves are the same block, so the drop is pinned to the lower one; otherwise breaking either half
		// would hand back two detectors.
		DeferredHolder<Block, Block> detector = IGRegistrationHolder.getBlockRegistryMap()
				.get(IGRegistrationHolder.METAL_DETECTOR_KEY);
		// Charge survives being picked up: the item hands the same tag straight back to the block entity when it
		// is placed again, so a detector is never wiped by moving it.
		this.register(detector, LootTable.lootTable().withPool(this.singleItem(detector.get().asItem())
				.apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
						.include(DataComponents.BLOCK_ENTITY_DATA))
				.when(this.propertyIs(detector, IGMetalDetector.HALF, DoubleBlockHalf.LOWER))));
	}

	private void registerMultiblock(MultiblockRegistration<?> registration) {
		this.registerMultiblock(registration.block());
	}

	private void registerMultiblock(Supplier<? extends Block> b) {
		this.register(b, this.dropInv(), this.dropOriginalBlock());
	}

	private LootPool.Builder dropInv() {
		return this.createPoolBuilder().add(DropInventoryLootEntry.builder());
	}

	private LootPool.Builder tileDrop() {
		return this.createPoolBuilder().add(BEDropLootEntry.builder());
	}

	private LootPool.Builder dropOriginalBlock() {
		return this.createPoolBuilder().add(LootUtils.getMultiblockDropBuilder());
	}

	private void register(Supplier<? extends Block> b, LootPool.Builder... pools) {
		LootTable.Builder builder = LootTable.lootTable();
		LootPool.Builder[] var4 = pools;
		int var5 = pools.length;

		for(int var6 = 0; var6 < var5; ++var6) {
			LootPool.Builder pool = var4[var6];
			builder.withPool(pool);
		}

		this.register(b, builder);
	}

	private void register(Supplier<? extends Block> b, LootTable.Builder table) {
		this.register(BuiltInRegistries.BLOCK.getKey((Block)b.get()), table);
	}

	private void register(ResourceLocation name, LootTable.Builder table) {
		ResourceKey<LootTable> loc = this.toTableLoc(name);
		if (!this.generatedTables.add(loc)) {
			throw new IllegalStateException("Duplicate loot table " + name);
		} else {
			this.out.accept(loc, table.setParamSet(LootContextParamSets.BLOCK));
		}
	}

	private void registerSelfDropping(Supplier<? extends Block> b, LootPool.Builder... pool) {
		LootPool.Builder[] withSelf = Arrays.copyOf(pool, pool.length + 1);
		withSelf[withSelf.length - 1] = this.singleItem(b.get().asItem());
		this.register(b, withSelf);
	}

	private LootTable.Builder dropProvider(ItemLike in) {
		return LootTable.lootTable().withPool(this.singleItem(in));
	}

	private LootPool.Builder singleItem(ItemLike in) {
		return this.createPoolBuilder().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(in));
	}

	private LootPool.Builder singleItemRandomAmount(ItemLike in, int min, int max) {
		return this.createPoolBuilder()
				.setRolls(ConstantValue.exactly(1.0F))
				.add(LootItem.lootTableItem(in)
						.apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max))));
	}

	private LootPool.Builder createPoolBuilder() {
		return LootPool.lootPool().when(ExplosionCondition.survivesExplosion());
	}

	private void registerOre(Supplier<Block> ore, ItemStack rawOre) {
		final var enchantments = this.provider.lookupOrThrow(Registries.ENCHANTMENT);
		LootPool.Builder pool_builder = LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F));
		pool_builder.add((LootItem.lootTableItem(rawOre.getItem())
				.when(MatchTool.toolMatches(net.minecraft.advancements.critereon.ItemPredicate.Builder.item()
						.withSubPredicate(
								ItemSubPredicates.ENCHANTMENTS,
								ItemEnchantmentsPredicate.enchantments(
										List.of(new EnchantmentPredicate(enchantments.getOrThrow(Enchantments.SILK_TOUCH), Ints.atLeast(1)))))
				)))
				.otherwise(LootItem.lootTableItem(rawOre.getItem())
						.apply(SetItemCountFunction.setCount(ConstantValue.exactly(rawOre.getCount())))
						.apply(ApplyBonusCount.addOreBonusCount(enchantments.getOrThrow(Enchantments.FORTUNE)))
						.apply(ApplyExplosionDecay.explosionDecay())));

		LootTable.Builder ret = LootTable.lootTable()
				.withPool(pool_builder);
		this.register(ore, ret);
	}

	private void registerOreSpecial(Supplier<Block> ore, ItemStack rawOre, List<Pair<ItemStack, Float>> extras) {
		final var enchantments = this.provider.lookupOrThrow(Registries.ENCHANTMENT);
		LootPool.Builder pool_builder = LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F));
		pool_builder.add((LootItem.lootTableItem(rawOre.getItem())
				.when(MatchTool.toolMatches(net.minecraft.advancements.critereon.ItemPredicate.Builder.item()
						.withSubPredicate(
								ItemSubPredicates.ENCHANTMENTS,
								ItemEnchantmentsPredicate.enchantments(
										List.of(new EnchantmentPredicate(enchantments.getOrThrow(Enchantments.SILK_TOUCH), Ints.atLeast(1)))))
				)))
				.otherwise(LootItem.lootTableItem(rawOre.getItem())
						.apply(SetItemCountFunction.setCount(ConstantValue.exactly(rawOre.getCount())))
						.apply(ApplyBonusCount.addOreBonusCount(enchantments.getOrThrow(Enchantments.FORTUNE)))
						.apply(ApplyExplosionDecay.explosionDecay())));

		for(Pair<ItemStack, Float> entry : extras)
		{
			ItemStack item = entry.getFirst();

			float chance = entry.getSecond();
			pool_builder.add(LootItem.lootTableItem(item.getItem())
					.apply(SetItemCountFunction.setCount(ConstantValue.exactly(item.getCount())))
					.when(LootItemRandomChanceCondition.randomChance(chance))  // Chance from the extras
			);
		}

		LootTable.Builder ret = LootTable.lootTable()
				.withPool(pool_builder);
		this.register(ore, ret);
	}

	private LootPool.Builder binBonusLootPool(ItemLike item, Holder<Enchantment> ench, float prob, int extra) {
		return this.createPoolBuilder().add(LootItem.lootTableItem(item)).apply(ApplyBonusCount.addBonusBinomialDistributionCount(ench, prob, extra));
	}

	private <T extends Comparable<T> & StringRepresentable> LootItemCondition.Builder propertyIs(Supplier<? extends Block> b, Property<T> prop, T value) {
		return LootItemBlockStatePropertyCondition.hasBlockStateProperties((Block)b.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(prop, value));
	}
}
