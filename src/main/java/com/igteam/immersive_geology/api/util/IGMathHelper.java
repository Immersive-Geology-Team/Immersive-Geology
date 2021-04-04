package com.igteam.immersive_geology.api.util;

import com.sun.javafx.geom.Vec3d;
import net.minecraft.util.math.MathHelper;

import java.util.*;

public class IGMathHelper extends MathHelper
{
	private static Random RANDOM = new Random();

	public static double abs(double value)
	{
		return value >= 0 ? value : -value;
	}

	public static long abs(long value)
	{
		return value >= 0 ? value : -value;
	}

	/**
	 * @return A random integer between Integer.MIN_VALUE and Integer.MAX_VALUE
	 * @Author CrimsonTwilight
	 * <p>
	 * Used to get a random Integer between Integer.MIN_VALUE and Integer.MAX_VALUE.
	 * </p>
	 */
	public static int randInt()
	{
		return RANDOM.nextInt()-RANDOM.nextInt();
	}

	/**
	 * @param bound The bound of the random value. Can be positive and negative.
	 * @return A random integer between 0 and Bound. Can be both positive and negative.
	 * @Author CrimsonTwilight
	 * <p>
	 * Used to get a random Integer zero and your bound. Bound can be positive and negative.
	 * </p>
	 */
	public static int randInt(int bound)
	{
		if(bound==0)
			return 0;
		if(bound < 0)
			return -RANDOM.nextInt(Math.abs(bound));
		return RANDOM.nextInt(Math.abs(bound));
	}

	/**
	 * @param x Bound 1 Can be positive and negative.
	 * @param y Bound 2 Can be positive and negative.
	 * @return A random integer between lower and upper. Can be positive and negative.
	 * @Author CrimsonTwilight
	 * <p>
	 * Used to get a random Integer between two bounds
	 * </p>
	 */
	public static int randInt(int x, int y)
	{
		if(x==y)
			return 0;
		if(x < y)
			return x+RANDOM.nextInt(y-x);
		return y+RANDOM.nextInt(x-y);
	}

	/**
	 * @param num  The number to get the root from
	 * @param root The root
	 * @return The root of num
	 * @Author Pabilo8
	 * <p>
	 * Used to get the xth root of a number
	 * </p>
	 */
	public static double root(double num, double root)
	{
		double d = Math.pow(num, 1.0/root);
		long rounded = Math.round(d);
		return Math.abs(rounded-d) < 0.00000000000001?rounded: d;
	}

	/**
	 * @param num The number to get the root from
	 * @return The square root of num
	 * @Author CrimsonTwilight
	 * <p>
	 * Used to get the square root of a number
	 * </p>
	 */
	public static double root(double num)
	{
		double d = Math.pow(num, 0.5);
		long rounded = Math.round(d);
		return Math.abs(rounded-d) < 0.00000000000001?rounded: d;
	}


	/**
	 * @param velocity  original velocity
	 * @param drag drag factor
	 * @return drag applied velocity
	 * @Author CrimsonTwilight
	 * <p>
	 * Used to get the drag applied velocity
	 * </p>
	 */
	public static double apply_drag(double velocity, double drag)
	{
		return root(Math.pow(velocity, 2) * drag * 0.5) * (abs(velocity)/velocity);
	}


	/**
	 * @param velocity  original velocity
	 * @param drag drag factor
	 * @return drag applied velocity
	 * @Author CrimsonTwilight
	 * <p>
	 * Used to get the drag applied velocity
	 * </p>
	 */
	public static double apply_drag(double velocity, double drag, double area)
	{
		return root(Math.pow(velocity, 2) * drag * 0.5 * area) * (abs(velocity)/velocity);
	}


	/**
	 * @param height height above sea level
	 * @return barometric air pressure in Pascal
	 * @Author CrimsonTwilight
	 * <p>
	 * Used to get the air pressure at a certain height above sea level
	 * </p>
	 */
	public static double barometric_air_pressure(double height)
	{
		return 101.325 * Math.exp(-0.00012*height) * 1000;
	}


