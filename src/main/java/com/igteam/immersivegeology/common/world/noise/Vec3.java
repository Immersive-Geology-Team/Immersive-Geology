package com.igteam.immersivegeology.common.world.noise;


final class Vec3
{
    final float x, y, z;

    Vec3(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    final float dot(float x, float y, float z)
    {
        return this.x * x + this.y * y + this.z * z;
    }
}
