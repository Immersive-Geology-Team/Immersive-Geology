package com.igteam.immersivegeology.common.world.climate;

import java.util.Random;

import static com.igteam.immersivegeology.common.world.chunk.ChunkGeneratorImmersiveOverworld.SEA_LEVEL;


public class ClimateHelper {	
	 private static final Random RANDOM = new Random();
	 
	    /**
	     * @param y the y level
	     * @return a value between 0 and 17.822
	     */
	    public static float heightFactor(int y)
	    {
	        if (y > SEA_LEVEL)
	        {
	            // This is much simpler and works just as well
	            float scale = (y - SEA_LEVEL) * 0.16225f;
	            if (scale > 17.822f)
	            {
	                scale = 17.822f;
	            }
	            return scale;
	        }
	        return 0;
	    }

	    /**
	     * Range -32 to 35 
	     *
	     * @param regionalTemp The base temp for the current location
	     * @return z chunk coord
	     */
	    public static float monthFactor(float regionalTemp, int z)
	    {
	        return monthFactor(regionalTemp, 29.5f, z); 
	    }

	    public static float monthFactor(float regionalTemp, float monthTempModifier, int z)
	    {
	        return (41f - monthTempModifier * 1.1f * (1 - 0.8f * latitudeFactor(z))) + regionalTemp;
	    }

	    /**
	     * Range 0 - 1
	     *
	     * @param chunkZ the chunk Z position (in block coordinates)
	     * @return the latitude factor for temperature calculation
	     */
	    public static float latitudeFactor(int chunkZ)
	    {
	        int tempRange = 40_000;
	        return 0.5f + 0.5f * 1 * (float) Math.sin(Math.PI * chunkZ / tempRange);
	    }
	    
	    private ClimateHelper() {}
}