	/**
	 * @param pressure pressure on the air
	 * @return air density at 20 C and pressure Pas
	 * @Author CrimsonTwilight
	 * <p>
	 * Used to get the air density at a certain pressure and 20 Celsius
	 * </p>
	 */
	public static double air_density(double pressure)
	{
		return pressure/(287*(20+273.15));
	}


	/**
	 * @param pressure pressure on the air
	 * @param temperature temperature in Kelvin
	 * @return air density at 20 C and pressure Pas
	 * @Author CrimsonTwilight
	 * <p>
	 * Used to get the air density at a certain pressure and 20 Celsius
	 * </p>
	 */
	public static double air_density(double pressure, float temperature)
	{
		return pressure/(287*temperature);
	}


	/**
	 * @param velocity  original velocity
	 * @param drag drag factor
	 * @return drag applied velocity
	 * @Author CrimsonTwilight
	 * <p>
	 * Used to get the drag applied velocity
	 * </p>
	 */
	public static double apply_drag(double velocity, double drag, double area, double density)
	{
		return -root(Math.pow(velocity, 2) * drag * 0.5 * area * density) * (abs(velocity)/velocity);
	}


	//CMY is actually reverse RGB (i tested that out in GIMP ^^)... adding black makes colour darker (less means lighter)
	//Black is actually the limit of darkness (less value - darker) in RGB
	//But because everything is reverse, we get the color with greater value.

	/**
	 * @param red   The amount of red, between 0 and 255
	 * @param green The amount of green, between 0 and 255
	 * @param blue  The amount of blue, between 0 and 255
	 * @return The CMYK version of the RGB input
	 * @Author Pabilo8 & CrimsonTwilight
	 * <p>
	 * Used to turn RGB colors into CMYK colors
	 * </p>
	 */
	public static int[] rgbToCmyk(int red, int green, int blue)
	{
		if(red > 255) red = 255;
		if(red < 0) red = 0;
		if(green > 255) green = 255;
		if(green < 0) green = 0;
		if(blue > 255) blue = 255;
		if(blue < 0) blue = 0;
		return new int[]{255-red, 255-green, 255-blue, 255-Math.min(red, Math.max(green, blue))};
	}

	/**
	 * @param cyan    The amount of cyan, between 0 and 255
	 * @param magenta The amount of magenta, between 0 and 255
	 * @param yellow  The amount of yellow, between 0 and 255
	 * @param black   The amount of black, between 0 and 255
	 * @return The RGB version of the CMYK input
	 * @Author Pabilo8 & CrimsonTwilight
	 * <p>
	 * Used to turn CMYK colors into RGB colors
	 * </p>
	 */
	public static int[] cmykToRgb(int cyan, int magenta, int yellow, int black)
	{
		if(cyan > 255) cyan = 255;
		if(cyan < 0) cyan = 0;
		if(magenta > 255) magenta = 255;
		if(magenta < 0) magenta = 0;
		if(yellow > 255) yellow = 255;
		if(yellow < 0) yellow = 0;
		if(black > 255) black = 255;
		if(black < 0) black = 0;
		return new int[]{Math.min(255-black, 255-cyan), Math.min(255-black, 255-magenta), Math.min(255-black, 255-yellow)};
	}

	/**
	 * @param x1     Edge one in the x-axle of the rectangle
	 * @param x2     Edge two in the x-axle of the rectangle
	 * @param y1     Edge one in the y-axle of the rectangle
	 * @param y2     Edge two in the y-axle of the rectangle
	 * @param pointX The x value of the point
	 * @param pointY The y value of the point
	 * @return If the point is in the rectangle
	 * @Author Pabilo8 & CrimsonTwilight
	 * <p>
	 * Used to see if a point is inside a rectangular area
	 * </p>
	 */
	public static boolean isPointInRectangle(double x1, double y1, double x2, double y2, double pointX, double pointY)
	{
		double xBottom = x2 > x1?x1: x2;
		double xTop = x2 > x1?x2: x1;
		double yBottom = y2 > y1?y1: y2;
		double yTop = y2 > y1?y2: y1;
		return pointX >= xBottom&&pointX <= xTop&&pointY >= yBottom&&pointY <= yTop;
	}

