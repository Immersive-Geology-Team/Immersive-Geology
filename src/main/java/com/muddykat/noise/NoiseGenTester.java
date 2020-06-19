package com.muddykat.noise;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.igteam.immersivegeology.common.world.biome.IGBiome;
import com.igteam.immersivegeology.common.world.biome.biomes.OceanBiome;
import com.igteam.immersivegeology.common.world.noise.OpenSimplexNoise;
import com.igteam.immersivegeology.common.world.noise.SimplexNoise2D;

public class NoiseGenTester {
	 public static void greyWriteImage(double[][] data){
	        //this takes and array of doubles between 0 and 1 and generates a grey scale image from them

	        BufferedImage image = new BufferedImage(data.length,data[0].length, BufferedImage.TYPE_INT_RGB);

	        for (int y = 0; y < data[0].length; y++)
	        {
	          for (int x = 0; x < data.length; x++)
	          {
	            if (data[x][y]>1){
	                data[x][y]=1;
	            }
	            if (data[x][y]<0){
	                data[x][y]=0;
	            }
	              Color col=new Color((float)data[x][y],(float)data[x][y],(float)data[x][y]); 
	            image.setRGB(x, y, col.getRGB());
	          }
	        }

	        try {
	            // retrieve image
	            File outputfile = new File("saved.png");
	            outputfile.createNewFile();

	            ImageIO.write(image, "png", outputfile);
	        } catch (IOException e) {
	            //o no!
	        }
	    }


	    public static void main(String args[]){
	    	double iStart=0;
	        double iEnd=500;
	        double jStart=0;
	        double jEnd=500;
	        long seed = 1020;
	        OpenSimplexNoise noise = new OpenSimplexNoise(seed);
	        double[][] result=new double[1000][1000];

	        for(int i=0;i<1000;i++){
	            for(int j=0;j<1000;j++){
	                int xp=(int)(iStart+i*((iEnd-iStart)/1000));
	                int yp=(int)(jStart+j*((jEnd-jStart)/1000));
	                
	                result[i][j]=0.5*(1+noise.eval(xp, yp));
	            } 
	        }

	        greyWriteImage(result);
	    }
}
