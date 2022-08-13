package igteam.immersive_geology.client.render.helper;

import net.minecraft.util.math.MathHelper;

import java.util.Arrays;

public class IGRenderHelper {
    public static float piecewiseLerp(float[] Xs, float[] Ys, float x){
        int index = Arrays.binarySearch(Xs, x);
        // If the index is non-negative
        // an exact match has been found!
        if (index >= 0)
            return Ys[index];
        // If the index is negative, it represents the bitwise
        // complement of the next larger element in the array.
        index = ~index;
        // index == 0           => result smaller than Ys[0]
        if (index == 0)
            return Ys[0];
        // index == Ys.Length   => result greater than Ys[Ys.Length-1]
        if (index == Ys.length)
            return Ys[Ys.length - 1];
        // else                 => result between Ys[index-1] and Ys[index]
        // Lerp
        return lerp
                (
                        Xs[index - 1], Xs[index],
                        Ys[index - 1], Ys[index],
                        x
                );
    }
    public static float lerp (float x0, float x1, float y0, float y1, float x)
    {
        float d = x1 - x0;
        if (d == 0)
            return (y0 + y1) / 2;
        return y0 + (x - x0) * (y1 - y0) / d;
    }
}
