package com.igteam.immersive_geology.core.data.generators.loot;

import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.core.data.generators.helpers.OreDropProperty;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.RandomValueRange;

public class BlockLootTableProvider extends BlockLootTables {

    @Override
    protected void addTables() {

        generateOreDrop(MaterialEnum.Vanilla.getMaterial());

//        for(MaterialUseType useType : MaterialUseType.values()){
//            for(MaterialEnum container : MaterialEnum.values()){
//                Material material = container.getMaterial();
//                if(material.hasSubtype(useType)){
//                    generateBlockDrop(useType, material);
//                }
//            }
//        }
    }

    private void generateBlockDrop(MaterialUseType useType, Material material) {
        switch (useType){
//            case STONE:
//                generateOreDrop(material);
//                this.registerDropSelfLootTable(IGRegistrationHolder.getBlockByMaterial(useType, material));
//                break;
            default:
                this.registerDropSelfLootTable(IGRegistrationHolder.getBlockByMaterial(useType, material));
        }
    }

    private void generateOreDrop(Material base_material) {
        MaterialUseType ore_stone = MaterialUseType.ORE_STONE;
        for(MaterialEnum container : MaterialEnum.values()){
            Material ore_material = container.getMaterial();
            if(ore_material.hasSubtype(ore_stone)){
                Block oreBlock = IGRegistrationHolder.getBlockByMaterial(base_material, ore_material, ore_stone);
                Item oreChunk = Items.COAL;//IGRegistrationHolder.getItemByMaterial(base_material, ore_material, MaterialUseType.ORE_CHUNK);
                this.registerLootTable(oreBlock, (block) -> LootTable.builder()
                    .addLootPool(LootPool.builder()
                        .rolls(RandomValueRange.of(1F,1F))
                        .addEntry(ItemLootEntry.builder(oreChunk)
                        .acceptFunction(OreDropProperty.builder()))
                    )
                );
            }
        }
    }
}
