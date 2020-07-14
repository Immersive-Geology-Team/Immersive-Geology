package com.igteam.immersivegeology.common.world.noise;

final class Vec2
{
	final float x, y;

	Vec2(float x, float y)
	{
		this.x = x;
		this.y = y;
	}

	final float dot(float x, float y)
	{
		return this.x*x+this.y*y;
	}
}