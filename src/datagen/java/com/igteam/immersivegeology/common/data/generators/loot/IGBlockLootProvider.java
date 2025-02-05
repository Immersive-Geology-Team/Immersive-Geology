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
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.igteam.immersivegeology.common.block.*;
import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds.Ints;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.simpleBuilder;

public class IGBlockLootProvider implements LootTableSubProvider
{
	private final Set<ResourceLocation> generatedTables = new HashSet();
	private BiConsumer<ResourceLocation, LootTable.Builder> out;

	private ResourceLocation toTableLoc(ResourceLocation in) {
		return new ResourceLocation(in.getNamespace(), "blocks/" + in.getPath());
	}

	@Override
	public void generate(BiConsumer<ResourceLocation, LootTable.Builder> out)
	{
		IGLib.IG_LOGGER.info("Started Registration of Immersive Geology Block Loot");
		this.out = out;

		registerOres();
		registerMultiblocks();
		registerSlabs();
		registerAllRemainingAsDefault();

		IGLib.IG_LOGGER.info("Finished Registration of Immersive Geology Block Loot");
	}

	private void registerOres()
	{
		for(RegistryObject<Block> block_object : IGRegistrationHolder.getBlockRegistryMap().values())
		{
			if(block_object.isPresent())
			{
				Block block = block_object.get();
				if(block instanceof IOreBlock ore)
				{
					this.registerOre(block_object, ore.getItemDrop());
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
		this.registerMultiblock(IGMultiblockProvider.REVERBERATION_FURNACE);
		this.registerMultiblock(IGMultiblockProvider.ROTARYKILN);
		this.registerMultiblock(IGMultiblockProvider.CHEMICAL_REACTOR);
		this.registerMultiblock(IGMultiblockProvider.GRAVITY_SEPARATOR);
		this.registerMultiblock(IGMultiblockProvider.CRYSTALLIZER);
	}

	private void registerSlabs() {
		Iterator<RegistryObject<Block>> iterator = IGRegistrationHolder.getBlockRegister().getEntries().iterator();

		while(iterator.hasNext())
		{
			RegistryObject<Block> block = iterator.next();
			if(block.get() instanceof IGSlabBlock slab)
			{
				if(slab.getMaterial(MaterialTexture.base).instance().checkExistingImplementation(slab.getFlag())) continue;
				LootItemConditionalFunction.Builder<?> doubleSlabFunction = SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)).when(this.propertyIs(block, SlabBlock.TYPE, SlabType.DOUBLE));
				LootTable.Builder lootBuilder = LootTable.lootTable().withPool(this.singleItem(slab).apply(doubleSlabFunction));
				this.register(block, lootBuilder);
			}
		}
	}

	private void registerAllRemainingAsDefault() {
		for(RegistryObject<Block> b : IGRegistrationHolder.getBlockRegister().getEntries())
		{
			if(b.get() instanceof IGGenericBlock block)
			{
				if(!this.generatedTables.contains(this.toTableLoc(b.getId())))
				{
					if(block.getMaterial(MaterialTexture.base).instance().checkExistingImplementation(block.getFlag()))
						continue;
					this.registerSelfDropping(b);
				}
			}
		}
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
		ResourceLocation loc = this.toTableLoc(name);
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

	private LootPool.Builder createPoolBuilder() {
		return LootPool.lootPool().when(ExplosionCondition.survivesExplosion());
	}

	private void registerOre(Supplier<Block> ore, ItemLike rawOre) {
		LootPool.Builder pool_builder = LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F));
		pool_builder.add((LootItem.lootTableItem(rawOre)
						.when(MatchTool.toolMatches(net.minecraft.advancements.critereon.ItemPredicate.Builder.item()
								.hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, Ints.atLeast(1))))))
						.otherwise(LootItem.lootTableItem(rawOre).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))
								.apply(ApplyExplosionDecay.explosionDecay())));

		LootTable.Builder ret = LootTable.lootTable()
				.withPool(pool_builder);

		this.register(ore, ret);
	}

	private LootPool.Builder binBonusLootPool(ItemLike item, Enchantment ench, float prob, int extra) {
		return this.createPoolBuilder().add(LootItem.lootTableItem(item)).apply(ApplyBonusCount.addBonusBinomialDistributionCount(ench, prob, extra));
	}

	private <T extends Comparable<T> & StringRepresentable> LootItemCondition.Builder propertyIs(Supplier<? extends Block> b, Property<T> prop, T value) {
		return LootItemBlockStatePropertyCondition.hasBlockStateProperties((Block)b.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(prop, value));
	}
}
