package com.igteam.immersivegeology.api.util;

import java.util.Random;

public class IGMathHelper
{
    private static Random RANDOM = new Random();

    public static int nextInt()
    {
        return RANDOM.nextInt();
    }

    public static int nextInt(int upper)
    {
        if(upper <= 0)
            return 0;
        return RANDOM.nextInt(upper);
    }

    public static int nextInt(int upper, int lower)
    {
        if(upper <= lower)
            return 0;
        return lower + RANDOM.nextInt(upper-lower);
    }
}
