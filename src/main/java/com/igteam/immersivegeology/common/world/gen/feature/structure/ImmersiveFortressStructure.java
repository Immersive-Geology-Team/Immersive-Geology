package com.igteam.immersivegeology.common.world.gen.feature.structure;


import com.google.common.collect.Lists;
import com.igteam.immersivegeology.common.world.gen.feature.IGFeatures;
import com.mojang.datafixers.Dynamic;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class ImmersiveFortressStructure extends Structure<NoFeatureConfig> {
    private static final List<Biome.SpawnListEntry> field_202381_d;

    public ImmersiveFortressStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51476_1_) {
        super(p_i51476_1_);
    }

    public boolean hasStartAt(ChunkGenerator<?> p_202372_1_, Random p_202372_2_, int p_202372_3_, int p_202372_4_) {
        int lvt_5_1_ = p_202372_3_ >> 4;
        int lvt_6_1_ = p_202372_4_ >> 4;
        p_202372_2_.setSeed((long)(lvt_5_1_ ^ lvt_6_1_ << 4) ^ p_202372_1_.getSeed());
        p_202372_2_.nextInt();
        if (p_202372_2_.nextInt(3) != 0) {
            return false;
        } else if (p_202372_3_ != (lvt_5_1_ << 4) + 4 + p_202372_2_.nextInt(8)) {
            return false;
        } else if (p_202372_4_ != (lvt_6_1_ << 4) + 4 + p_202372_2_.nextInt(8)) {
            return false;
        } else {
            Biome lvt_7_1_ = p_202372_1_.getBiomeProvider().getBiome(new BlockPos((p_202372_3_ << 4) + 9, 0, (p_202372_4_ << 4) + 9));
            return p_202372_1_.hasStructure(lvt_7_1_, IGFeatures.IMMERSIVE_NETHER_BRIDGE);
        }
    }

    public IStartFactory getStartFactory() {
        return ImmersiveFortressStructure.Start::new;
    }

    public String getStructureName() {
        return "Immersive_Fortress";
    }

    public int getSize() {
        return 8;
    }

    public List<Biome.SpawnListEntry> getSpawnList() {
        return field_202381_d;
    }

    static {
        field_202381_d = Lists.newArrayList(new Biome.SpawnListEntry[]{new Biome.SpawnListEntry(EntityType.BLAZE, 10, 2, 3), new Biome.SpawnListEntry(EntityType.ZOMBIE_PIGMAN, 5, 4, 4), new Biome.SpawnListEntry(EntityType.WITHER_SKELETON, 8, 5, 5), new Biome.SpawnListEntry(EntityType.SKELETON, 2, 5, 5), new Biome.SpawnListEntry(EntityType.MAGMA_CUBE, 3, 4, 4)});
    }

    public static class Start extends StructureStart {
        public Start(Structure<?> p_i49949_1_, int p_i49949_2_, int p_i49949_3_, Biome p_i49949_4_, MutableBoundingBox p_i49949_5_, int p_i49949_6_, long p_i49949_7_) {
            super(p_i49949_1_, p_i49949_2_, p_i49949_3_, p_i49949_4_, p_i49949_5_, p_i49949_6_, p_i49949_7_);
        }

        public void init(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_) {
            net.minecraft.world.gen.feature.structure.FortressPieces.Start lvt_6_1_ = new net.minecraft.world.gen.feature.structure.FortressPieces.Start(this.rand, (p_214625_3_ << 4) + 2, (p_214625_4_ << 4) + 2);
            this.components.add(lvt_6_1_);
            lvt_6_1_.buildComponent(lvt_6_1_, this.components, this.rand);
            List lvt_7_1_ = lvt_6_1_.pendingChildren;

            while(!lvt_7_1_.isEmpty()) {
                int lvt_8_1_ = this.rand.nextInt(lvt_7_1_.size());
                StructurePiece lvt_9_1_ = (StructurePiece)lvt_7_1_.remove(lvt_8_1_);
                lvt_9_1_.buildComponent(lvt_6_1_, this.components, this.rand);
            }

            this.recalculateStructureSize();
            this.func_214626_a(this.rand, 90, 125);
        }
    }
}

