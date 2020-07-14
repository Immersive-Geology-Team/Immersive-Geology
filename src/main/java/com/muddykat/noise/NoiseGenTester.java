package com.muddykat.noise;

import static com.igteam.immersivegeology.common.world.gen.config.ImmersiveGenerationSettings.SEA_LEVEL;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.igteam.immersivegeology.common.world.noise.INoise2D;
import com.igteam.immersivegeology.common.world.noise.OpenSimplexNoise;
import com.igteam.immersivegeology.common.world.noise.SimplexNoise2D;

public class NoiseGenTester {
	

	 public static void greyWriteImage(double[][] data){
	        //this takes an array of doubles between 0 and 1 and generates a grey scale image from them

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
	            float THRESHOLD = 0.3f;
	            if(data[x][y] > THRESHOLD) {
		            Color col=new Color((float)data[x][y],(float)data[x][y],(float)data[x][y]); 
		            image.setRGB(x, y, col.getRGB());
	            } else {
	            	 Color col=new Color(255,0,0); 
			         image.setRGB(x, y, col.getRGB());
	            }
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
	        int chunkAmount = 30;
	        long seed = 100;
	        
	        INoise2D noise = new SimplexNoise2D(seed).flattened(0.4f, 1f).spread(0.001f);
	        
	        INoise2D noise2 = new SimplexNoise2D(seed).octaves(3, 0.95f).spread(0.005f).flattened(0f, 1);
	       
	        double[][] result=new double[16 * chunkAmount][16 * chunkAmount];
 
	        for(int i=0;i< (16 * chunkAmount);i++){
	            for(int j=0;j<(16 * chunkAmount);j++){
	                int xp=(int)(iStart+i*((iEnd-iStart)/(16 * chunkAmount)));
	                int yp=(int)(jStart+j*((jEnd-jStart)/(16 * chunkAmount)));
	                
	                result[i][j]=noise2.sub(noise).noise(xp, yp);
	            }    
	        }
	            
	        greyWriteImage(result);
	    }
}
