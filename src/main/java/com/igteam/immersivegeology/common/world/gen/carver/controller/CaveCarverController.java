package com.igteam.immersivegeology.common.world.gen.carver.controller;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import com.igteam.immersivegeology.common.world.IGLib;
import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.gen.carver.CarverNoiseRange;
import com.igteam.immersivegeology.common.world.gen.carver.CaveCarver;
import com.igteam.immersivegeology.common.world.gen.carver.ICarver;
import com.igteam.immersivegeology.common.world.gen.carver.VanillaCaveCarver;
import com.igteam.immersivegeology.common.world.gen.carver.builder.CaveCarverBuilder;
import com.igteam.immersivegeology.common.world.gen.carver.builder.VanillaCaveCarverBuilder;
import com.igteam.immersivegeology.common.world.gen.carver.util.CaveType;
import com.igteam.immersivegeology.common.world.gen.carver.util.ColPos;
import com.igteam.immersivegeology.common.world.noise.FastNoise;
import com.igteam.immersivegeology.common.world.noise.ybc.NoiseColumn;

import static com.igteam.immersivegeology.common.world.gen.carver.util.CarverUtils.isPosInWorld;


import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class CaveCarverController {
	private IWorld world;
	private VanillaCaveCarver surfaceCaveCarver;
	private FastNoise caveRegionController;
	private List<CarverNoiseRange> noiseRanges = new ArrayList<>();

    private boolean isOverrideSurfaceDetectionEnabled;
    private boolean isSurfaceCavesEnabled;
    private boolean isFloodedUndergroundEnabled;
	
	public CaveCarverController(IWorld worldIn) {
        this.world = worldIn;
        this.isOverrideSurfaceDetectionEnabled = false;
        this.isSurfaceCavesEnabled = true;
        this.isFloodedUndergroundEnabled = true;
        this.surfaceCaveCarver = new VanillaCaveCarverBuilder()
            .bottomY(40)
            .topY(128)
            .density(17)
            .liquidAltitude(12)
            .replaceGravel(true)
            .floodedUnderground(true)
            .build();

        // Configure cave region controller, which determines what type of cave should be
        // carved in any given region
        float caveRegionSize = calcCaveRegionSize("Small", 0.008f);
        this.caveRegionController = new FastNoise();
        this.caveRegionController.SetSeed((int)worldIn.getSeed() + 222);
        this.caveRegionController.SetFrequency(caveRegionSize);
        this.caveRegionController.SetNoiseType(FastNoise.NoiseType.Cellular);
        this.caveRegionController.SetCellularDistanceFunction(FastNoise.CellularDistanceFunction.Natural);

        // Initialize all carvers using config options
        List<ICarver> carvers = new ArrayList<>();
        // Type 1 caves
        carvers.add(new CaveCarverBuilder(worldIn.getSeed())
            .ofTypeFromConfig(CaveType.CUBIC)
            .build()
        ); 
        // Type 2 caves
        carvers.add(new CaveCarverBuilder(worldIn.getSeed())
            .ofTypeFromConfig(CaveType.SIMPLEX)
            .build()
        );
        
        // Vanilla caves
        carvers.add(new VanillaCaveCarverBuilder()
            .bottomY(8)
            .topY(128)
            .density(9)
            .priority(0)
            .liquidAltitude(12)
            .replaceGravel(true)
            .floodedUnderground(true)
            .build());

        // Remove carvers with no priority
        carvers.removeIf(carver -> carver.getPriority() == 0);

        // Initialize vars for calculating controller noise thresholds
        float maxPossibleNoiseThreshold = 100f * .01f * 2 - 1;
        int totalPriority = carvers.stream().map(ICarver::getPriority).reduce(0, Integer::sum);
        float totalRangeLength = maxPossibleNoiseThreshold - -1f;
        float currNoise = -1f;

        for (ICarver carver : carvers) {
            float noiseRangeLength = (float)carver.getPriority() / totalPriority * totalRangeLength;
            float rangeTop = currNoise + noiseRangeLength;
            CarverNoiseRange range = new CarverNoiseRange(currNoise, rangeTop, carver);
            currNoise = rangeTop;
            noiseRanges.add(range);
        }
	}
    public void carveChunk(IChunk chunk, int chunkX, int chunkZ, int[][] surfaceAltitudes, BlockState[][] liquidBlocks, Map<Long, IGBiome> biomeMap, BitSet airCarvingMask, BitSet liquidCarvingMask) {
        // Prevent unnecessary computation if caves are disabled
        if (noiseRanges.size() == 0 && !isSurfaceCavesEnabled) {
            return;
        }

        ColPos.MutableColPos mutablePos = new ColPos.MutableColPos();
        boolean flooded;

        // Flag to keep track of whether or not we've already carved vanilla caves for this chunk, since
        // vanilla caves operate on a chunk-by-chunk basis rather than by column
        boolean shouldCarveVanillaCaves = false;

        // Since vanilla caves carve by chunk and not by column, we store an array
        // indicating which x-z coordinates are valid to be carved in
        boolean[][] validPositions = new boolean[16][16];

        // Break into subchunks for noise interpolation
        for (int subX = 0; subX < 16 / IGLib.SUB_CHUNK_SIZE; subX++) {
            for (int subZ = 0; subZ < 16 / IGLib.SUB_CHUNK_SIZE; subZ++) {
                int startX = subX * IGLib.SUB_CHUNK_SIZE;
                int startZ = subZ * IGLib.SUB_CHUNK_SIZE;
                int endX = startX + IGLib.SUB_CHUNK_SIZE - 1;
                int endZ = startZ + IGLib.SUB_CHUNK_SIZE - 1;
                BlockPos startPos = new BlockPos(chunkX * 16 + startX, 1, chunkZ * 16 + startZ);
                BlockPos endPos = new BlockPos(chunkX * 16 + endX, 1, chunkZ * 16 + endZ);

                noiseRanges.forEach(range -> range.setNoiseCube(null));

                // Get max height in subchunk. This is needed for calculating the noise cube
                int maxHeight = 0;
                if (!isOverrideSurfaceDetectionEnabled) { // Only necessary if we aren't overriding surface detection
                    for (int x = startX; x < endX; x++) {
                        for (int z = startZ; z < endZ; z++) {
                            maxHeight = Math.max(maxHeight, surfaceAltitudes[x][z]);
                        }
                    }
                    for (CarverNoiseRange range : noiseRanges) {
                        maxHeight = Math.max(maxHeight, range.getCarver().getTopY());
                    }
                }

                // Offset within subchunk
                for (int offsetX = 0; offsetX < IGLib.SUB_CHUNK_SIZE; offsetX++) {
                    for (int offsetZ = 0; offsetZ < IGLib.SUB_CHUNK_SIZE; offsetZ++) {
                        int localX = startX + offsetX;
                        int localZ = startZ + offsetZ;
                        ColPos colPos = new ColPos(chunkX * 16 + localX, chunkZ * 16 + localZ);

                        flooded = isFloodedUndergroundEnabled && biomeMap.get(colPos.toLong()).getCategory() == Biome.Category.OCEAN;
                        if (flooded) {
                            if (
                                (isPosInWorld(mutablePos.setPos(colPos).move(Direction.EAST), world) && biomeMap.get(mutablePos.setPos(colPos).move(Direction.EAST).toLong()).getCategory() != Biome.Category.OCEAN) ||
                                (isPosInWorld(mutablePos.setPos(colPos).move(Direction.WEST), world) && biomeMap.get(mutablePos.setPos(colPos).move(Direction.WEST).toLong()).getCategory() != Biome.Category.OCEAN) ||
                                (isPosInWorld(mutablePos.setPos(colPos).move(Direction.NORTH), world) && biomeMap.get(mutablePos.setPos(colPos).move(Direction.NORTH).toLong()).getCategory() != Biome.Category.OCEAN) ||
                                (isPosInWorld(mutablePos.setPos(colPos).move(Direction.SOUTH), world) && biomeMap.get(mutablePos.setPos(colPos).move(Direction.SOUTH).toLong()).getCategory() != Biome.Category.OCEAN)
                            ) {
                                continue;
                            }
                        }

                        int surfaceAltitude = surfaceAltitudes[localX][localZ];
                        BlockState liquidBlock = liquidBlocks[localX][localZ];

                        // Get noise values used to determine cave region
                        float caveRegionNoise = caveRegionController.GetNoise(colPos.getX(), colPos.getZ());

                        // Carve cave using matching carver
                        for (CarverNoiseRange range : noiseRanges) {
                            if (!range.contains(caveRegionNoise)) {
                                continue;
                            }
                            if (range.getCarver() instanceof CaveCarver) {
                                CaveCarver carver = (CaveCarver) range.getCarver();
                                int bottomY = carver.getBottomY();
                                int topY = Math.min(surfaceAltitude, carver.getTopY());
                                if (isOverrideSurfaceDetectionEnabled) {
                                    topY = carver.getTopY();
                                    maxHeight = carver.getTopY();
                                }
                                if (range.getNoiseCube() == null) {
                                    range.setNoiseCube(carver.getNoiseGen().interpolateNoiseCube(startPos, endPos, bottomY, maxHeight));
                                }
                                NoiseColumn noiseColumn = range.getNoiseCube().get(offsetX).get(offsetZ);
                                carver.carveColumn(chunk, colPos, topY, noiseColumn, liquidBlock, flooded, flooded ? liquidCarvingMask : airCarvingMask);
                                break;
                            }
                            else if (range.getCarver() instanceof VanillaCaveCarver) {
                                validPositions[localX][localZ] = true;
                                shouldCarveVanillaCaves = true;
                            }
                        }
                    }
                }
            }
        }
        if (shouldCarveVanillaCaves) {
            VanillaCaveCarver carver = null;
            for (CarverNoiseRange range : noiseRanges) {
                if (range.getCarver() instanceof VanillaCaveCarver) {
                    carver = (VanillaCaveCarver) range.getCarver();
                    break;
                }
            }
            if (carver != null) {
                carver.generate(world, chunkX, chunkZ, chunk, true, liquidBlocks, biomeMap, validPositions, airCarvingMask, liquidCarvingMask);
            }
        }
        // Generate surface caves if enabled
        if (isSurfaceCavesEnabled) {
            surfaceCaveCarver.generate(world, chunkX, chunkZ, chunk, false, liquidBlocks, biomeMap, airCarvingMask, liquidCarvingMask);
        }
    }

    /**
     * @return frequency value for cave region controller
     */
    private float calcCaveRegionSize(String caveRegionSize, float caveRegionCustomSize) {
        switch (caveRegionSize) {
            case "Small":
                return .008f;
            case "Large":
                return .0032f;
            case "ExtraLarge":
                return .001f;
            case "Custom":
                return caveRegionCustomSize;
            case "Medium":
            default:
                return .005f;
        }
    }

    public void setWorld(IWorld worldIn) {
        this.world = worldIn;
        this.surfaceCaveCarver.setWorld(worldIn);
        for (CarverNoiseRange range : noiseRanges) {
            if (range.getCarver() instanceof VanillaCaveCarver) {
                ((VanillaCaveCarver)range.getCarver()).setWorld(worldIn);
                break;
            }
        }
    }
}