	/**
	 * @param offset (length) of the vector
	 * @param yaw    of the vector
	 * @param pitch  of the vector
	 * @return direction transformed position
	 * @author Pabilo8
	 * <p>
	 * Used to calculate 3D vector offset in a direction
	 * </p>
	 */
	public static Vec3d offsetPosDirection(float offset, double yaw, double pitch)
	{
		double yy = (MathHelper.sin((float)pitch)*offset);
		double true_offset = (MathHelper.cos((float)pitch)*offset);

		double xx = (MathHelper.sin((float)yaw)*true_offset);
		double zz = (MathHelper.cos((float)yaw)*true_offset);

		Vec3d vec = new Vec3d(xx, yy, zz);
		return vec;
	}

	/**
	 * @param rgb An Integer based version of RGB
	 * @return A float array of Red, Green, Blue
	 * @Author Pabilo8
	 * <p>
	 * Used to turn a single RGB integer into a float array of Red, Green, Blue
	 * </p>
	 */
	public static float[] rgbIntToRGB(int rgb)
	{
		float r = (rgb/256/256%256)/255f;
		float g = (rgb/256%256)/255f;
		float b = (rgb%256)/255f;
		return new float[]{r, g, b};
	}

	/**
	 * @param distance the distance of the target
	 * @param height   height difference
	 * @param force    The Force of the projectile
	 * @param gravity  The loss of height per calculation
	 * @param drag     The Drag Constant of the projectile
	 * @return Angle to fire ballistic from
	 * @Author Pabilo8 & CrimsonTwilight
	 * <p>
	 * Used to calculate best angle to fire projectile from
	 * </p>
	 */
	public static float calculateBallisticAngle(double distance, double height, float force, double gravity, double drag)
	{
		double bestAngle = 0;
		double bestDistance = Float.MAX_VALUE;
		if(gravity==0D)
		{
			return 90F-(float)(Math.atan(height/distance)*180F/Math.PI);
		}
		// simulate the trajectory for angles from 45 to 90 degrees,
		// returning the angle which lands the projectile closest to the target distance
//        for (double i = Math.PI * 0.25D; i < Math.PI * 0.50D; i += 0.001D) {
		for(double i = Math.PI*0.01D; i < Math.PI*0.5D; i += 0.01D)
		{
			double motionX = MathHelper.cos((float)i)*force;// calculate the x component of the vector
			double motionY = MathHelper.sin((float)i)*force;// calculate the y component of the vector
			double posX = 0;
			double posY = 0;
			while(posY > height||motionY > 0)
			{ // simulate movement, until we reach the y-level required
				motionY -= gravity;
				motionX += apply_drag(motionX, drag);
				motionY += apply_drag(motionY, drag);
				posX += motionX;
				posY += motionY;
			}
			double distanceToTarget = Math.abs(distance-posX);
			if(distanceToTarget < bestDistance)
			{
				bestDistance = distanceToTarget;
				bestAngle = i;
			}
		}
		return 90F-(float)(bestAngle*180D/Math.PI);
	}

