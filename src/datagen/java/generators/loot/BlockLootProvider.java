package generators.loot;

import blusunrize.immersiveengineering.common.util.loot.DropInventoryLootEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import igteam.api.main.IGMultiblockProvider;
import igteam.api.materials.MineralEnum;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.helper.MaterialTexture;
import igteam.api.materials.pattern.BlockPattern;
import igteam.api.materials.pattern.ItemPattern;
import igteam.immersive_geology.common.block.IGGenericBlock;
import igteam.immersive_geology.common.block.blocks.IGOreBlock;
import igteam.immersive_geology.common.loot.ChunkDropProperty;
import igteam.immersive_geology.common.loot.DummyOreDropProperty;
import igteam.immersive_geology.common.loot.OreDropProperty;
import igteam.immersive_geology.common.loot.SecondaryOreDropProperty;
import igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.MatchTool;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

//I feel bad for this, but either this or copy-paste class. My apologies, IE team, ~UnSchtalch
import blusunrize.immersiveengineering.common.util.loot.MBOriginalBlockLootEntry;

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

            if(b instanceof IGOreBlock) {
                IGOreBlock ore = (IGOreBlock) b;
                MaterialInterface<?> stoneMaterial = ore.getMaterial(MaterialTexture.base);
                MaterialInterface<?> oreMaterial = ore.getMaterial(MaterialTexture.overlay);
                Item stoneChunk = stoneMaterial.getItem(ItemPattern.stone_chunk);
                if (oreMaterial.instance().isSalt())
                {
                    Item crystal = oreMaterial.getItem(ItemPattern.crystal);
                    Item dust = oreMaterial.getItem(ItemPattern.dust);
                    Block oreBlock = stoneMaterial.getBlock(BlockPattern.ore, ore.getMaterial(MaterialTexture.overlay));
                    functionTable.put(b, (block) -> LootTable.builder()
                                    .addLootPool(LootPool.builder()
                                            .rolls(RandomValueRange.of(1f,1f))
                                            .addEntry(ItemLootEntry.builder(oreBlock)
                                                    .acceptFunction(DummyOreDropProperty.builder()))
                                    ).addLootPool(LootPool.builder()
                                            .rolls(RandomValueRange.of(1F, 1F))
                                            .addEntry(ItemLootEntry.builder(crystal)
                                                    .acceptFunction(SecondaryOreDropProperty.builder()))
                                    ).addLootPool(LootPool.builder()
                                            .rolls(RandomValueRange.of(.5F, 1F))
                                            .addEntry(ItemLootEntry.builder(dust)
                                                    .acceptFunction(SecondaryOreDropProperty.builder()))
                                    )
                                    .addLootPool(LootPool.builder()
                                            .rolls(RandomValueRange.of(1F, 1F))
                                            .addEntry(ItemLootEntry.builder(stoneChunk)
                                                    .acceptFunction(ChunkDropProperty.builder()))
                                    )
                    );
                } else {

                    Item oreChunk = stoneMaterial.getItem(ItemPattern.ore_chunk, ore.getMaterial(MaterialTexture.overlay));
                    Item oreBit = stoneMaterial.getItem(ItemPattern.ore_bit, ore.getMaterial(MaterialTexture.overlay));

                    functionTable.put(b, (block) -> LootTable.builder()
                                    .addLootPool(LootPool.builder()
                                            .rolls(RandomValueRange.of(1F, 1F))
                                            .addEntry(ItemLootEntry.builder(oreChunk)
                                                    .acceptFunction(OreDropProperty.builder()))
                                    ).addLootPool(LootPool.builder()
                                            .rolls(RandomValueRange.of(.5F, 1F))
                                            .addEntry(ItemLootEntry.builder(oreBit)
                                                    .acceptFunction(SecondaryOreDropProperty.builder()))
                                    )
                                    .addLootPool(LootPool.builder()
                                            .rolls(RandomValueRange.of(1F, 1F))
                                            .addEntry(ItemLootEntry.builder(stoneChunk)
                                                    .acceptFunction(ChunkDropProperty.builder()))
                                    )
                    );
                }
            }


            if(b instanceof IGGenericBlock){
                IGGenericBlock genericBlock = (IGGenericBlock) b;
                BlockPattern pattern = genericBlock.getPattern();
                if(pattern == BlockPattern.storage){
                    if(genericBlock.getMaterial(MaterialTexture.base).getName().equals( MineralEnum.Kaolinite.getName())){
                        Item clay = MineralEnum.Kaolinite.getItem(ItemPattern.clay);
                        functionTable.put(b, (block) -> LootTable.builder()
                                .addLootPool(LootPool.builder()
                                        .rolls(RandomValueRange.of(1F, 1F))
                                        .addEntry(ItemLootEntry.builder(clay)
                                                .acceptFunction(SetCount.builder(RandomValueRange.of(4.0F, 4.0F))))
                                )
                        );
                    }
                }
            }

        registerMultiblockDrop(IGMultiblockProvider.chemicalvat);
        registerMultiblockDrop(IGMultiblockProvider.crystallizer);
        registerMultiblockDrop(IGMultiblockProvider.rotarykiln);
        registerMultiblockDrop(IGMultiblockProvider.hydrojet_cutter);
        registerMultiblockDrop(IGMultiblockProvider.reverberation_furnace);
//                        functionTable.put(b, BlockLootProvider::genRegular);

        }
    }

    private LootPool.Builder createPoolBuilder() {
        return LootPool.builder().acceptCondition(SurvivesExplosion.builder());
    }

    private LootPool.Builder dropOriginalBlock() {
        return this.createPoolBuilder().addEntry(MBOriginalBlockLootEntry.builder());
    }
    private LootPool.Builder dropInv() {
        return this.createPoolBuilder().addEntry(DropInventoryLootEntry.builder());
    }

    private void registerMultiblockDrop(Block b)
    {
        LootTable.Builder builder = LootTable.builder();
        builder.addLootPool(dropOriginalBlock());
        builder.addLootPool(dropInv());
        functionTable.put(b, (block) -> builder);
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
