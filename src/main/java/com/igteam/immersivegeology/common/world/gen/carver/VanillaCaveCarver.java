package com.igteam.immersivegeology.common.world.gen.carver;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;

import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.gen.carver.builder.VanillaCaveCarverBuilder;
import com.igteam.immersivegeology.common.world.gen.carver.util.CarverUtils;
import com.igteam.immersivegeology.common.world.gen.carver.util.ColPos;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

import static com.igteam.immersivegeology.common.world.gen.carver.util.CarverUtils.isPosInWorld;

public class VanillaCaveCarver implements ICarver {
    private int
        bottomY,
        topY,
        density,
        priority,
        liquidAltitude;
    private BlockState debugBlock;
    private boolean
        isDebugVisualizerEnabled,
        isReplaceGravelEnabled,
        isFloodedUndergroundEnabled;
    private Random rand = new Random();
    private IWorld world; 

    private int range = 8;

    public VanillaCaveCarver(final VanillaCaveCarverBuilder builder) {
        this.bottomY = builder.getBottomY();
        this.topY = builder.getTopY();
        this.density = builder.getDensity();
        this.priority = builder.getPriority();
        this.liquidAltitude = builder.getLiquidAltitude();
        this.debugBlock = builder.getDebugBlock();
        this.isDebugVisualizerEnabled = builder.isDebugVisualizerEnabled();
        this.isReplaceGravelEnabled = builder.isReplaceGravel();
        this.isFloodedUndergroundEnabled = builder.isFloodedUndergroundEnabled();
        if (bottomY > topY) {
            this.bottomY = 40;
            this.topY = 128;
        }
    }

    /**
     * Calls recursiveGenerate() on all chunks within a certain square range (default 8) of this chunk.
     */
    public void generate(IWorld worldIn, int chunkX, int chunkZ, IChunk primer, boolean addRooms, BlockState[][] liquidBlocks, Map<Long, IGBiome> biomeMap, boolean[][] validPositions, BitSet airCarvingMask, BitSet liquidCarvingMask) {
        int chunkRadius = this.range;
        this.world = worldIn;
        this.rand.setSeed(worldIn.getSeed());
        long j = this.rand.nextLong();
        long k = this.rand.nextLong();
        for (int currChunkX = chunkX - chunkRadius; currChunkX <= chunkX + chunkRadius; ++currChunkX) {
            for (int currChunkZ = chunkZ - chunkRadius; currChunkZ <= chunkZ + chunkRadius; ++currChunkZ) {
                long j1 = (long) currChunkX * j;
                long k1 = (long) currChunkZ * k;
                this.rand.setSeed(j1 ^ k1 ^ worldIn.getSeed());
                this.recursiveGenerate(worldIn, currChunkX, currChunkZ, chunkX, chunkZ, primer, addRooms, liquidBlocks, biomeMap, validPositions, airCarvingMask, liquidCarvingMask);
            }
        }
    }

    public void generate(IWorld worldIn, int x, int z, IChunk primer, boolean addRooms, BlockState[][] liquidBlocks, Map<Long, IGBiome> biomeMap, BitSet airCarvingMask, BitSet liquidCarvingMask) {
        boolean[][] validPositions = new boolean[16][16];
        for (boolean[] row : validPositions)
            Arrays.fill(row, true);
        generate(worldIn, x, z, primer, addRooms, liquidBlocks, biomeMap, validPositions, airCarvingMask, liquidCarvingMask);
    }