	/**
	 * @param distance   the distance of the target
	 * @param height     height difference
	 * @param mass       Mass of the projectile
	 * @param velocity   Velocity of the projectile
	 * @param gravity    Gravity constant of the dimension
	 * @param dragFactor The drag factor of the projectile
	 * @return Angle to fire ballistic projectile from
	 * @Author Pabilo8 & CrimsonTwilight
	 * <p>
	 * Used to calculate best angle to fire projectile from
	 * </p>
	 */
	public static float calculateBallisticAngle(double distance, double height, float mass, float velocity, double gravity, double dragFactor)
	{
		double bestAngle = 0;
		double bestDistance = Float.MAX_VALUE;
		if(gravity==0D)
		{
			return 90F-(float)(Math.atan(height/distance)*180F/Math.PI);
		}
		// simulate the trajectory for angles from 45 to 90 degrees,
		// returning the angle which lands the projectile closest to the target distance
		//        for (double i = Math.PI * 0.25D; i < Math.PI * 0.50D; i += 0.001D) {
		for(double i = Math.PI*0.01D; i < Math.PI*0.5D; i += 0.01D)
		{
			double vX = MathHelper.cos((float)i)*velocity;// calculate the x component of the vector
			double vY = MathHelper.sin((float)i)*velocity;// calculate the y component of the vector
			double posX = 0;
			double posY = 0;
			while(posY > height||vY > 0)
			{ // simulate movement, until we reach the y-level required
				vY -= mass*gravity;
				vX += apply_drag(vX, dragFactor);
				vY += apply_drag(vY, dragFactor);
				posX += vX;
				posY += vY;
			}
			double distanceToTarget = Math.abs(distance-posX);
			if(distanceToTarget < bestDistance)
			{
				bestDistance = distanceToTarget;
				bestAngle = i;
			}
		}
		return 90F-(float)(bestAngle*180D/Math.PI);
	}

	/**
	 * @param distance   the distance of the target
	 * @param height     height difference
	 * @param mass       Mass of the projectile
	 * @param velocity   Velocity of the projectile
	 * @param gravity    Gravity constant of the dimension
	 * @param dragFactor The drag factor of the projectile
	 * @return Momentum of ballistic projectile
	 * @Author Pabilo8 & CrimsonTwilight
	 * <p>
	 * Used to calculate momentum of projectile from best angle
	 * </p>
	 */
	public static float calculateBallisticMomentum(double distance, double height, float mass, float velocity, double gravity, double dragFactor)
	{
		double bestAngle = 0;
		double bestDistance = Float.MAX_VALUE;
		double posX = 0;
		double posY = 0;
		double vX = 0;
		double vY = 0;
		if(gravity==0D)
		{
			return 90F-(float)(Math.atan(height/distance)*180F/Math.PI);
		}
		// simulate the trajectory for angles from 45 to 90 degrees,
		// returning the angle which lands the projectile closest to the target distance
		//        for (double i = Math.PI * 0.25D; i < Math.PI * 0.50D; i += 0.001D) {
		for(double i = Math.PI*0.01D; i < Math.PI*0.5D; i += 0.01D)
		{
			vX = MathHelper.cos((float)i)*velocity;// calculate the x component of the vector
			vY = MathHelper.sin((float)i)*velocity;// calculate the y component of the vector
			while(posY > height||vY > 0)
			{ // simulate movement, until we reach the y-level required
				vY -= mass*gravity;
				vX += apply_drag(vX, dragFactor);
				vY += apply_drag(vY, dragFactor);
				posX += vX;
				posY += vY;
			}
			double distanceToTarget = Math.abs(distance-posX);
			if(distanceToTarget < bestDistance)
			{
				bestDistance = distanceToTarget;
				bestAngle = i;
			}
		}
		velocity = (float)IGMathHelper.root(Math.pow(vX, 2)*Math.pow(vY, 2));
		return mass*velocity;
	}

	/**
	 * @param list1
	 * @param list2
	 * @return Intersection of List1 and List2
	 * @author Muddykat
	 */
	public static List<Integer> intersection(List<Integer> list1, List<Integer> list2)
	{
		List<Integer> list = new ArrayList<Integer>();

		for(Integer t : list1)
		{
			if(list2.contains(t))
			{
				list.add(t);
			}
		}

		return list;
	}

	/**
	 * @param list1
	 * @param list2
	 * @return Union of List1 and List2
	 * @author Muddykat
	 */
	public static List<Integer> union(List<Integer> list1, List<Integer> list2)
	{
		Set<Integer> set = new HashSet<Integer>();

		set.addAll(list1);
		set.addAll(list2);

		return new ArrayList<Integer>(set);
	}
}
