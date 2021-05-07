package com.igteam.immersive_geology.core.data.generators.loot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.common.block.IGOreBlock;
import com.igteam.immersive_geology.common.block.helpers.BlockMaterialType;
import com.igteam.immersive_geology.common.block.helpers.IGBlockType;
import com.igteam.immersive_geology.core.data.generators.helpers.OreDropProperty;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.MatchTool;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BlockLootProvider implements IDataProvider {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    //Allow us to use SILK TOUCH as a condition, must be at least level 1 or higher!
    private static final ILootCondition.IBuilder SILK_TOUCH = MatchTool.builder(ItemPredicate.Builder.create()
            .enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))));

    private final DataGenerator generator;
    private final Map<Block, Function<Block, LootTable.Builder>> functionTable = new HashMap<>();

    public BlockLootProvider(DataGenerator generator){
        this.generator = generator;

        //Loop through block registry
        for (Block b : Registry.BLOCK) {
            ResourceLocation id = Registry.BLOCK.getKey(b);
            if(!IGLib.MODID.equals(id.getNamespace())){
                continue; // skip over this block in the loop if it isn't from our mod!
            }

            if(b instanceof IGOreBlock){
                IGOreBlock oreBlock = ((IGOreBlock) b);
                Item stoneChunk = IGRegistrationHolder.getItemByMaterial(oreBlock.getMaterial(BlockMaterialType.BASE_MATERIAL), MaterialUseType.CHUNK);
                Item oreChunk = IGRegistrationHolder.getItemByMaterial(oreBlock.getMaterial(BlockMaterialType.BASE_MATERIAL), oreBlock.getMaterial(BlockMaterialType.ORE_MATERIAL), MaterialUseType.ORE_CHUNK);
                functionTable.put(b, (block) -> LootTable.builder()
                        .addLootPool(LootPool.builder()
                                .rolls(RandomValueRange.of(1F, 1F))
                                .addEntry(ItemLootEntry.builder(oreChunk)
                                        .acceptFunction(OreDropProperty.builder()))
                        )
                );
            } else if(b instanceof IGBlockType){
                IGBlockType blockType = (IGBlockType) b;

                Item itemDrop = IGRegistrationHolder.getItemByMaterial(((IGBlockType) b).getMaterial(BlockMaterialType.BASE_MATERIAL), ((IGBlockType) b).getDropUseType());
                if(itemDrop != null && ((IGBlockType) b).getDropUseType() != ((IGBlockType) b).getBlockUseType()) {
                    functionTable.put(b, (block) -> LootTable.builder()
                            .addLootPool(LootPool.builder()
                                    .rolls(RandomValueRange.of(blockType.minDrops(), (blockType).maxDrops()))
                                    .addEntry(ItemLootEntry.builder(itemDrop))));
                } else {
                    functionTable.put(b, BlockLootProvider::genRegular);
                }
            }
        }
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        Map<ResourceLocation, LootTable.Builder> tables = new HashMap<>();

        for (Block b : Registry.BLOCK) {
            ResourceLocation id = Registry.BLOCK.getKey(b);
            if(!IGLib.MODID.equals(id.getNamespace())){
                continue; // skip over this block in the loop if it isn't from our mod!
            }
            Function<Block, LootTable.Builder> func = functionTable.getOrDefault(b, BlockLootProvider::genRegular);
            tables.put(id, func.apply(b));
        }

        for (Map.Entry<ResourceLocation, LootTable.Builder> e : tables.entrySet()) {
            Path path = getPath(generator.getOutputFolder(), e.getKey());
            IDataProvider.save(GSON, cache, LootTableManager.toJson(e.getValue().setParameterSet(LootParameterSets.BLOCK).build()), path);
        }
    }

    private static Path getPath(Path root, ResourceLocation id) {
        return root.resolve("data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json");
    }

    private static LootTable.Builder empty(Block b) {
        return LootTable.builder();
    }

    private static LootTable.Builder genRegular(Block b) {
        LootEntry.Builder<?> entry = ItemLootEntry.builder(b);
        LootPool.Builder pool = LootPool.builder().name("main").rolls(ConstantRange.of(1)).addEntry(entry)
                .acceptCondition(SurvivesExplosion.builder());
        return LootTable.builder().addLootPool(pool);
    }

    @Nonnull
    @Override
    public String getName() {
        return "Immersive Geology block loot tables";
    }
}