    /**
     * Calls addTunnel and addRoom (wrapper for addTunnel) for this chunk.
     * Note that each call to this function (and subsequently addTunnel) will be done with the same rand seed.
     * This means that when a chunk is checked multiple times by different neighbor chunks, each time it will be processed
     * the same way, ensuring the tunnels are always consistent and connecting.
     */
    private void recursiveGenerate(IWorld worldIn, int chunkX, int chunkZ, int originalChunkX, int originalChunkZ, @Nonnull IChunk primer, boolean addRooms, BlockState[][] liquidBlocks, Map<Long, IGBiome> biomeMap, boolean[][] validPositions, BitSet airCarvingMask, BitSet liquidCarvingMask) {
        int numAttempts = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(15) + 1) + 1);

        if (this.rand.nextInt(100) > this.density) {
            numAttempts = 0;
        }

        for (int i = 0; i < numAttempts; ++i) {
            double caveStartX = chunkX * 16 + this.rand.nextInt(16);
            double caveStartY = this.rand.nextInt(this.topY - this.bottomY) + this.bottomY;
            double caveStartZ = chunkZ * 16 + this.rand.nextInt(16);

            int numAddTunnelCalls = 1;

            if (addRooms && this.rand.nextInt(4) == 0) {
                this.addRoom(worldIn, this.rand.nextLong(), originalChunkX, originalChunkZ, primer, caveStartX, caveStartY, caveStartZ, liquidBlocks, biomeMap, validPositions, airCarvingMask, liquidCarvingMask);
                numAddTunnelCalls += this.rand.nextInt(4);
            }

            for (int j = 0; j < numAddTunnelCalls; ++j) {
                float yaw = this.rand.nextFloat() * ((float) Math.PI * 2F);
                float pitch = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float width = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();

                // Chance of wider caves.
                // Although not actually related to adding rooms, I perform an addRoom check here
                // to avoid the chance of really large caves when generating surface caves.
                if (addRooms && this.rand.nextInt(10) == 0) {
                    width *= this.rand.nextFloat() * this.rand.nextFloat() * 3.0F + 1.0F;
                }

                this.addTunnel(worldIn, this.rand.nextLong(), originalChunkX, originalChunkZ, primer, caveStartX, caveStartY, caveStartZ, width, yaw, pitch, 0, 0, 1.0D, liquidBlocks, biomeMap, validPositions, airCarvingMask, liquidCarvingMask);
            }
        }
    }

    public int getPriority() {
        return this.priority;
    }

    @Override
    public int getTopY() {
        return this.topY;
    }


    private void addRoom(IWorld worldIn, long seed, int originChunkX, int originChunkZ, IChunk primer, double caveStartX, double caveStartY, double caveStartZ, BlockState[][] liquidBlocks, Map<Long, IGBiome> biomeMap, boolean[][] validPositions, BitSet airCarvingMask, BitSet liquidCarvingMask) {
        this.addTunnel(worldIn, seed, originChunkX, originChunkZ, primer, caveStartX, caveStartY, caveStartZ, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5D, liquidBlocks, biomeMap, validPositions, airCarvingMask, liquidCarvingMask);
    }

    protected void addTunnel(IWorld worldIn, long seed, int originChunkX, int originChunkZ, IChunk chunk, double caveStartX, double caveStartY, double caveStartZ, float width, float yaw, float pitch, int startCounter, int endCounter, double heightModifier, BlockState[][] liquidBlocks, Map<Long, IGBiome> biomeMap, boolean[][] validPositions, BitSet airCarvingMask, BitSet liquidCarvingMask) {
        BlockState liquidBlock;
        Random random = new Random(seed);

        // Center block of the origin chunk
        double originBlockX = (originChunkX * 16 + 8);
        double originBlockZ = (originChunkZ * 16 + 8);

        // Variables to slightly change the yaw/pitch for each iteration in the while loop below.
        float yawModifier = 0.0F;
        float pitchModifier = 0.0F;

        // Raw calls to addTunnel from recursiveGenerate give startCounter and endCounter a value of 0.
        // Calls from addRoom make them both -1.

        // This appears to be called regardless of where addTunnel was called from.
        if (endCounter <= 0) {
            int i = this.range * 16 - 16;
            endCounter = i - random.nextInt(i / 4);
        }

        // Whether or not this function call was made from addRoom.
        boolean comesFromRoom = false;

        // Only called if the function call came from addRoom.
        // If this call came from addRoom, startCounter is set to halfway to endCounter.
        // If this is a raw call from recursiveGenerate, startCounter will be zero.
        if (startCounter == -1) {
            startCounter = endCounter / 2;
            comesFromRoom = true;
        }

        int randomCounterValue = random.nextInt(endCounter / 2) + endCounter / 4;

        // Loops one block at a time to the endCounter (about 6-7 chunks away on average).
        // startCounter starts at either zero or endCounter / 2.
        while (startCounter < endCounter) {
            // Appears to change how wide caves are. Value will be between 1.5 and 1.5 + width.
            // Note that caves will become wider toward the middle, and close off on the ends.
            double xzOffset = 1.5D + (double) (MathHelper.sin((float) startCounter * (float) Math.PI / (float) endCounter) * width);

            // Appears to just be a linear modifier for the cave height
            double yOffset = xzOffset * heightModifier;

            float pitchXZ = MathHelper.cos(pitch);
            float pitchY = MathHelper.sin(pitch);
            caveStartX += MathHelper.cos(yaw) * pitchXZ;
            caveStartY += pitchY;
            caveStartZ += MathHelper.sin(yaw) * pitchXZ;

            boolean flag = random.nextInt(6) == 0;
            if (flag) {
                pitch = pitch * 0.92F;
            } else {
                pitch = pitch * 0.7F;
            }

            pitch = pitch + pitchModifier * 0.1F;
            yaw += yawModifier * 0.1F;

            pitchModifier = pitchModifier * 0.9F;
            yawModifier = yawModifier * 0.75F;

            pitchModifier = pitchModifier + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            yawModifier = yawModifier + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

            if (!comesFromRoom && startCounter == randomCounterValue && width > 1.0F && endCounter > 0) {
                this.addTunnel(worldIn, random.nextLong(), originChunkX, originChunkZ, chunk, caveStartX, caveStartY, caveStartZ, random.nextFloat() * 0.5F + 0.5F, yaw - ((float) Math.PI / 2F), pitch / 3.0F, startCounter, endCounter, 1.0D, liquidBlocks, biomeMap, validPositions, airCarvingMask, liquidCarvingMask);
                this.addTunnel(worldIn, random.nextLong(), originChunkX, originChunkZ, chunk, caveStartX, caveStartY, caveStartZ, random.nextFloat() * 0.5F + 0.5F, yaw + ((float) Math.PI / 2F), pitch / 3.0F, startCounter, endCounter, 1.0D, liquidBlocks, biomeMap, validPositions, airCarvingMask, liquidCarvingMask);
                return;
            }

            if (comesFromRoom || random.nextInt(4) != 0) {
                double caveStartXOffsetFromCenter = caveStartX - originBlockX; // Number of blocks from current caveStartX to center of origin chunk
                double caveStartZOffsetFromCenter = caveStartZ - originBlockZ; // Number of blocks from current caveStartZ to center of origin chunk
                double distanceToEnd = endCounter - startCounter;
                double d7 = width + 2.0F + 16.0F;

                // I think this prevents caves from generating too far from the origin chunk
                if (caveStartXOffsetFromCenter * caveStartXOffsetFromCenter + caveStartZOffsetFromCenter * caveStartZOffsetFromCenter - distanceToEnd * distanceToEnd > d7 * d7) {
                    return;
                }

                // Only continue if cave start is close enough to origin
                if (caveStartX >= originBlockX - 16.0D - xzOffset * 2.0D && caveStartZ >= originBlockZ - 16.0D - xzOffset * 2.0D && caveStartX <= originBlockX + 16.0D + xzOffset * 2.0D && caveStartZ <= originBlockZ + 16.0D + xzOffset * 2.0D) {
                    int minX = MathHelper.floor(caveStartX - xzOffset) - originChunkX * 16 - 1;
                    int minY = MathHelper.floor(caveStartY - yOffset) - 1;
                    int minZ = MathHelper.floor(caveStartZ - xzOffset) - originChunkZ * 16 - 1;
                    int maxX = MathHelper.floor(caveStartX + xzOffset) - originChunkX * 16 + 1;
                    int maxY = MathHelper.floor(caveStartY + yOffset) + 1;
                    int maxZ = MathHelper.floor(caveStartZ + xzOffset) - originChunkZ * 16 + 1;

                    if (minX < 0) {
                        minX = 0;
                    }

                    if (maxX > 16) {
                        maxX = 16;
                    }

                    if (minY < 1) {
                        minY = 1;
                    }

                    if (maxY > 248) {
                        maxY = 248;
                    }

                    if (minZ < 0) {
                        minZ = 0;
                    }

                    if (maxZ > 16) {
                        maxZ = 16;
                    }

                    for (int currX = minX; currX < maxX; ++currX) {
                        // Distance along the x-axis from the center (caveStart) of this ellipsoid.
                        // You can think of this value as (x/a), where a is the length of the ellipsoid's semi-axis in the x direction.
                        double xAxisDist = ((double) (currX + originChunkX * 16) + 0.5D - caveStartX) / xzOffset;

                        for (int currZ = minZ; currZ < maxZ; ++currZ) {
                            // Distance along the z-axis from the center (caveStart) of this ellipsoid.
                            // You can think of this value as (z/b), where b is the length of the ellipsoid's semi-axis in the z direction (same as a in this case).
                            double zAxisDist = ((double) (currZ + originChunkZ * 16) + 0.5D - caveStartZ) / xzOffset;

                            // Skip column if position not marked as valid
                            if (!validPositions[currX][currZ])
                                continue;

                            // Only operate on points within ellipse on XZ axis. Avoids unnecessary computation along y axis
                            if (xAxisDist * xAxisDist + zAxisDist * zAxisDist < 1.0D) {
                                for (int currY = maxY; currY > minY; --currY) {
                                    // Distance along the y-axis from the center (caveStart) of this ellipsoid.
                                    // You can think of this value as (y/c), where c is the length of the ellipsoid's semi-axis in the y direction.
                                    double yAxisDist = ((double) (currY - 1) + 0.5D - caveStartY) / yOffset;

                                    // Only operate on points within the ellipsoid.
                                    // This conditional is validating the current coordinate against the equation of the ellipsoid, that is,
                                    // (x/a)^2 + (z/b)^2 + (y/c)^2 <= 1.
                                    if (yAxisDist > -0.7D && xAxisDist * xAxisDist + yAxisDist * yAxisDist + zAxisDist * zAxisDist < 1.0D) {
                                        liquidBlock = liquidBlocks[CarverUtils.getLocal(currX)][CarverUtils.getLocal(currZ)];
                                            digBlock(worldIn, chunk, originChunkX, originChunkZ, currX, currY, currZ, liquidBlock, biomeMap, airCarvingMask, liquidCarvingMask);
                                    }
                                }
                            }
                        } 
                    }

                    if (comesFromRoom) {
                        break;
                    }
                }
            }
            startCounter++;
        }
    }

    private void digBlock(IWorld worldIn, IChunk chunkIn, int chunkX, int chunkZ, int localX, int y, int localZ, BlockState liquidBlockState, Map<Long, IGBiome> biomeMap, BitSet airCarvingMask, BitSet liquidCarvingMask) {
        // Check if already carved
        int bitIndex = localX | localZ << 4 | y << 8;
        if (airCarvingMask.get(bitIndex) || liquidCarvingMask.get(bitIndex)) {
            return;
        }
 
        BlockPos blockPos = new BlockPos(chunkX * 16 + localX, y, chunkZ * 16 + localZ);
        ColPos.MutableColPos mutableColPos = new ColPos.MutableColPos(blockPos);
 
        // Determine if cave is flooded at this location
        boolean flooded = isFloodedUndergroundEnabled && !isDebugVisualizerEnabled && biomeMap.get(mutableColPos.toLong()).getCategory() == Biome.Category.OCEAN;
        if (flooded) {
            // Cannot go above sea level
            if (y >= worldIn.getSeaLevel()) {
                return;
            }

            // Don't dig boundaries between flooded and unflooded openings.
            if (
                (isPosInWorld(mutableColPos.setPos(blockPos).move(Direction.EAST), world) && biomeMap.get(mutableColPos.setPos(blockPos).move(Direction.EAST).toLong()).getCategory() != Biome.Category.OCEAN) ||
                (isPosInWorld(mutableColPos.setPos(blockPos).move(Direction.WEST), world) && biomeMap.get(mutableColPos.setPos(blockPos).move(Direction.WEST).toLong()).getCategory() != Biome.Category.OCEAN) ||
                (isPosInWorld(mutableColPos.setPos(blockPos).move(Direction.NORTH), world) && biomeMap.get(mutableColPos.setPos(blockPos).move(Direction.NORTH).toLong()).getCategory() != Biome.Category.OCEAN) ||
                (isPosInWorld(mutableColPos.setPos(blockPos).move(Direction.SOUTH), world) && biomeMap.get(mutableColPos.setPos(blockPos).move(Direction.SOUTH).toLong()).getCategory() != Biome.Category.OCEAN)
            ) {
                return;
            }
        }
 
        // Carve block
        if (flooded) {
            CarverUtils.carveFloodedBlock(chunkIn, rand, new BlockPos.MutableBlockPos(blockPos), liquidBlockState, liquidAltitude, liquidCarvingMask);
        }
        else {
            CarverUtils.carveBlock(chunkIn, blockPos, liquidBlockState, this.liquidAltitude, this.isReplaceGravelEnabled, airCarvingMask);
        }
    }

    public void setWorld(IWorld worldIn) {
        this.world = worldIn;
    }
}