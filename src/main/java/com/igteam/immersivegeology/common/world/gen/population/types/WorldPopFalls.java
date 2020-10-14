package com.igteam.immersivegeology.common.world.gen.population.types;

import com.igteam.immersivegeology.common.world.chunk.ChunkGeneratorImmersiveOverworld;
import com.igteam.immersivegeology.common.world.help.IGBlockHelpers;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class WorldPopFalls implements IWorldGenerator {
    private final BlockState block;
    private final int rarity;

    public WorldPopFalls(BlockState block, int rarity){
        this.block = block;
        this.rarity = rarity;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, ChunkGenerator chunkGenerator, AbstractChunkProvider chunkProvider) {
        for (int k5 = 0; k5 < rarity; ++k5)
        {
            int x = random.nextInt(16) + 8;
            int z = random.nextInt(16) + 8;
            int y = random.nextInt(ChunkGeneratorImmersiveOverworld.SEA_LEVEL - 50) + 30;
            BlockPos pos = new BlockPos(chunkX << 4, y, chunkZ << 4).add(x, 0, z);
            if (!IGBlockHelpers.isRawStone(world.getBlockState(pos.down())) && !IGBlockHelpers.isRawStone(world.getBlockState(pos.up())) && (!IGBlockHelpers.isRawStone(world.getBlockState(pos)) || !world.isAirBlock(pos)))
            {
                continue;
            }
            int rawHorizontal = 0, airHorizontal = 0;
            for (Direction facing : Direction.values())
            {
                if(!(facing == Direction.UP || facing == Direction.DOWN)) {
                    if (world.isAirBlock(pos.offset(facing))) {
                        airHorizontal++;
                    } else if (IGBlockHelpers.isRawStone(world.getBlockState(pos.offset(facing)))) {
                        rawHorizontal++;
                    }
                    if (airHorizontal > 1) break;
                }
            }
            if (rawHorizontal == 3 && airHorizontal == 1)
            {
                world.setBlockState(pos, block, 2);
            }
        }
    }
}
