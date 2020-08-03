package com.muddykat.noise;

import java.awt.Color;
import java.util.Random;
import java.util.function.LongFunction;

import com.igteam.immersivegeology.common.world.layer.IGLayerUtil;
import com.igteam.immersivegeology.common.world.layer.layers.AddIslandLayer;
import com.igteam.immersivegeology.common.world.layer.layers.AddLakeLayer;
import com.igteam.immersivegeology.common.world.layer.layers.AddOasisLayer;
import com.igteam.immersivegeology.common.world.layer.layers.BiomeLayer;
import com.igteam.immersivegeology.common.world.layer.layers.ElevationLayer;
import com.igteam.immersivegeology.common.world.layer.layers.IslandLayer;
import com.igteam.immersivegeology.common.world.layer.layers.MixRiverLayer;
import com.igteam.immersivegeology.common.world.layer.layers.OceanLayer;
import com.igteam.immersivegeology.common.world.layer.layers.RemoveOceanLayer;
import com.igteam.immersivegeology.common.world.layer.layers.RiverLayer;
import com.igteam.immersivegeology.common.world.layer.layers.ShoreLayer;
import com.igteam.immersivegeology.common.world.layer.layers.TemperateLayer;

import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.SmoothLayer;
import net.minecraft.world.gen.layer.VoroniZoomLayer;
import net.minecraft.world.gen.layer.ZoomLayer;

public class NoiseGenTester {
	
	public static final ImageUtil<IAreaFactory<LazyArea>> IMAGES = ImageUtil.noise(lazyAreaIAreaFactory -> {
        LazyArea area = lazyAreaIAreaFactory.make();
        return (left, right) -> area.getValue((int) left, (int) right);
    }, b -> b.scale(ImageUtil.Scales.NEAREST_INT).size(1000));

 	public void generate(long seed){
 		int attachedID = new Random().nextInt(1000);
 		
    	LongFunction<LazyAreaLayerContext> contextFactory = seedModifier -> new LazyAreaLayerContext(25, seed,
				seedModifier);
    	
    	IAreaFactory<LazyArea> mainLayer, riverLayer;
		// Ocean / Continents
		mainLayer = new IslandLayer(6).apply(contextFactory.apply(1000L));

		mainLayer = ZoomLayer.FUZZY.apply(contextFactory.apply(1001L), mainLayer);

		mainLayer = AddIslandLayer.NORMAL.apply(contextFactory.apply(1002L), mainLayer);

		mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1003L), mainLayer);

		mainLayer = AddIslandLayer.NORMAL.apply(contextFactory.apply(1004L), mainLayer);

		for(int i = 0; i < 2; i++)
		{
			mainLayer = AddIslandLayer.HEAVY.apply(contextFactory.apply(1005L+2*i), mainLayer);

			mainLayer = SmoothLayer.INSTANCE.apply(contextFactory.apply(1006L+2*i), mainLayer);
		}
		
		// Oceans and Continents => Elevation Mapping
		mainLayer = ElevationLayer.INSTANCE.apply(contextFactory.apply(1009L), mainLayer);

		mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1010L), mainLayer);
    	
		// Elevation Mapping => Rivers
		riverLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1011L), mainLayer);

		for(int i = 0; i < 6; i++)
		{
			riverLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1012L+i), riverLayer);
		}

		riverLayer = RiverLayer.INSTANCE.apply(contextFactory.apply(1018L), riverLayer);

		riverLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1019L), riverLayer);

		// Elevation Mapping => Biomes
		mainLayer = BiomeLayer.INSTANCE.apply(contextFactory.apply(1012L), mainLayer);
		
		for(int i = 0; i <= 4; i++){
			mainLayer = TemperateLayer.CASTLE.apply(contextFactory.apply(1012L), mainLayer);
			mainLayer = TemperateLayer.BISHOP.apply(contextFactory.apply(1012L), mainLayer);
		}
		
		IMAGES.color(this::tempColor).dimensions(1000);
    	IMAGES.draw("elevation_temperate_map_" + String.valueOf(attachedID), mainLayer);
		
		mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1013L), mainLayer);

		mainLayer = AddIslandLayer.NORMAL.apply(contextFactory.apply(1014L), mainLayer);

		mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1015L), mainLayer);

		mainLayer = RemoveOceanLayer.INSTANCE.apply(contextFactory.apply(1016L), mainLayer);

		mainLayer = OceanLayer.INSTANCE.apply(contextFactory.apply(1017L), mainLayer);

		//mainLayer = EdgeBiomeLayer.INSTANCE.apply(contextFactory.apply(1018L), mainLayer);

		mainLayer = AddLakeLayer.INSTANCE.apply(contextFactory.apply(1019L), mainLayer);

		mainLayer = AddOasisLayer.INSTANCE.apply(contextFactory.apply(1020L), mainLayer);

		for(int i = 0; i < 4; i++)
		{
			mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1021L), mainLayer);
		}

		mainLayer = ShoreLayer.INSTANCE.apply(contextFactory.apply(1023L), mainLayer);

		for(int i = 0; i < 2; i++)
		{
			mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1024L), mainLayer);
		}

		mainLayer = SmoothLayer.INSTANCE.apply(contextFactory.apply(1025L), mainLayer);

		mainLayer = MixRiverLayer.INSTANCE.apply(contextFactory.apply(1026L), mainLayer, riverLayer);

		IAreaFactory<LazyArea> areaFactoryActual = VoroniZoomLayer.INSTANCE.apply(contextFactory.apply(1029L),
				mainLayer);
		
		
		
		IMAGES.color(this::biomeColor).dimensions(1000);
    	IMAGES.draw("world_map_" + String.valueOf(attachedID), mainLayer);

    	IMAGES.color(this::tempColor).dimensions(1000);
    	IMAGES.draw("temperate_map_" + String.valueOf(attachedID), mainLayer);
    }	
 	
 	private Color tempColor(double value) {
 		int id = (int) value;
 		if(TemperateLayer.isBiomeFrozen(id)) return new Color(50,81,112);
 		if(TemperateLayer.isBiomeCold(id)) return new Color(0,171,250);
 		if(TemperateLayer.isBiomeTemperate(id)) return new Color(0,255,0);
 		if(TemperateLayer.isBiomeWarm(id)) return new Color(255,161,0);
 		if(TemperateLayer.isBiomeHot(id)) return new Color(255,0,0);
 		return Color.black;
 	} 
 	
    private Color biomeColor(double value)
    { 
        int id = (int) value;
        if (id == IGLayerUtil.DEEP_OCEAN) return new Color(50,81,112);
        if (id == IGLayerUtil.OCEAN) return new Color(0, 171, 250);
        if (id == IGLayerUtil.ARCTIC_DESERT) return new Color(140, 218, 255);
        if (id == IGLayerUtil.BADLANDS) return new Color(219, 113, 72);
        if (id == IGLayerUtil.PLAINS) return new Color(0,190,136);
        if (id == IGLayerUtil.CANYONS) return new Color(188,153,96);
        if (id == IGLayerUtil.DEEP_OCEAN_VOLCANIC) return new Color(120,96,188);
        if (id == IGLayerUtil.DESERT) return new Color(225,225,163);
        if (id == IGLayerUtil.FLOODED_MOUNTAINS) return new Color(127, 144, 198);
        if (id == IGLayerUtil.FROZEN_MOUNTAINS) return new Color(57,219,184);
        if (id == IGLayerUtil.GLACIER) return new Color(155,221,255);
        if (id == IGLayerUtil.HILLS) return new Color(85,190,50);
        if (id == IGLayerUtil.LAKE) return new Color(41,134,188);
        if (id == IGLayerUtil.LOW_CANYONS) return new Color(188,116,100);
        if (id == IGLayerUtil.LOWLANDS) return new Color(70,137,118);
        if (id == IGLayerUtil.LUSH_MOUNTAINS) return new Color(118,173,116);
        if (id == IGLayerUtil.MOUNTAIN_DUNES) return new Color(173,167,126);
        if (id == IGLayerUtil.OASIS) return new Color(113,182,255);
        if (id == IGLayerUtil.OCEAN_EDGE) return new Color(56,124,183);
        if (id == IGLayerUtil.OLD_MOUNTAINS) return new Color(127,118,103);
        if (id == IGLayerUtil.PLATEAU) return new Color(92,137,82);
        if (id == IGLayerUtil.RIVER) return new Color(4,174,255);
        if (id == IGLayerUtil.ROLLING_HILLS) return new Color(112,170,68);
        if (id == IGLayerUtil.MOUNTAINS_EDGE) return new Color(183,183,180);
        if (id == IGLayerUtil.MOUNTAINS) return new Color(130,129,127);
        if (id == IGLayerUtil.SHORE) return new Color(255,236,142);
        if (id == IGLayerUtil.STONE_SHORE) return new Color(181,178,181);
        return Color.black;
    }
}
